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

# Install JMeter Plugins Manager and required plugins
RUN wget https://jmeter-plugins.org/get/ -O /opt/apache-jmeter-5.6.2/lib/ext/jmeter-plugins-manager.jar && \
    wget https://repo1.maven.org/maven2/kg/apc/cmdrunner/2.3/cmdrunner-2.3.jar -O /opt/apache-jmeter-5.6.2/lib/cmdrunner-2.3.jar && \
    java -cp /opt/apache-jmeter-5.6.2/lib/ext/jmeter-plugins-manager.jar org.jmeterplugins.repository.PluginManagerCMDInstaller && \
    chmod +x /opt/apache-jmeter-5.6.2/bin/PluginsManagerCMD.sh && \
    /opt/apache-jmeter-5.6.2/bin/PluginsManagerCMD.sh install jpgc-graphs-basic jpgc-functions jpgc-casutg && \
    wget https://jmeter-plugins.org/files/packages/jpgc-graphs-basic-2.0.zip -O /tmp/jpgc-graphs-basic.zip && \
    unzip -o /tmp/jpgc-graphs-basic.zip -d /opt/apache-jmeter-5.6.2/ && \
    rm /tmp/jpgc-graphs-basic.zip

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
    mkdir -p /var/lib/jmeter/temp && \
    mkdir -p /opt/apache-jmeter-5.6.2/temp && \
    chown -R jenkins:jenkins /var/lib/jmeter && \
    chown -R jenkins:jenkins /opt/apache-jmeter-5.6.2/temp && \
    chmod -R 755 /var/lib/jmeter && \
    chmod -R 755 /opt/apache-jmeter-5.6.2/temp

# Create JMeter properties file with custom settings
RUN echo "jmeter.reportgenerator.temp_dir=/var/lib/jmeter/temp" >> /opt/apache-jmeter-5.6.2/bin/user.properties

# Ensure Jenkins workspace has proper permissions
RUN mkdir -p /var/jenkins_home/workspace && \
    chmod -R 777 /var/jenkins_home/workspace

# Switch back to Jenkins user
USER jenkins

# Expose Jenkins port
EXPOSE 8081