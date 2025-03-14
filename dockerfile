# Use OpenJDK 8 image as base
FROM openjdk:8-jdk-alpine

# Set working directory in the container
WORKDIR /desktop_app

# Install necessary dependencies: bash, curl, unzip, and Python 3
RUN apk add --no-cache bash curl unzip python3 py3-pip

# Install Gradle manually
RUN curl -L https://services.gradle.org/distributions/gradle-7.5.1-bin.zip -o /tmp/gradle.zip && \
    unzip /tmp/gradle.zip -d /opt && \
    rm /tmp/gradle.zip && \
    ln -s /opt/gradle-7.5.1 /opt/gradle

# Set Gradle environment variables
ENV GRADLE_HOME=/opt/gradle-7.5.1
ENV PATH="${GRADLE_HOME}/bin:${PATH}"

# Verify Gradle installation
RUN gradle -v

# Copy the local repository contents into the container at /app
COPY . /desktop_app/

# Install Chromedriver
RUN curl -sSL https://chromedriver.storage.googleapis.com/113.0.5672.63/chromedriver_linux64.zip -o chromedriver.zip \
    && unzip chromedriver.zip \
    && mv chromedriver /usr/local/bin/ \
    && rm chromedriver.zip

# Try to install pipenv and dependencies but continue if it fails
RUN pip3 install --upgrade pip && \
    pip3 install pipenv==2021.11.23 && \
    (pipenv install --system --deploy)

# Expose port 8080 for the web application
EXPOSE 8080

# Run the web application
#CMD ["./gradlew", "apprun"]