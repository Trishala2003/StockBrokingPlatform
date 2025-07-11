# Use an OpenJDK base image
FROM openjdk:17-jdk-slim

# Copy your built JAR file into the image
COPY target/StockBrokingPlatform-0.0.1-SNAPSHOT.jar app.jar

# Expose the Spring Boot port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app.jar"]
