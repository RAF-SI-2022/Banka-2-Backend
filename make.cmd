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
	echo "Downloading Oracle JDK..."
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

rem Builds the production image.
if "%1" == "build" (
    :build
    mvnw spotless:apply
	docker build -t banka2backend-prod -f prod.Dockerfile .
    goto end
)

rem Builds the app locally and starts the required services
rem in a docker container (the app is run locally.)
if "%1" == "local" (
    :local
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	set JAVA_HOME=%projectHome%\lib\%jdk%
	mvnw spotless:apply clean compile exec:java
	goto end
)

rem Builds the dev image and starts the required services.
if "%1" == "dev" (
    :dev
    mvnw spotless:apply
    docker build -t banka2backend-dev -f dev.Dockerfile .
    docker compose rm -s -f banka2backend-test
    docker compose rm -s -f banka2backend-prod
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d mongodb
    docker compose up -d banka2backend-dev
    goto end
)

rem Builds the test image and starts the required services.
if "%1" == "test" (
    :test
    mvnw spotless:apply
    docker build -t banka2backend-test -f test.Dockerfile .
    docker compose rm -s -f banka2backend-dev
    docker compose rm -s -f banka2backend-prod
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d mongodb
    docker run --rm --network container:mariadb banka2backend-test
    docker compose rm -s -f banka2backend-test
    goto end
)

rem Builds the prod image and starts the required services.
if "%1" == "prod" (
    :prod
    mvnw spotless:apply
	docker build -t banka2backend-prod -f prod.Dockerfile .
	docker compose down
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d mongodb
    docker compose up -d banka2backend-prod
    goto end
)

rem # Restarts all Docker helper services.
if "%1" == "restart-services" (
    :restart-services
    docker compose restart mariadb
    docker compose restart mongodb
    docker compose restart flyway
    goto end
)

rem Removes and rebuilds all Docker helper services. Use this
rem command if encountering errors in your build process.
if "%1" == "reset-all" (
    :init
    docker compose -v down
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d mongodb
    goto end
)

rem For testing the development environment. Do NOT use for
rem testing the actual application.
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