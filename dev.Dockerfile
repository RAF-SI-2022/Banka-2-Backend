FROM maven:3.8.5-openjdk-17-slim as base

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ADD pom.xml /usr/src/app
RUN mvn verify -Dspring.profiles.active=remote --fail-never

FROM base AS stage1
ADD . /usr/src/app
RUN mvn spotless:apply clean package -DskipTests -Dspring.profiles.active=remote

FROM stage1 AS stage2
RUN cp /usr/src/app/target/*.jar /usr/src/app/app.jar

FROM stage2 AS stage3
WORKDIR /usr/src/app
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=remote", "app.jar"]