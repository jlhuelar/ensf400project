FROM openjdk:11
WORKDIR /app

# get chromedriver& pipenv & unzip then delete cache
RUN apt-get update && apt-get install -y \
    chromium chromium-driver \
    python3 python3-pip pipenv \
    wget unzip && \
    rm -rf /var/lib/apt/lists/*


# gradle copy and set permission
COPY gradlew .
COPY gradle ./gradle
RUN chmod +x ./gradlew

COPY build.gradle .

COPY src ./src

# build project
RUN ./gradlew clean build

# install tomcat
RUN wget https://downloads.apache.org/tomcat/tomcat-9/v9.0.100/bin/apache-tomcat-9.0.100.tar.gz \
    && tar -xzf apache-tomcat-9.0.100.tar.gz \
    && mv apache-tomcat-9.0.100 tomcat \
    && rm apache-tomcat-9.0.100.tar.gz



RUN mkdir -p tomcat/webapps/

# copy war file to tomcat webapps
RUN cp build/libs/app.war tomcat/webapps/demo.war



EXPOSE 8080

# run tomcat server
CMD ["sh", "-c", "./tomcat/bin/catalina.sh run"]