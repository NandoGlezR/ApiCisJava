FROM eclipse-temurin:21-jdk AS build

WORKDIR /app
EXPOSE 8081

RUN apt-get update && apt-get install -y maven

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src/main ./src/main

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /app/target/*jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]