# Use the correct gradle jdk image to avoid versioning issues
FROM gradle:7.6.4-jdk11

# Make the working directory
RUN mkdir /home/app
COPY . /home/app
WORKDIR /home/app


EXPOSE 8080 

# Start the application, speed up the image build by excluding the daemon 
CMD [ "gradle", "--no-daemon", "apprun"]