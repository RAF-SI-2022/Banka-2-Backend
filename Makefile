.DEFAULT_GOAL := dev

build:
	./mvnw spotless:apply
	docker build -t banka2backend-prod -f prod.Dockerfile .

dev:
	./mvnw spotless:apply
	docker build -t banka2backend-dev -f dev.Dockerfile .
	docker compose up -d dbusers
	docker compose up -d flyway
	docker compose up -d dbexchange
	docker compose up -d banka2backend-dev

test:
	./mvnw spotless:apply
	docker build -t banka2backend-test -f test.Dockerfile .
	docker compose up -d dbusers
	docker compose up -d flyway
	docker compose up -d dbexchange
	docker run --rm --network container:dbusers banka2backend-test
	-docker compose rm -s -f banka2backend-test

prod:
	./mvnw spotless:apply
	docker build -t banka2backend-prod -f prod.Dockerfile .
	docker compose down
	docker compose up -d dbusers
	docker compose up -d flyway
	docker compose up -d dbexchange
	docker compose up -d banka2backend-prod

restart-services:
	docker compose restart dbusers
	docker compose restart dbexchange
	docker compose restart flyway

reset-all:
	docker compose down -v
	docker compose up -d dbusers
	docker compose up -d flyway
	docker compose up -d dbexchange