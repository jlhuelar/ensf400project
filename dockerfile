   
    # Establishing the Base Image and where the image will build
FROM gradle:7.6.1-jdk11 AS build
WORKDIR /desktop_app
      

    # Copying the file components present in the project into the image
COPY . .

    # Made the Gradle wrapper to be executable, as well as build the application
RUN chmod +x gradlew
RUN ./gradlew build    

    # Exposing the port to be accessed
EXPOSE 8080

    # The commands used to for the CMD own its own
CMD ["./gradlew", "apprun"]