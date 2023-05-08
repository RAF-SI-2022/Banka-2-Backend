.DEFAULT_GOAL := dev

ver = 17
os = $(shell uname)
ext = tar.gz
# Get architecture; if egrep returns nothing, then it's arm64
ifeq ($(shell uname -m | egrep "^(arm.*)|(aarch.*)"),)
	arch = x64
else
	arch = aarch64
endif
# Get os and sha commands for each os
ifeq ($(os),Linux)
	os = linux
	commsha = sha256sum -c ${targetSha}
endif
ifeq ($(os), Darwin)
	os = macos
	commsha = shasum -a 256 -c ${targetSha}
endif
# File/dir names
targetJdk = amazon-corretto-${ver}-${arch}-${os}-jdk.${ext}
sourceJdk = https://corretto.aws/downloads/latest/${targetJdk}
targetSha = amazon-corretto-${ver}-${arch}-${os}-jdk.${ext}.checksum
sourceSha = https://corretto.aws/downloads/latest_sha256/${targetJdk}
jdk = ./lib/jdk-amazon-corretto-${ver}-${arch}-${os}
fullJdk = $(shell pwd)

# Initializes the repository on the local machine: sets up
# the .git folder and downloads the correct JDK.
init:
	# Disable auto-modification of CR/LF endings
	echo "Disabling git config core.autocrlf (this repo)..."
	-git config core.autocrlf false
	echo "Done"
	# Copy git hooks
	echo "Copying git hooks..."
	cp -a ./git/hooks/. ./.git/hooks
	echo "Done"
	# Download the package
	echo "Downloading Amazon Corretto JDK..."
	curl -L -o ./lib/${targetJdk} ${sourceJdk}
	# Download the checksum
	curl -L -o ./lib/${targetSha} ${sourceSha}
	echo "Done"
	# Check SHA256
	echo "Verifying JDK checksum..."
	cd lib && \
	echo '  ${targetJdk}' >> ${targetSha} && \
	${commsha}
	echo "Done"
	# Unpack
	echo "Unpacking JDK..."
	mkdir -p ${jdk}
	tar -xf ./lib/amazon-corretto-${ver}-${arch}-${os}-jdk.${ext} -C ${jdk}
	# Remove residue
	rm -f ./lib/${targetJdk}
	rm -f ./lib/${targetSha}
	# Move files directly into current dir
	mv $(jdk)/amazon-* ./lib/extracted
	rm -rf $(jdk)
	mkdir -p $(jdk)
	mv ./lib/extracted/* $(jdk)
	rm -rf ./lib/extracted
	echo "Done"
	echo "Init complete"

# Builds the production image.
build:
	./mvnw spotless:apply
	docker build -t banka2backend-prod -f ./docker/prod.Dockerfile .

# Builds the app locally and starts the required services
# in a docker container (the app is run locally.)
local-dev:
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	cd ${jdk} && export JAVA_HOME=$(pwd)#TODO test this!
	export MAVEN_OPTS=-Dspring.profiles.active=local,dev
	./mvnw spotless:apply clean compile exec:java

# Builds the app locally and starts the required services
# in a docker container, then runs app tests.
local-test:
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	cd ${jdk} && export JAVA_HOME=$(pwd)#TODO test this!
	export MAVEN_OPTS=-Dspring.profiles.active=local,test
	./mvnw spotless:apply clean compile test -DargLine="-Dspring.profiles.active=local,test"

# Builds the dev image and starts the required services.
dev:
	./mvnw spotless:apply
	docker compose stop backend
	docker network create --driver bridge bank2_net
	docker build -t backend -f ./docker/backend.Dockerfile .
	docker tag backend harbor.k8s.elab.rs/banka-2/backend
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	docker run --rm -d --expose 8080 --name backend --network bank2_net --entrypoint="" backend /bin/bash -c "java -jar -Dspring.profiles.active=container,dev app.jar"

# Builds the test image and starts the required services.
test:
	./mvnw spotless:apply
	docker compose stop backend
	docker network create --driver bridge bank2_net
	docker build -t backend -f ./docker/backend.Dockerfile .
	docker tag backend harbor.k8s.elab.rs/banka-2/backend
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	# TODO when adding new services, each service has to be started the
	# "normal" way (but with test profile), and also have a test container
	# started as well
	docker run --rm --expose 8080 --name backend --network bank2_net --entrypoint="" backend /bin/bash -c "export MAVEN_OPTS=\"-Dspring.profiles.active=container,test\" && mvn clean compile test -DargLine=\"-Dspring.profiles.active=container,test\""

# Builds and tests the production image, and pushes to harbor. NOTE: you
# need to be logged in to harbor.k8s.elab.rs to execute this.
dist:
	./mvnw spotless:apply
	docker compose stop backend
	docker network create --driver bridge bank2_net
	docker build -t backend -f ./docker/backend.Dockerfile .
	docker tag backend harbor.k8s.elab.rs/banka-2/backend
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	# TODO when adding new services, each service has to be started the
	# "normal" way (but with test profile), and also have a test container
	# started as well
	docker run --rm --expose 8080 --name backend --network bank2_net --entrypoint="" backend /bin/bash -c "export MAVEN_OPTS=\"-Dspring.profiles.active=container,test\" && mvn clean compile test -DargLine=\"-Dspring.profiles.active=container,test\"" && docker push harbor.k8s.elab.rs/banka-2/backend

# Starts frontend and backend on production.
prod:
	docker tag backend harbor.k8s.elab.rs/banka-2/backend
	docker tag frontend harbor.k8s.elab.rs/banka-2/frontend
	docker compose down
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	# ne radi kako treba:
	# docker run --rm -d --expose 8080 --name backend --network bank2_net --entrypoint="" ^
	#     backend /bin/bash ^
	#     -c "java -jar -Dspring.profiles.active=container,prod app.jar"
	# docker run --rm -d --expose 80 --publish 80:80 --name frontend ^
	#     --network bank2_net frontend
    docker run --rm -d --expose 8080 --publish 8080:8080 --name backend --network bank2_net --entrypoint="" backend /bin/bash -c "java -jar -Dspring.profiles.active=container,prod app.jar"
    docker run --rm -d --expose 80 --publish 80:80 --name frontend frontend

# Restarts all Docker helper services.
services:
	docker compose restart mariadb
	docker compose restart mongodb
	docker compose restart flyway

# Removes and rebuilds all Docker helper services. Use this
# command if encountering errors in your build process.
reset:
	docker compose -v down
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb

# Stops all services.
stop:
	docker compose down
	docker stop backend

# DANGER!!! For testing the development environment.
# Executes Docker containers in privileged mode. Do NOT use
# for app development!
test-devenv:
	#TODO add windows images
	docker build -t test-devenv-ubuntu-x64 -f ./docker/test-devenv.ubuntu.x64.Dockerfile .
	docker run --rm -it --cap-add=NET_ADMIN --privileged --entrypoint /home/project/docker/test-devenv.sh test-devenv-ubuntu-x64
	docker build -t test-devenv-ubuntu-aarch64 -f ./docker/test-devenv.ubuntu.aarch64.Dockerfile .
	docker run --rm -it --cap-add=NET_ADMIN --privileged --entrypoint /home/project/docker/test-devenv.sh test-devenv-ubuntu-aarch64