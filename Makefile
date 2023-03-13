.DEFAULT_GOAL := dev

build:
	docker build -t banka2backend .

test:
    docker compose down
    docker build -t banka2backend .
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend-test
    docker compose down

dev:
    docker build -t banka2backend .
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend-test
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend

restart:
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend