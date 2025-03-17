# use gradle 7.6, jdk-11 like video on d2l
FROM gradle:7.6-jdk11 AS builder
WORKDIR /home/gradle/project

COPY . .

RUN apt-get update

# installing necessary dependencies
RUN apt-get install -y python3
RUN apt-get install -y python3-pip
RUN apt-get install -y curl
RUN apt-get install -y unzip

# cleaning up the cache to make the imahe as small as possible
RUN apt-get clean

# clean and build project, avoid the daemon
RUN gradle clean build --no-daemon --refresh-dependencies

# tomcat base img, jdk 11 use again
FROM tomcat:9-jre11

# working dir inside tomcat container
WORKDIR /usr/local/tomcat/webapps

# take out default ROOT app if there is any
RUN rm -rf ROOT

# copy war file that was built from initial stage to tomcat webapps directory, renamed to ROOT
COPY --from=builder /home/gradle/project/build/libs/*.war ./ROOT.war

# Localhost 8080 here
EXPOSE 8080