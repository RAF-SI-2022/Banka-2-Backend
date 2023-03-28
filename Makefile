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
	cd lib/${targetJdk} && export JAVA_HOME=$(pwd)#TODO test this!
	./lib/${targetJdk}/bin/javac.exe ./lib/FixLineEndings.java
	./lib/${targetJdk}/bin/java.exe -cp lib FixLineEndings
	./mvnw spotless:apply clean compile exec:java

# Builds the app locally and starts the required services
# in a docker container, then runs app tests.
local-test:
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	cd lib/${targetJdk} && export JAVA_HOME=$(pwd)#TODO test this!
	./lib/${targetJdk}/bin/javac.exe ./lib/FixLineEndings.java
	./lib/${targetJdk}/bin/java.exe -cp lib FixLineEndings
	./mvnw spotless:apply clean compile test

# Builds the dev image and starts the required services.
dev:
	./mvnw spotless:apply
	docker build -t banka2backend-dev -f ./docker/dev.Dockerfile .
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	docker compose up -d banka2backend-dev

# Builds the test image and starts the required services.
test:
	./mvnw spotless:apply
	docker build -t banka2backend-test -f ./docker/test.Dockerfile .
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	docker run --rm --network container:mariadb banka2backend-test
	-docker compose rm -s -f banka2backend-test

# Builds the prod image and starts the required services.
prod:
	./mvnw spotless:apply
	docker build -t banka2backend-prod -f ./docker/prod.Dockerfile .
	docker compose down
	docker compose up -d mariadb
	docker compose up -d flyway
	docker compose up -d mongodb
	docker compose up -d banka2backend-prod

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

# DANGER!!! For testing the development environment.
# Executes Docker containers in privileged mode. Do NOT use
# for app development!
test-devenv:
	#TODO add windows images
	docker build -t test-devenv-ubuntu-x64 -f ./docker/test-devenv.ubuntu.x64.Dockerfile .
	docker run --rm -it --cap-add=NET_ADMIN --privileged --entrypoint /home/project/docker/test-devenv.sh test-devenv-ubuntu-x64
	docker build -t test-devenv-ubuntu-aarch64 -f ./docker/test-devenv.ubuntu.aarch64.Dockerfile .
	docker run --rm -it --cap-add=NET_ADMIN --privileged --entrypoint /home/project/docker/test-devenv.sh test-devenv-ubuntu-aarch64