FROM maven:3.8.5-openjdk-17-slim as base

RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ADD . /usr/src/app
RUN mvn spotless:apply clean package -DskipTests -Dspring.profiles.active=prod

FROM base AS stage1
RUN cp /usr/src/app/target/*.jar /usr/src/app/app.jar

FROM stage1 AS stage2
WORKDIR /usr/src/app
ENTRYPOINT ["java", "-jar", "-Dspring.profiles.active=prod", "app.jar"]