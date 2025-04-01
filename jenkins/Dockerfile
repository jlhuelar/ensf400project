FROM jenkins/jenkins:alpine

USER root

# Install Docker and OpenRC
RUN apk add --no-cache docker openrc

RUN apk add --no-cache openjdk11-jre wget

# Download and install Apache JMeter (version 5.6.2)
RUN wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.2.tgz -O /tmp/jmeter.tgz && \
    tar -xzf /tmp/jmeter.tgz -C /opt && \
    ln -s /opt/apache-jmeter-5.6.2/bin/jmeter /usr/local/bin/jmeter && \
    rm /tmp/jmeter.tgz

# Install necessary plugins
RUN jenkins-plugin-cli --plugins \
    git \
    gradle \
    docker-workflow \
    pipeline-model-definition

# Install Python, pip, and pipenv
RUN apk add --no-cache python3 py3-pip && \
    pip3 install --no-cache-dir pipenv --break-system-packages

# Switch back to Jenkins user
USER jenkins

# Expose Jenkins port
EXPOSE 8081