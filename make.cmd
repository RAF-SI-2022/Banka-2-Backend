@echo off

if "%1" == "" goto dev

if "%1" == "build" (
    :build
    mvnw spotless:apply
	docker build -t banka2backend-prod -f prod.Dockerfile .
    goto end
)

if "%1" == "dev" (
    :dev
    mvnw spotless:apply
    docker build -t banka2backend-dev -f dev.Dockerfile .
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend-dev
    goto end
)

if "%1" == "test" (
    :test
    mvnw spotless:apply
    docker build -t banka2backend-test -f test.Dockerfile .
    docker compose up -d dbusers
    docker compose up -d flyway
    docker run --rm --network container:dbusers banka2backend-test
    docker compose down
    goto end
)

if "%1" == "prod" (
    :prod
    mvnw spotless:apply
	docker build -t banka2backend-prod -f prod.Dockerfile .
	docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend-prod
    goto end
)

if "%1" == "restart-services" (
    :restart-services
    docker compose restart dbusers
    docker compose restart flyway
    goto end
)

:end