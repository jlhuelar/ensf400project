FROM openjdk:11
 
 WORKDIR /app
 
 COPY . .
 
 

 EXPOSE 8080
 
 RUN ./gradlew build
 
 CMD ["/bin/bash", "-c", "./gradlew appRun"]