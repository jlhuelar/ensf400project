# Use the official Gradle image with JDK (adjust version as needed)
FROM gradle:7.5.1-jdk11

# Set the working directory inside the container
WORKDIR /home/gradle/project

# Copy the entire project into the container
COPY . .

# Ensure the gradlew script is executable
RUN chmod +x gradlew

# Start the application using the gradle wrapper and apprun task
CMD ["./gradlew", "apprun"]
