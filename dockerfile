
FROM gradle:8-jdk17 AS builder

WORKDIR /desktop_app

# Copy Gradle wrapper and configuration files
COPY gradle/ gradle/   
COPY gradlew .          
COPY gradlew.bat .      
COPY build.gradle .     
COPY settings.gradle .  
COPY src/ src/          

# Make Gradle wrapper executable and build the application
RUN chmod +x gradlew
RUN ./gradlew build    

# Step 2: Runtime stage - Use a minimal Java runtime image
FROM openjdk:17-jre-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/your-app.jar .

# Expose port 8080 (adjust this if needed for your application)
EXPOSE 8080

# Command to run the application
CMD ["java", "-jar", "your-app.jar"]