# using slim cuz its more lightweight than the normal jdk
FROM openjdk:17-jdk-slim

# default directory going to be desktop_app
WORKDIR /desktop_app

RUN apt-get update

# installing necessary dependencies
RUN apt-get install -y python3
RUN apt-get install -y python3-pip
RUN apt-get install -y curl
RUN apt-get install -y unzip

# cleaning up the cache to make the imahe as small as possible
RUN apt-get clean