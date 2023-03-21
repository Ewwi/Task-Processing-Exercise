FROM maven:3.8.6-openjdk-18 AS build
COPY pom.xml .
RUN mvn -B dependency:go-offline
COPY src src
RUN mvn -B package

FROM eclipse-temurin:17-alpine
EXPOSE 8080/tcp
ARG JAR_FILE=/target/*.jar
COPY --from=build ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
