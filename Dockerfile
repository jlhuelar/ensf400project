# Stage 1: Build the WAR file using Gradle
FROM gradle:7.6.1-jdk11 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the Gradle configuration files and source code
# COPY build.gradle ./
# COPY gradle ./gradle
# COPY gradlew gradlew.bat ./
# COPY src ./src
# COPY desktop_app ./desktop_app
COPY . .

# Build the WAR file
RUN gradle war --no-daemon

# Stage 2: Deploy the WAR file to Tomcat
FROM tomcat:9-jdk11 AS runtime

# Remove the default ROOT application in Tomcat
RUN rm -rf /usr/local/tomcat/webapps/ROOT

# Copy the WAR file from the build stage to Tomcat's webapps directory
COPY --from=build /app/build/libs/*.war /usr/local/tomcat/webapps/ROOT.war

# Expose the port Tomcat runs on
EXPOSE 8080

# Start Tomcat
CMD ["catalina.sh", "run"]