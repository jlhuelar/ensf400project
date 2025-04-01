FROM jenkins/jenkins:alpine

USER root

# Install Docker and OpenRC
RUN apk add --update docker openrc

# Install necessary plugins
RUN jenkins-plugin-cli --plugins \
    git \
    gradle \
    docker-workflow \
    pipeline-model-definition

# Switch back to Jenkins user
USER jenkins

# Expose Jenkins port
EXPOSE 8081