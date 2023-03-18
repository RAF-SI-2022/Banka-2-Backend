# Backend Documentation

## Setup & Installation

### Requirements

The following software is required for the development process:

1. [Java](https://openjdk.org/) - you'll need to download **Java 17** and set the `PATH` as well as `JAVA_HOME` variables to point to the installation dir. (`PATH` should point to the `/bin` directory, while `JAVA_HOME` should point to the Java installation directory (the "root" directory.)) [OpenJDK](https://openjdk.org/) is recommended.
2. [Apache Maven](https://maven.apache.org/install.html) - included in the project repository.
3. [Docker Desktop](https://www.docker.com/products/docker-desktop/) - required for building and testing the project. **Please download and install Docker Desktop if you're not already using it.**

### Installing (Forking)

Start by forking the latest sprint branch into your GitHub account.

Once you clone the project to your computer, run the following command:

```shell
    ./init
```

This script will download the required JDK and set up Git hooks to ensure you only push clean, tested code.

## Building

The service can be deployed using locally-installed Maven, Java and other services (MariaDB and Flyway), or can be run using Docker.

**For development**, it is recommended to compile the Java code locally, while running it in Docker (together with other services which are containerized as well.)

**For production**, it is recommended to compile the Java code inside the provided Dockerfile as well.

The `Makefile` is the central tool for building the project. Use the following commands during development and production stages:

- `./make build` - simply builds the Docker image file of the service.
- `./make dev` - compiles the Java code locally, and creates a "development" version of the Docker image. Starts other necessary services. **This command is recommended for development.** NOTE: in order to run this command successfully, you'll need to install the Java version found in `pom.xml` on your local computer, and set the `JAVA_HOME` path variable to its location. 
- `./make test` - compiles the Java code inside the Docker container, runs all required services, and runs tests. **This command is the recommended command before opening a pull request.**
- `./make prod` - compiles the Java code inside the Docker container and runs all required services. **This command is the recommended production command.**

**It is strongly recommended to test the application before committing.** However, if you're unable to do so, please run the following command:

```shell
    ./mvnw spotless:apply clean test
```