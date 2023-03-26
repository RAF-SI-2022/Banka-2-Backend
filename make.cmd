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
    docker compose rm -s -f banka2backend-test
    docker compose rm -s -f banka2backend-prod
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d mongodb
    docker compose up -d banka2backend-dev
    goto end
)

if "%1" == "test" (
    :test
    mvnw spotless:apply
    docker build -t banka2backend-test -f test.Dockerfile .
    docker compose rm -s -f banka2backend-dev
    docker compose rm -s -f banka2backend-prod
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d influxdb
    docker run --rm --network container:mariadb banka2backend-test
    docker compose rm -s -f banka2backend-test
    goto end
)

if "%1" == "prod" (
    :prod
    mvnw spotless:apply
	docker build -t banka2backend-prod -f prod.Dockerfile .
	docker compose down
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d influxdb
    docker compose up -d banka2backend-prod
    goto end
)

if "%1" == "restart-services" (
    :restart-services
    docker compose restart mariadb
    docker compose restart influxdb
    docker compose restart flyway
    goto end
)

if "%1" == "reset-all" (
    :init
    docker compose -v down
    docker compose up -d mariadb
    docker compose up -d flyway
    docker compose up -d influxdb
    goto end
)

:end