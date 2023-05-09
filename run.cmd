@echo off

rem This script is used for manipulating all projects in this repository, on
rem a per-project and global level (all projects at once).



rem ---------------------------------- CONFIG ----------------------------------

rem Microservices are defined here. Each microservice must be placed in the
rem directory with the exact same name:

set services[0]="main"

rem ----------------------------------------------------------------------------





goto :main

rem Helper function for building a Docker image of a microservice.
:docker-service-build
    setlocal
        echo Building project "%~1"...
        set JAVA_HOME=%projectHome%\lib\%jdk%
        cd %~1
        call mvnw spotless:apply > nul
        cd ..
        call docker build -t %~1 -f ./docker/%~1.Dockerfile .
        call docker tag %~1 harbor.k8s.elab.rs/banka-2/%~1
    endlocal
exit /B 0



rem Helper function for executing a Docker image.
:docker-service-exec
    setlocal
        if "%~2" == "dev" (
            call docker run --rm -d --name %~1 ^
                --network bank2_net --entrypoint="" ^
                %~1 /bin/bash ^
                -c "java -jar -Dspring.profiles.active=container,dev app.jar"
        )
        if "%~2" == "test" (
            call docker run --rm --name %~1 ^
                --network bank2_net --entrypoint="" ^
                %~1 /bin/bash ^
                -c "export MAVEN_OPTS=^"-Dspring.profiles.active=container,test^" ^&^& mvn clean compile test -DargLine=^"-Dspring.profiles.active=container,test^"!"
        )
    endlocal
exit /B 0



rem Helper function for setting up the Docker network for services.
:docker-network-up
    setlocal
        echo Setting up network...
        (
            call docker network inspect bank2_net >NUL 2>&1
        ) || (
            call docker network create --driver bridge bank2_net
        )
    endlocal
exit /B 0



rem Helper function for executing a microservice locally.
:local-service-exec
    setlocal
        set JAVA_HOME=%projectHome%\lib\%jdk%
        cd %~1
        set MAVEN_OPTS=-Dspring.profiles.active=%~3
        rem If starting in new window:
        if "%~4" == "true" (
            start "%~1" cmd /k "mvnw spotless:apply clean compile %~2"
        )
        if "%~4" NEQ "true" (
            echo %~2
            call mvnw spotless:apply clean compile %~2
        )
        cd ..
    endlocal
exit /B 0



rem Helper function for stopping a running service.
:local-service-stop
    setlocal
        rem TODO kill maven process by project name, see:
        rem https://stackoverflow.com/questions/45975107/how-to-stop-maven-execution
        taskkill /fi "WINDOWTITLE eq mvnw"
    endlocal
exit /B 0



rem Initializes the JDK, Git repo settings/hooks, etc. Sets up the development
rem environemnt.
:init
    setlocal
        rem Disable auto-modification of CR/LF endings
        echo "Disabling git config core.autocrlf (this repo)..."
        call git config core.autocrlf false
        echo "Done"

        rem Copy git hooks
        echo "Copying git hooks..."
        xcopy "git\hooks" ".git\hooks" /E /C /H /R /K /Y
        echo "Done"

        rem Download the package
        echo "Downloading Amazon Corretto JDK..."
        call curl -Lo ./lib/%targetJdk% %sourceJdk%
        rem Download the checksum
        call curl -Lo ./lib/%targetSha% %sourceSha%
        echo "Done"

        rem Check SHA256
        cd lib
        echo "Verifying JDK checksum..."
        del shacomputed0.txt
        del shacomputed1.txt
        set /p shadownload=<%targetSha%
        >shacomputed0.txt (
            certutil -hashfile %targetJdk% SHA256
        )
        >shacomputed1.txt (
            findstr /r /c:"^[a-z0-9]*$" shacomputed0.txt
        )
        fsutil file seteof shacomputed1.txt 64 1> NUL
        rem File may have multiple lines, but we're only interested
        rem in the first line, so we'll keep the first var
        SET count=1
        FOR /F "tokens=* USEBACKQ" %%F IN (`type shacomputed1.txt`) DO (
          SET shacomputed!count!=%%F
          SET /a count=!count!+1
        )
        if "!shacomputed1!"=="" (
            echo "Bad SHA, do ./run init again"
            exit 1
        )
        if not "!shadownload!"=="!shacomputed1!" (
            echo "Bad SHA, do ./run init again"
            exit 1
        )
        del shacomputed0.txt >NUL
        del shacomputed1.txt >NUL
        echo "Done"

        rem Unpack
        echo "Unpacking JDK..."
        cd lib
        rmdir %jdk% /s /q
        tar -xf %targetJdk%

        rem Remove residue
        del %targetJdk%
        del %targetSha%

        rem Move files directly into current dir
        move /y jdk* %jdk%
        echo "Done"
        echo "Init complete"
    endlocal
