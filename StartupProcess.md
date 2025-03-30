# CI/CD Setup with SonarQube, Jenkins, Demo App, and OWASP ZAP

This guide provides step-by-step instructions for setting up a complete CI/CD environment using Docker containers. The setup includes SonarQube for code quality analysis, Jenkins for CI/CD pipeline execution, a demo application, and OWASP ZAP for security testing.

## Prerequisites

- Docker installed on your machine
- Terminal/command line access
- Sufficient disk space for Docker images and volumes

## Creating a Docker Network

First, create a dedicated Docker network to enable communication between the containers:

```bash
docker network create ci-network
```

## Setting Up SonarQube

1. Run SonarQube in a Docker container:

```bash
docker run -d --name sonarqube --network ci-network -p 9000:9000 \
  -v sonarqube_conf:/opt/sonarqube/conf \
  -v sonarqube_data:/opt/sonarqube/data \
  -v sonarqube_extensions:/opt/sonarqube/extensions \
  sonarqube:latest
```

2. Access SonarQube at http://localhost:9000
3. Log in with the default credentials:
   - Username: `admin`
   - Password: `admin`
4. Create your own credentials when prompted for security
5. Create a project called `ENSF400`
6. Generate a token for your project and replace it in your `gradle.properties` file

## Setting Up Jenkins

1. Run Jenkins in a Docker container:

```bash
docker run -d --name jenkins --network ci-network -p 8090:8080 \
  -v "$(pwd)/jenkins_home:/var/jenkins_home" \
  jenkins/jenkins:lts
```

2. Retrieve the initial admin password:

```bash
docker exec -it jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

3. Access Jenkins at http://localhost:8090 and log in using the retrieved password
4. Install recommended plugins when prompted
5. Install pipenv in the Jenkins container:

```bash
docker exec -u root -it jenkins bash -c "apt-get update && apt-get install -y pipenv"
```

## Deploying the Demo Application

1. Run the demo application:

```bash
docker run -d --name demo-app --network ci-network -p 8080:8080 \
  charbel123456/ensf400-finalproject:HEAD-ab00219
```

2. Access the application at http://localhost:8080/demo

## Setting Up OWASP ZAP

1. Generate an API key for ZAP:

```bash
openssl rand -hex 16
```

2. Run ZAP in daemon mode:

```bash
docker run -d --name zap --network ci-network -p 9888:8080 ghcr.io/zaproxy/zaproxy:stable zap.sh -daemon -host 0.0.0.0 -port 8080 -config api.key=Your_Api_Key -config api.addrs.addr.name=.* -config api.addrs.addr.regex=true 
```

3. Replace `YOUR_GENERATED_API_KEY` with the key you generated in step 1
4. Update the ZAP API key in your Jenkinsfile to match your generated key

## Configuration Updates

### Updating URLs in build.gradle

Ensure that your `build.gradle` file contains the correct URLs for SonarQube and Jenkins:

```groovy
sonarqube {
    properties {
        property "sonar.host.url", "http://sonarqube:9000"
        // other sonar properties...
    }
}

// For Jenkins-related configurations:
jenkins {
    serverUrl = "http://jenkins:8090"
    // other jenkins properties...
}
```

## Running the CI/CD Pipeline

1. Configure your Jenkins pipeline using the Jenkinsfile from your project
2. Trigger a build and verify that all steps are passing
3. The pipeline should successfully execute up to the API Tests stage

## Troubleshooting

If you encounter issues with any of the containers:

1. Check container logs:
   ```bash
   docker logs container_name
   ```

2. Verify network connectivity:
   ```bash
   docker network inspect ci-network
   ```

3. Ensure all services are running:
   ```bash
   docker ps
   ```

4. For Jenkins pipeline failures, check the specific stage that failed and review the logs

If tests are not passing up to the API Tests stage, review your configuration and ensure all services are correctly integrated.
