# ==============================================================================
# Multi-stage Dockerfile per Game Vault (Spring Boot 3 + Java 21)
# ==============================================================================

# Stage 1: Build dell'applicazione
FROM maven:3.9.8-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copia pom.xml e sorgenti
COPY pom.xml .
COPY src ./src

# Compilazione ed impacchettamento del JAR
RUN mvn clean package -DskipTests

# Stage 2: Runtime leggero
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Utente non-root per sicurezza container
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copia JAR generato dallo stage di build
COPY --from=build /app/target/*.jar app.jar

# Esposizione porta standard dell'applicazione
EXPOSE 8080

# Avvio applicazione con profili di produzione
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
