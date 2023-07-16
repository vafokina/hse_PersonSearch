FROM maven:3.5.2-jdk-8-alpine AS MAVEN_BUILD

MAINTAINER Brian Hannaway

COPY pom.xml /build/
COPY src /build/src/

WORKDIR /build/
RUN mvn package

FROM openjdk:17-alpine

WORKDIR /app

COPY --from=MAVEN_BUILD /build/target/personsearch-0.1.0.jar /app/

ENTRYPOINT ["java", "-jar", "personsearch-0.1.0.jar"]