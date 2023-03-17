FROM maven:3.8.5-openjdk-17-slim as base

RUN mkdir -p /usr/target/app
WORKDIR /usr/target/app
ADD /target/*.jar /usr/target/app.jar

FROM base AS stage1
WORKDIR /usr/target/app
ENTRYPOINT ["java", "-jar", "app.jar"]