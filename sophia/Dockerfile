FROM maven:3.9-eclipse-temurin-21 AS build

COPY src /app/src
COPY pom.xml /app/pom.xml

WORKDIR /app
RUN mvn clean install -DskipTests

FROM eclipse-temurin:21-jre-alpine

COPY --from=build /app/target/*.jar /app/app.jar

WORKDIR /app

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]