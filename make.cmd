@echo off

if "%1" == "" goto dev

if "%1" == "build" (
    :build
    mvnw spotless:apply clean test
	docker build -t banka2backend-prod -f prod.Dockerfile .
    goto end
)

if "%1" == "dev" (
    :dev
    mvnw spotless:apply clean package -DskipTests
    docker build -t banka2backend-dev -f dev.Dockerfile .
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend-dev
    goto end
)

if "%1" == "test" (
    :test
    mvnw spotless:apply clean
    docker build -t banka2backend-test -f prod.Dockerfile .
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend-test
    docker compose down
    goto end
)

if "%1" == "prod" (
    :prod
    mvnw spotless:apply clean
	docker build -t banka2backend-prod -f prod.Dockerfile .
	docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend-prod
    goto end
)

:end