exit /B 0



rem Builds the dev images and starts the required services (including frontend).
rem Can be run locally or via Docker.
:dev
    setlocal
        if "%~1" == "FALSE" (
            echo Starting dev stack...

            call :stop "--microservices"
            call :docker-network-up

            set "i=0"
            :loopDev0
            if defined services[!i!] (
                call :docker-service-build %%services[!i!]%%
                set /a "i+=1"
                goto :loopDev0
            )

            echo Starting services...

            call docker compose up -d mariadb
            call docker compose up -d flyway
            call docker compose up -d mongodb

            set "i=0"
            :loopDev1
            if defined services[!i!] (
                call :docker-service-exec %%services[!i!]%% "dev"
                set /a "i+=1"
                goto :loopDev1
            )

            echo Pulling and starting frontend...

            rem TODO test if frontend working correctly when dev --local
            (
                call docker tag frontend ^
                    harbor.k8s.elab.rs/banka-2/frontend
            ) || (
                    (
                        call docker image pull ^
                            harbor.k8s.elab.rs/banka-2/frontend
                    ) && (
                        call docker tag ^
                            harbor.k8s.elab.rs/banka-2/frontend frontend
                    )
            )
            call docker run --rm -d --expose 80 --publish 80:80 ^
                --name frontend frontend

            echo Dev stack started
        )

        if "%~1" == "TRUE" (
            echo Starting dev stack locally...

            call :stop "--microservices"
            call :docker-network-up

            echo Starting services...

            call docker compose up -d mariadb
            call docker compose up -d flyway
            call docker compose up -d mongodb

            set "i=0"
            :loopDev2
            if defined services[!i!] (
                call :local-service-exec %%services[!i!]%% "exec:java" "local,dev" "true"
                set /a "i+=1"
                goto :loopDev2
            )

            echo Pulling and starting frontend...

            rem TODO test if frontend working correctly when dev (no --local)
            (
                call docker tag frontend ^
                    harbor.k8s.elab.rs/banka-2/frontend
            ) || (
                (
                    call docker image pull ^
                        harbor.k8s.elab.rs/banka-2/frontend
                ) && (
                    call docker tag ^
                        harbor.k8s.elab.rs/banka-2/frontend frontend
                )
            )
            call docker run --rm -d --expose 80 --publish 80:80 ^
                --name frontend frontend

            echo Dev stack started
        )
    endlocal
exit /B 0



rem Builds the test images and starts the required services. Can be run locally
rem or via Docker.
:test
    setlocal
        if "%~1" == "FALSE" (
            echo Starting test...

            call docker compose stop backend
            call :stop "--microservices"
            call :docker-network-up

            echo Removing old builds...

            set "i=0"
            :loopTest0
            if defined services[!i!] (
                call docker compose rm -s -f %%services[!i!]%%
                call :docker-service-build %%services[!i!]%%

                set /a "i+=1"
                goto :loopTest0
            )

            echo Starting services...

            call docker compose up -d mariadb
            call docker compose up -d flyway
            call docker compose up -d mongodb

            rem Start each service with test profile, but do NOT run the test.
            rem This is because services rely on each other for functionality,
            rem so one service may depend on another.

            set "i=0"
            :loopTest1
            if defined services[!i!] (
                call docker run --rm -d --name %%services[!i!]%% ^
                    --network bank2_net --entrypoint="" ^
                    %%services[!i!]%% /bin/bash ^
                    -c "java -jar -Dspring.profiles.active=container,test app.jar"

                set /a "i+=1"
                goto :loopTest1
            )

            echo Starting tests...

            rem Stop a started service, run it in test, and restart.

            set "i=0"
            :loopTest2
            if defined services[!i!] (

                echo Starting test on !services[%i%]!...

                call docker stop %%services[!i!]%% >NUL 2>&1

                call :docker-service-exec %%services[!i!]%% "test"

                call docker run --rm -d --name %%services[!i!]%% ^
                    --network bank2_net --entrypoint="" ^
                    %%services[!i!]%% /bin/bash ^
                    -c "java -jar -Dspring.profiles.active=container,test app.jar"

                set /a "i+=1"
                goto :loopTest2
            )

            rem Stop all services.

            echo Cleaning up...

            set "i=0"
            :loopTest3
            if defined services[!i!] (
                call docker stop %%services[!i!]%% >NUL 2>&1

                set /a "i+=1"
                goto :loopTest3
            )
            call docker compose down

            echo Tests completed
        )

        if "%~1" == "TRUE" (
            echo Starting test locally...

            call docker compose stop backend
            call :docker-network-up

            echo Starting services...

            call docker compose up -d mariadb
            call docker compose up -d flyway
            call docker compose up -d mongodb

            rem Start all services in local, test profiles as dependencies,
            rem and run tests.

            echo Starting tests...

            set "i=0"
            :loopTest4
            if defined services[!i!] (

                set "j=0"
                :loopTest5
                if defined services[!j!] (

                    rem Run if not the service to be tested

                    if %%services[!j!]%% NEQ %%services[!i!]%% (
                        call :local-service-exec %%services[!i!]%% ^
                            "exec:java" "local,test" "true"
                        call timeout /t 3
                    )

                    rem Run tests if to be tested

                    if %%services[!j!]%% == %%services[!i!]%% (

                        echo Starting test on !services[%i%]!...

                        rem TODO fails here, string not properly concatenating!
                        call :local-service-exec %%services[!i!]%% "test -DargLine=^"-Dspring.profiles.active^=local,test^"!" "local,test"
                    )

                    set /a "j+=1"
                    goto :loopTest5
                )

                rem Clean up for next cycle of tests

                call taskkill /t /fi "WINDOWTITLE eq mvnw"

                set /a "i+=1"
                goto :loopTest4
            )

            rem Clean-up.

            echo Cleaning up...

            call docker compose down
            set "i=0"
            :loopTest6
            if defined services[!i!] (
                call taskkill /t /fi "WINDOWTITLE eq mvnw" >NUL 2>&1

                set /a "i+=1"
                goto :loopTest6
            )

            echo Tests completed
        )
    endlocal
