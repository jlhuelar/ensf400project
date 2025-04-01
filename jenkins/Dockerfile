FROM jenkins/jenkins:alpine

USER root

# Install Docker and OpenRC
RUN apk add --no-cache docker openrc

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