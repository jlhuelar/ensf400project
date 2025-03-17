
FROM gradle:7.6.1-jdk11 AS builder

WORKDIR /desktop_app

# Copy Gradle wrapper and configuration files
COPY . /app      

# Make Gradle wrapper executable and build the application
RUN chmod +x gradlew
RUN ./gradlew build    

# Step 2: Runtime stage - Use a minimal Java runtime image
FROM openjdk:11-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/your-app.jar .

# Expose port 8080 (adjust this if needed for your application)
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "your-app.jar"]