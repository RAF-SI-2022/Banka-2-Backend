# Backend Documentation

## Setup & Installation

Admin login
email: "anesic3119rn+banka2backend+admin@raf.rs"
password: "admin"

Supervizori, agenti i klijenti moraju biti manuelno napravljeni 


### Requirements

The following software is required for the development process:

1. [Java](https://openjdk.org/) - if you want to run the project locally,
   run `./run init` to download the JDK automatically into `lib`, which will
   enable you to run any command on the project. Alternatively, you can
   download Java 17 JDK manually, and set the `PATH` as well as `JAVA_HOME`
   variables to point to the installation dir. (`PATH` should point to the
   `/bin` directory, while `JAVA_HOME` should point to the Java installation
   directory (the "root" directory.)) [OpenJDK](https://openjdk.org/) is
   recommended.
2. [Apache Maven](https://maven.apache.org/install.html) - included in the
   project repository.
3. [Docker Desktop](https://www.docker.com/products/docker-desktop/) - required
   for building and testing the project. **Please download and install Docker
   Desktop if you're not already using it.**

### Installing (Forking)

Start by forking the latest sprint branch into your GitHub account.

Once you clone the project to your computer, run the following command:

```shell
./run init
```

This script will download the required JDK and set up your local .git
repository.

## Building

The service can be deployed using locally-installed Maven, Java and other
services (MariaDB and Flyway), or can be run using Docker.

**For development**, it is recommended to compile the Java code locally, while
running it in Docker (together with other services which are containerized as
well.)

**For production**, it is recommended to compile the Java code inside the
provided Dockerfile as well.

The `run` file is the central tool for building the project. More
information is available with the `./run help` command.

### Hooks

**NOTE: GIT HOOKS ARE CURRENTLY DISABLED.**

This project is configured to use several hooks via the `init` file which copies
them into `.git/hooks`.

- **Pre-commit hook:** formats the code before committing.
- **Pre-push hook:** runs all the required services and tests the application.
  If testing fails, the push is aborted.

## Resources

- [Spring Data MongoDB - Reference Documentation](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/) -
  documentation of the Spring Data MongoDB project with examples and templates.
- [Spring & InfluxDB Tutorial by Baeldung](https://www.baeldung.com/java-influxdb)
