.DEFAULT_GOAL := dev

build:
    mvnw spotless:apply clean test
	docker build -t banka2backend-prod -f prod.Dockerfile .

dev:
    mvnw spotless:apply clean package -DskipTests
    docker build -t banka2backend-dev -f dev.Dockerfile .
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend-dev

test:
    mvnw spotless:apply clean
    docker build -t banka2backend-test -f prod.Dockerfile .
    docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend-test
    docker compose down

prod:
    mvnw spotless:apply clean
	docker build -t banka2backend-prod -f prod.Dockerfile .
	docker compose down
    docker compose up -d dbusers
    docker compose up -d flyway
    docker compose up -d banka2backend-prod
