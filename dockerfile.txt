FROM gradle:7.6-jdk11 AS build
WORKDIR /home/gradle/project
COPY . .
EXPOSE 8080

CMD ["gradle", "apprun"]

# VOLUME /tmp
# ARG JAVA_OPTS
# ENV JAVA_OPTS=$JAVA_OPTS
# #COPY build/libs/demo-0.0.1.jar demo.jar
# EXPOSE 3000
# ENTRYPOINT exec java $JAVA_OPTS -jar demo.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
#ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar demo.jar
