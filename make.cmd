@echo off
goto :main



rem #TODO remove functions and use for-loops instead
:docker-service-build
    setlocal
        set JAVA_HOME=%projectHome%\lib\%jdk%
        cd .\%~1
        call mvnw spotless:apply
        call docker build -t %~1-%~2 -f ./docker/%~2.Dockerfile .
        cd ..
    endlocal
exit /B 0



:local-service-exec
    setlocal
        set JAVA_HOME=%projectHome%\lib\%jdk%
        cd %~1
        call mvnw spotless:apply clean compile %~2
        cd ..
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
        curl -Lo ./lib/%targetJdk% %sourceJdk%
        rem Download the checksum
        curl -Lo ./lib/%targetSha% %sourceSha%
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
            echo "Bad SHA, do make init again"
            exit 1
        )
        if not "!shadownload!"=="!shacomputed1!" (
            echo "Bad SHA, do make init again"
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



rem Builds the dev image and starts the required services. Can be run locally
rem or via Docker.
:dev
    setlocal
        if %1 == FALSE (
            call docker compose rm -s -f users-test
            call :docker-service-build "users" "dev"
            call docker compose up -d mariadb
            call docker compose up -d flyway
            call docker compose up -d mongodb
            call docker compose up -d users
        ) else (
            call docker compose up -d mariadb
            call docker compose up -d flyway
            call docker compose up -d mongodb
            call :local-service-exec "users" "exec:java"
        )
    endlocal
exit /B 0



rem Builds the test image and starts the required services. Can be run locally
rem or via Docker.
:test
    setlocal
        if %1 == FALSE (
            call docker compose rm -s -f users-test
            call :docker-service-build "users" "test"
            call docker compose up -d mariadb
            call docker compose up -d flyway
            call docker compose up -d mongodb
            call docker run --rm --network container:mariadb users-test
            call docker compose rm -s -f users-test
        ) else (
            call docker compose up -d mariadb
            call docker compose up -d flyway
            call docker compose up -d mongodb
            call :local-service-exec "users" "test"
        )
    endlocal
exit /B 0



rem Builds the production image and pushes it to the harbor.
:prod
    setlocal
        call :docker-service-build "users" "prod"
        rem add docker login and push to harbor
    endlocal
exit /B 0



rem Restarts all auxiliary Docker services.
:services
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
        if "%2" == "--local" (
            set local=TRUE
        ) else (
            set local=FALSE
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
        if "%1" == "prod" call :prod %local%
        if "%1" == "services" call :services
        if "%1" == "reset" call :reset
        if "%1" == "devenv" call :devenv
    endlocal
exit /B 0
