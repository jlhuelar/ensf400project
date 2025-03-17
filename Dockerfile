# Use OpenJDK 17 as base image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the entire project into the container
COPY . .

# Install Gradle (if not available in the image)
RUN apt-get update && apt-get install -y wget unzip && \
    wget https://services.gradle.org/distributions/gradle-7.3.3-bin.zip -P /tmp && \
    unzip /tmp/gradle-7.3.3-bin.zip -d /opt && \
    rm /tmp/gradle-7.3.3-bin.zip && \
    ln -s /opt/gradle-7.3.3/bin/gradle /usr/bin/gradle

# Expose the port used by the application (8080 in this case) to allow external access
EXPOSE 8080

# Make the Gradle wrapper executable
RUN chmod +x gradlew

# Run the application using the Gradle wrapper
CMD ["./gradlew", "apprun"]