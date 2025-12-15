FROM eclipse-temurin:17-jdk-jammy

WORKDIR /app

# Copy Maven build files
COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Download dependencies (cache for faster builds)
RUN ./mvnw dependency:go-offline

# Copy the source code
COPY src ./src

# Package the application
RUN ./mvnw clean package -DskipTests

# Expose port (same as Spring Boot server.port)
EXPOSE 8080

# Run the Spring Boot application
CMD ["java", "-jar", "target/crm-0.0.1-SNAPSHOT.jar"]
