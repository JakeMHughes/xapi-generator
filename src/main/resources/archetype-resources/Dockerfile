FROM maven:3.6.3-jdk-11-slim AS build
COPY src /app/arc
COPY pom.xml /app
WORKDIR /app
RUN mvn package


FROM openjdk:11-jre-slim-buster as deploy
WORKDIR /app
COPY --from=0 /app/target/*.jar /app/Application.jar
CMD java -jar Application.jar