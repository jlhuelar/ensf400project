# Use an OpenJDK base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project into the container
COPY . .

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Expose the port used by the application
EXPOSE 8080

# Run the application using the Gradle wrapper
CMD ["./gradlew", "apprun"]