# Stage 1: Build with Gradle 7 and JDK 11
FROM gradle:7.6-jdk11 AS build

WORKDIR /app

# Copy everything to the container
COPY . .

# Ensure the wrapper is executable
RUN chmod +x gradlew

# Build the project (skip tests if needed)
RUN ./gradlew build --no-daemon

# Stage 2: Runtime image with only JDK 11
FROM openjdk:11-jdk-slim

WORKDIR /app

# Copy the entire built app from the build stage
COPY --from=build /app .

# Expose the app's port
EXPOSE 8080

# Command to run the application
CMD ["./gradlew", "apprun"]
