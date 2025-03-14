FROM openjdk:21-jdk-slim

# Set the working directory
WORKDIR /app

# Copy the Gradle wrapper and build files
COPY gradlew gradlew
COPY gradle gradle
COPY build.gradle gradle.properties ./

# Grant execute permission for the Gradle wrapper
RUN chmod +x gradlew

# Copy the source code
COPY src src

# Expose port
EXPOSE 8080

# Run the application in the background and keep container alive
CMD ./gradlew appRun & tail -f /dev/null
