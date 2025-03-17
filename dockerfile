
    # Establishing the Base Image and where the image will build
FROM gradle:7.6.1-jdk11 AS build
WORKDIR /desktop_app

    # Setting up the file to be sent from the machine into the image
COPY gradle/ gradle/   
COPY gradlew .          
COPY gradlew gradlew.bat ./      
COPY build.gradle ./               

    # Make Gradle wrapper executable and build the application
RUN chmod +x gradlew
RUN ./gradlew build    

    # Copying the file components present in the project into the image
COPY . .

    # Exposing the port to be accessed
EXPOSE 8080

    # The commands used to for the CMD
CMD ["java", "-jar", "your-app.jar", "apprun", "./gradlew"]