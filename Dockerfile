# Use the correct gradle jdk image to avoid versioning issues
FROM gradle:7.6.1-jdk11

# Make the working directory
RUN mkdir /home/app
WORKDIR /home/app
COPY . /home/app


EXPOSE 8080 

# Start the application 
CMD [ "./gradlew", "apprun" ]