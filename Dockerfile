FROM openjdk:21-jdk-slim AS builder

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
RUN ./mvnw dependency:go-offline

COPY src src
RUN ./mvnw package -DskipTests

FROM openjdk:21-jdk-slim
WORKDIR /app

COPY --from=builder /app/target/*.jar auth_service.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","auth_service.jar"]