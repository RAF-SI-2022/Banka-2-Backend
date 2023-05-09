FROM maven:3.8.5-openjdk-17-slim as base

# Caching
RUN mkdir -p /usr/src/app
WORKDIR /usr/src/app
ADD pom.xml /usr/src/app
RUN mvn verify clean --fail-never

# Build
FROM base AS stage1
ADD . /usr/src/app
RUN mvn package -DskipTests

# Prepare JAR
FROM stage1 AS stage2
RUN cp /usr/src/app/target/*.jar /usr/src/app/app.jar

# Entrypoint
FROM stage2 AS stage3
WORKDIR /usr/src/app
ENTRYPOINT ["bash"]