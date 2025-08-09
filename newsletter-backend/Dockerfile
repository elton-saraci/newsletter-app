# Stage 1: Build
FROM eclipse-temurin:17-jdk-alpine AS build

WORKDIR /app

# Copy Maven files
COPY pom.xml mvnw ./
COPY .mvn .mvn

# Download dependencies only
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src src

# Build the jar
RUN ./mvnw clean package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/newsletter-0.0.1-SNAPSHOT.jar app.jar

# Expose port your app runs on (adjust if needed)
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