exit /B 0



rem Builds the production image for a single service and pushes it to harbor.
rem NOTE: you need to be logged in to harbor.k8s.elab.rs to execute this.
:dist
    setlocal
        call :docker-service-build "%~1"
        call docker push harbor.k8s.elab.rs/banka-2/%~1
    endlocal
exit /B 0



rem Restarts all auxiliary Docker services.
:stack
    setlocal
        call docker compose restart mariadb
        call docker compose restart mongodb
        call docker compose restart flyway
    endlocal
exit /B 0



rem Removes and rebuilds all Docker auxiliary services. Use this command if
rem encountering errors in your build process.
:reset
    setlocal
        call docker compose -v down
        call docker compose up -d mariadb
        call docker compose up -d flyway
        call docker compose up -d mongodb
    endlocal
exit /B 0



rem Stops all auxiliary services or microservices if "--microservices" passed.
:stop
    setlocal
        if "%~1" == "" (
            call docker compose down >NUL
        )

        if "%~1" == "--microservices" (
            set "i=0"
            :loopStop0
            if defined services[!i!] (
                call docker stop %%services[!i!]%% >NUL 2>&1
                set /a "i+=1"
                call :loopStop0
            )
            call docker stop frontend >NUL 2>&1
        )
    endlocal
exit /B 0



rem DANGER!!! For testing the development environment. Executes Docker
rem containers in privileged mode. Do NOT use for app development!
:devenv
    setlocal
        rem TODO add windows images
        call docker build -t test-devenv-ubuntu-x64 -f ./docker/test-devenv.ubuntu.x64.Dockerfile .
        call docker run --rm -it --cap-add=NET_ADMIN --privileged --entrypoint /home/project/docker/test-devenv.sh test-devenv-ubuntu-x64
        call docker build -t test-devenv-ubuntu-aarch64 -f ./docker/test-devenv.ubuntu.aarch64.Dockerfile .
        call docker run --rm -it --cap-add=NET_ADMIN --privileged --entrypoint /home/project/docker/test-devenv.sh test-devenv-ubuntu-aarch64
    endlocal
exit /B 0



rem Main script logic.
:main
    setlocal enableextensions enabledelayedexpansion

        rem If local execution (not Docker) requested.
        echo "%*"|findstr /R "^[^\-]*--local.*$" > reg.temp
        set /p reg=<reg.temp
        del reg.temp
        if [%reg%] == [] (
            set local=FALSE
        ) else (
            set local=TRUE
        )

        rem Initializes the repository on the local machine: sets up
        rem the .git folder and downloads the correct JDK.
        set projectHome=%cd%
        set targetJdk=amazon-corretto-17-x64-windows-jdk.zip
        set sourceJdk=https://corretto.aws/downloads/latest/%targetJdk%
        set targetSha=amazon-corretto-17-x64-windows-jdk.zip.checksum
        set sourceSha=https://corretto.aws/downloads/latest_sha256/%targetJdk%
        set jdk=jdk-amazon-corretto-17-x64-windows

        if "%1" == "" call :dev
        if "%1" == "init" call :init
        if "%1" == "dev" call :dev %local%
        if "%1" == "test" call :test %local%
        if "%1" == "dist" call :dist %~1
        if "%1" == "stack" call :stack
        if "%1" == "reset" call :reset
        if "%1" == "stop" call :stop %~1
        if "%1" == "devenv" call :devenv
    endlocal
exit /B 0
