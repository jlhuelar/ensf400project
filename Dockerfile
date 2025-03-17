# Stage 1: Build - Using Gradle with JDK 17 to compile the application
FROM gradle:7.6-jdk17 AS builder
WORKDIR /home/gradle/project
# Copying all project files into the container
COPY . .

# Clean and build the project without using the daemon; forces dependency refresh
RUN gradle clean build --no-daemon --refresh-dependencies

# Stage 2: Using Tomcat for a WAR - Deploying the application using Tomcat 9
FROM tomcat:9-jre11
WORKDIR /usr/local/tomcat/webapps
# Removing default Tomcat applications 
RUN rm -rf ROOT

# Copying the WAR file built in Stage 1 into the Tomcat webapps directory and named it ROOT.war
COPY --from=builder /home/gradle/project/build/libs/*.war ./ROOT.war
# Expose port 8080 so the application can be accessed
EXPOSE 8080
