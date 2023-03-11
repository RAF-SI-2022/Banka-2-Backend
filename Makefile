.DEFAULT_GOAL := build

build:
	docker build -t banka2backend .
	docker compose down
	docker compose up -d dbusers
	docker compose up -d flyway
	docker compose up -d banka2backend

start:
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend