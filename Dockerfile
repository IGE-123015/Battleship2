# ── Stage 1: Build ──────────────────────────────────────────────────────────
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build the fat jar
COPY src ./src
RUN mvn clean package -DskipTests -B

# ── Stage 2: Run ────────────────────────────────────────────────────────────
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the fat jar from the build stage
COPY --from=build /app/target/BattleshipGamePlayer-2.0.jar app.jar

# Expose no ports (CLI application)
ENTRYPOINT ["java", "-jar", "app.jar"]
