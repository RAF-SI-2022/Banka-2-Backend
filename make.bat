@echo off

if "%1" == "" goto dev

if "%1" == "build" (
    :build
    docker build -t banka2backend .
    goto end
)

if "%1" == "test" (
    :test
    docker compose down
    docker build -t banka2backend .
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend-test
    docker compose down
    goto end
)

if "%1" == "dev" (
    :dev
    docker build -t banka2backend .
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend-test
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend
    goto end
)

if "%1" == "restart" (
    :restart
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend
    goto end
)

:end