@echo off
setlocal enableextensions enabledelayedexpansion

if "%1" == "" goto dev

rem Initializes the repository on the local machine: sets up
rem the .git folder and downloads the correct JDK.
set projectHome=%cd%
set targetJdk=amazon-corretto-17-x64-windows-jdk.zip
set sourceJdk=https://corretto.aws/downloads/latest/%targetJdk%
set targetSha=amazon-corretto-17-x64-windows-jdk.zip.checksum
set sourceSha=https://corretto.aws/downloads/latest_sha256/%targetJdk%
set jdk=jdk-amazon-corretto-17-x64-windows
if "%1" == "init" (
    :init
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
    goto end
)

rem Builds the app locally and starts the required services
rem in a docker container (the app is run locally.)
if "%1" == "local-dev" (
    :local-dev
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	set JAVA_HOME=%projectHome%\lib\%jdk%
	set MAVEN_OPTS=-Dspring.profiles.active=local,dev
	mvnw spotless:apply clean compile exec:java
	goto end
)

rem Builds the app locally and starts the required services
rem in a docker container, then runs app tests.
if "%1" == "local-test" (
    :local-test
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	set JAVA_HOME=%projectHome%\lib\%jdk%
	set MAVEN_OPTS=-Dspring.profiles.active=local,test
	mvnw spotless:apply clean compile test -DargLine="-Dspring.profiles.active=local,test"
	goto end
)

rem Builds the dev image and starts the required services.
if "%1" == "dev" (
    :dev
    mvnw spotless:apply
    docker compose stop backend
    docker network create --driver bridge bank2_net
    docker build -t backend -f ./docker/backend.Dockerfile .
    docker tag backend harbor.k8s.elab.rs/banka-2/backend
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d mongodb
    docker run --rm -d --expose 8080 --name backend --network bank2_net --entrypoint="" ^
        backend /bin/bash ^
        -c "java -jar -Dspring.profiles.active=container,dev app.jar"
    goto end
)

rem Builds the test image and starts the required services.
if "%1" == "test" (
    :test
    mvnw spotless:apply
    docker compose stop backend
    docker network create --driver bridge bank2_net
    docker build -t backend -f ./docker/backend.Dockerfile .
    docker tag backend harbor.k8s.elab.rs/banka-2/backend
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d mongodb
    rem TODO when adding new services, each service has to be started the
    rem "normal" way (but with test profile), and also have a test container
    rem started as well
    docker run --rm --expose 8080 --name backend --network bank2_net --entrypoint="" ^
        backend /bin/bash ^
        -c "export MAVEN_OPTS=\"-Dspring.profiles.active=container,test\" && mvn clean compile test -DargLine=\"-Dspring.profiles.active=container,test\""
    goto end
)

rem Builds and tests the production image, and pushes to harbor. NOTE: you
rem need to be logged in to harbor.k8s.elab.rs to execute this.
if "%1" == "dist" (
    :test
    docker compose stop backend
    docker network create --driver bridge bank2_net
    docker tag backend harbor.k8s.elab.rs/banka-2/backend
    docker build -t backend -f ./docker/backend.Dockerfile .
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d mongodb
    rem TODO when adding new services, each service has to be started the
    rem "normal" way (but with test profile), and also have a test container
    rem started as well
    docker run --rm --expose 8080 --name backend --network bank2_net --entrypoint="" ^
        backend /bin/bash ^
        -c "export MAVEN_OPTS=\"-Dspring.profiles.active=container,test\" && mvn clean compile test -DargLine=\"-Dspring.profiles.active=container,test\"" ^
        && docker push harbor.k8s.elab.rs/banka-2/backend
    goto end
)

rem Starts frontend and backend on production.
if "%1" == "prod" (
    :prod
    docker tag backend harbor.k8s.elab.rs/banka-2/backend
    docker tag frontend harbor.k8s.elab.rs/banka-2/frontend
	docker compose down
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d mongodb
    REM ne radi kako treba:
    REM docker run --rm -d --expose 8080 --name backend --network bank2_net --entrypoint="" ^
    REM     backend /bin/bash ^
    REM     -c "java -jar -Dspring.profiles.active=container,prod app.jar"
    REM docker run --rm -d --expose 80 --publish 80:80 --name frontend ^
    REM     --network bank2_net frontend
    docker run --rm -d --expose 8080 --publish 8080:8080 --name backend ^
        --network bank2_net --entrypoint="" backend /bin/bash ^
        -c "java -jar -Dspring.profiles.active=container,prod app.jar"
    docker run --rm -d --expose 80 --publish 80:80 --name frontend frontend
    goto end
)

rem Restarts all Docker helper services.
if "%1" == "services" (
    :services
    docker compose restart mariadb
    docker compose restart mongodb
    docker compose restart flyway
    goto end
)

rem Removes and rebuilds all Docker helper services. Use this
rem command if encountering errors in your build process.
if "%1" == "reset" (
    :init
    docker compose -v down
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d mongodb
    goto end
)

rem Stops all services.
if "%1" == "stop" (
    :stop
	docker compose down
	docker stop backend
    goto end
)

rem DANGER!!! For testing the development environment.
rem Executes Docker containers in privileged mode. Do NOT use
rem for app development!
if "%1" == "test-devenv" (
    :test-devenv
    rem TODO add windows images
    docker build -t test-devenv-ubuntu-x64 -f ./docker/test-devenv.ubuntu.x64.Dockerfile .
    docker run --rm -it --cap-add=NET_ADMIN --privileged --entrypoint /home/project/docker/test-devenv.sh test-devenv-ubuntu-x64
    docker build -t test-devenv-ubuntu-aarch64 -f ./docker/test-devenv.ubuntu.aarch64.Dockerfile .
    docker run --rm -it --cap-add=NET_ADMIN --privileged --entrypoint /home/project/docker/test-devenv.sh test-devenv-ubuntu-aarch64
    goto end
)

:end
endlocal