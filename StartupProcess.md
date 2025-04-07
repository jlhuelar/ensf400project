# CI/CD Environment Startup Guide

This guide explains how to set up your complete CI/CD environment using Docker Compose, configure ZAP with a custom API key, set up SonarQube, and configure Jenkins for automated builds on pull requests.

---

## Prerequisites

- **Docker & Docker Compose:** Make sure both are installed.
- **OpenSSL:** Required for generating a random API key.
- **Basic knowledge of Git, Jenkins, and SonarQube.**

---

## Step 1: Generate a Random API Key

Generate a new API key using the following command:

```bash
openssl rand -hex 16
```

Example output:
```
a1b2c3d4e5f67890123456789abcdef0
```

Note: Remember this key for use in later steps.

## Step 2: Update the Jenkinsfile

Edit your Jenkinsfile to replace the hard-coded API key with your new one in two locations.

### Replace in Line 70
Before:
```
sh 'curl "http://zap:8080/JSON/core/action/newSession/?apikey=e02e6c167c1018f6f087f95a2d64e56c" -s --proxy zap:8080'
```

After:
```
sh 'curl "http://zap:8080/JSON/core/action/newSession/?apikey=<YOUR_API_KEY>" -s --proxy zap:8080'
```

### Replace in Line 150
Before:
```
sh 'curl "http://zap:8080/OTHER/core/other/htmlreport/?apikey=e02e6c167c1018f6f087f95a2d64e56c" --proxy zap:8080 > build/reports/zap/zap_report.html'
```

After:
```
sh 'curl "http://zap:8080/OTHER/core/other/htmlreport/?apikey=<YOUR_API_KEY>" --proxy zap:8080 > build/reports/zap/zap_report.html'
```

Remember: Replace `<YOUR_API_KEY>` with the key you generated.

## Step 3: Update the Docker Compose File

Edit your docker-compose.yml file to update the ZAP container configuration with your new API key.

### Update ZAP Container Command (Line 46)
Before:
```
command: >
  zap.sh -daemon -host 0.0.0.0 -port 8080 -config api.key=e02e6c167c1018f6f087f95a2d64e56c -config api.addrs.addr.name=.* -config api.addrs.addr.regex=true 
```

After:
```
command: >
  zap.sh -daemon -host 0.0.0.0 -port 8080 -config api.key=<YOUR_API_KEY> -config api.addrs.addr.name=.* -config api.addrs.addr.regex=true 
```

Note: Replace `<YOUR_API_KEY>` with your generated API key.

## Step 4: Start the Environment

Bring up your Docker environment by running:

```bash
docker-compose up -d
```

After starting, verify that the following ports are set:

- Demo App: Port 8080
- Jenkins: Port 8090
- SonarQube: Port 9000

## Step 5: Configure SonarQube

### 5.1 Access SonarQube
Open your browser and navigate to:
http://localhost:9000

### 5.2 Create a Project
Create a new project with the exact title: ENSF400

### 5.3 Generate a User Token
- Go to My Account in SonarQube.
- Create a new user token.

### 5.4 Update Gradle Configuration
Update your Gradle configuration files with the newly generated SonarQube token:

In gradle.properties:
```
sonar.token=<YOUR_SONAR_TOKEN>
```

In build.gradle (around line 450):
```
String token = "<YOUR_SONAR_TOKEN>"  // Or retrieve from Gradle properties / env variable
```

Replace: `<YOUR_SONAR_TOKEN>` with the token you created.

## Step 6: Configure Jenkins

### 6.1 Access Jenkins
Open your browser and navigate to:
http://localhost:8090

### 6.2 Retrieve the Initial Admin Password
Run the following command to get the password:

```bash
docker exec -it jenkins cat /var/jenkins_home/secrets/initialAdminPassword
```

Use the output to unlock Jenkins.

### 6.3 Set Up Your Jenkins Project
- Create a new project: Use your GitHub repository URL.
- Configure SCM: Set up source control management to monitor your repository.
- Enable Webhooks:
  - Configure the project to use GitHub webhooks.
  - Ensure that automated builds are triggered on pull requests.
- Verify: Confirm that the webhook in your GitHub repository points to your Jenkins endpoint (e.g., http://<your-domain-or-ip>:8090/github-webhook/).

## Final Notes

### API Key:
All instances of the API key in your configuration (Jenkinsfile and Docker Compose file) must be updated to your generated key.

### Ports:
Ensure that ports 8080, 8090, and 9000 are available and not blocked by other services.

### Security Considerations:
When adjusting settings (e.g., permissions in Jenkins, or disabling CSRF protection), make sure to understand the security implications.

## Conclusion

Following this guide, you have:

- Generated a custom API key.
- Updated the Jenkinsfile and Docker Compose configuration with your API key.
- Started your Docker environment.
- Configured SonarQube with a new project and updated tokens.
- Set up Jenkins to trigger builds via GitHub webhooks.

Your CI/CD environment should now be fully operational. Happy building!