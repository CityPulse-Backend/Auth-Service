FROM maven:3-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline

COPY src src
RUN mvn package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=builder /app/target/Auth-Service-v1.0.jar Auth-Service-v1.0.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","Auth-Service-v1.0.jar"]