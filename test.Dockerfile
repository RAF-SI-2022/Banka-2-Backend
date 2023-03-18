FROM maven:3.8.5-openjdk-17-slim as base

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ADD pom.xml /usr/src/app
RUN mvn verify --fail-never

FROM base AS stage1
ADD . /usr/src/app
ENTRYPOINT ["mvn", "spotless:apply", "clean", "test", "-Dspring.profiles.active=remote"]
