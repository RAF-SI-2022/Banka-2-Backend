FROM maven:3.9.1 as builder

# Configure for microservice
ENV SERVICE=client

# Build & Cache
ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME
ADD ./$SERVICE $HOME
RUN --mount=type=cache,target=/root/.m2 mvn -DfinalName=app -Dmaven.test.skip -f $HOME/pom.xml clean package

# Expose
FROM openjdk:17-alpine

ARG COPY_SRC=false

ENV HOME=/usr/app
RUN mkdir -p $HOME
WORKDIR $HOME

# Copy only if source requested
COPY --from=builder $HOME $HOME
RUN if [ "$COPY_SRC" != "true" ]; then cd .. && rm -rf $HOME && mkdir -p $HOME && cd $HOME; fi
COPY --from=builder $HOME/target/*.jar $HOME/app.jar

# Add curl for health check
RUN apk --no-cache --update add curl

# Configure for microservice
EXPOSE 8083
ENTRYPOINT ["echo", "Image entrypoint not defined!", "&&", "exit", "1"]