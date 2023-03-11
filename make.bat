@echo off

if "%1" == "" goto build

if "%1" == "build" (
    :build
    docker build -t banka2backend .
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend
    goto end
)

if "%1" == "start" (
    :build
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend
    goto end
)

:end