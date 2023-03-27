@echo off

if "%1" == "" goto dev

rem Initializes the repository on the local machine: sets up
rem the .git folder and downloads the correct JDK.
if "%1" == "init" (
    :init
    rem TODO add contents from Makefile.init
    xcopy "git\hooks" ".git\hooks" /E /C /H /R /K /Y
    goto end
)

rem Builds the production image.
if "%1" == "build" (
    :build
    mvnw spotless:apply
	docker build -t banka2backend-prod -f prod.Dockerfile .
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
    docker build -t test-devenv-ubuntu-x64     -f ./docker/test-devenv.ubuntu.x64.Dockerfile .
    docker build -t test-devenv-ubuntu-aarch64 -f ./docker/test-devenv.ubuntu.aarch64.Dockerfile .
    goto end
)

:end