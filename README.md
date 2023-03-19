# Backend Documentation

## Setup & Installation

### Requirements

The following software is required for the development process:

1. [Java](https://openjdk.org/) - if you want to run the project locally (besides using Docker), you'll need to download **Java 17** and set the `PATH` as well as `JAVA_HOME` variables to point to the installation dir. (`PATH` should point to the `/bin` directory, while `JAVA_HOME` should point to the Java installation directory (the "root" directory.)) [OpenJDK](https://openjdk.org/) is recommended.
2. [Apache Maven](https://maven.apache.org/install.html) - included in the project repository.
3. [Docker Desktop](https://www.docker.com/products/docker-desktop/) - required for building and testing the project. **Please download and install Docker Desktop if you're not already using it.**

### Installing (Forking).

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

- `./make build` - builds the Docker production image.
- `./make dev` - compiles the Java code inside a Docker container (caching enabled!), runs all required services, but does NOT run tests. **This command is recommended for development.**
- `./make test` - compiles the Java code inside a Docker container (caching enabled!), runs all required services, and executes app tests. Stops all services after testing. **This command is the recommended command before opening a pull request.**
- `./make prod` - compiles the Java code inside a Docker container and runs all required services. **This command is the recommended production command.**

**It is strongly recommended to test the application in Docker before committing changes.** However, if you're unable to do so, please run the following command:

```shell
./mvnw spotless:apply clean test
```

### Hooks

This project is configured to use several hooks via the `init` file which copies them into `.git/hooks`.

- **Pre-commit hook:** formats the code before committing.
- **Pre-push hook:** runs all the required services and tests the application. If testing fails, the push is aborted.

## Resources

- [Spring Data MongoDB - Reference Documentation](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/) - documentation of the Spring Data MongoDB project with examples and templates.