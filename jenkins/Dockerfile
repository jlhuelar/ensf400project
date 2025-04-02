FROM jenkins/jenkins:alpine

USER root

# Install Docker and OpenRC
RUN apk add --no-cache docker openrc

# Install OpenJDK 11 and wget
RUN apk add --no-cache openjdk11-jre wget

# Download and install Apache JMeter (version 5.6.2)
RUN wget https://archive.apache.org/dist/jmeter/binaries/apache-jmeter-5.6.2.tgz -O /tmp/jmeter.tgz && \
    tar -xzf /tmp/jmeter.tgz -C /opt && \
    ln -s /opt/apache-jmeter-5.6.2/bin/jmeter /usr/local/bin/jmeter && \
    rm /tmp/jmeter.tgz

# Install JMeter Plugins Manager
RUN wget https://jmeter-plugins.org/get/ -O /opt/apache-jmeter-5.6.2/lib/ext/jmeter-plugins-manager.jar && \
    wget https://jmeter-plugins.org/files/cmdrunner/2.2/cmdrunner-2.2.jar -O /opt/apache-jmeter-5.6.2/lib/cmdrunner-2.2.jar && \
    java -cp /opt/apache-jmeter-5.6.2/lib/ext/jmeter-plugins-manager.jar org.jmeterplugins.repository.PluginManagerCMDInstaller && \
    /opt/apache-jmeter-5.6.2/bin/PluginsManagerCMD.sh install jmeter-plugins-corrected-results-collector

# Install Jenkins plugins
RUN jenkins-plugin-cli --plugins \
    git \
    gradle \
    docker-workflow \
    pipeline-model-definition

# Install Python, pip, and pipenv
RUN apk add --no-cache python3 py3-pip && \
    pip3 install --no-cache-dir pipenv --break-system-packages

# Create and set permissions for JMeter work directories
RUN mkdir -p /var/lib/jmeter/reports && \
    mkdir -p /var/lib/jmeter/results && \
    chown -R jenkins:jenkins /var/lib/jmeter && \
    chmod -R 755 /var/lib/jmeter

# Switch back to Jenkins user
USER jenkins

# Expose Jenkins port
EXPOSE 8081