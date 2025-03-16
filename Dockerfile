# Base Image for building the application
FROM gradle:7.3.3-jdk11 AS build

# Set working directory inside the container
WORKDIR /desktop_app

# Copy Gradle-related files first (Optimizes caching)
COPY build.gradle ./
COPY gradlew gradlew.bat ./
COPY gradle ./gradle

# Copy the rest of the application source code
COPY . .

# Give execute permission to Gradle wrapper
RUN chmod +x ./gradlew

# Build the application
RUN ./gradlew build --no-daemon

# Expose application port
EXPOSE 8080

# Keep the application running in the foreground
CMD ["sh", "-c", "./gradlew appRun --no-daemon --continuous"]
