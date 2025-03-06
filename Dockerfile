FROM alpine/java:22-jdk
WORKDIR /app

COPY . .
RUN ./gradlew build

CMD ["java", "-jar", "build/libs/ensf400-final-project.jar"]