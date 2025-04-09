# ENSF400 Final Project

## OVERVIEW

This project extends an open source Java demo application. It uses an automated CI/CD pipeline using Docker, Jenkins and GitHub. 

### Docker

Docker was used to containerize the project using a dockerfile. The dockerfile includes:
- Jenkins with essential plugins
- Docker support
- Java11 and JMeter for testing 
- JUnit and SonarQube support
- All necessary permissions and configurations

# Github

Github was used as the vsc for this project. We used traditional github practices including:
- Branching strategies
- Pull requests
- Commits
- Actions (automation)
- Webhooks (Jenkins)

# Jenkins

We used webhooks to link our github and Jenkins along with using ngrok. Our jenkinsfile was used for:
- Builds
- Tests
- Code quality scans
- Performance Testing
- Deployment


It is all ran through gradle and integrates the following tools:
- Pipenv for Python environment
- JUnit for unit testing
- SonarQube for static analysis
- OWASP DependencyCheck for security vulnerabilities
- JMeter for performance testing
- OWASP ZAP for dynamic security analysis
- Javadoc for documentation

# Running automated builds

In order to run automated builds you must first create a codespace of the project repository.

Go to create a new codespace inside of the codespace webpage and select "boumster/ENSF400-Final-Project" for the repository and "main" for the branch.

You can then ensure that ngork is downloaded within your codespace by using:

```
curl -s https://ngrok-agent.s3.amazonaws.com/ngrok.asc | sudo tee /etc/apt/trusted.gpg.d/ngrok.asc >/dev/null
echo "deb https://ngrok-agent.s3.amazonaws.com buster main" | sudo tee /etc/apt/sources.list.d/ngrok.list
sudo apt update && sudo apt install ngrok
```

You can then go to this website:

https://dashboard.ngrok.com/get-started/setup/windows

Follow the instructions to get set up or just log in using Git

Add the authenticator token by running the line of code under "Run the following command to add your authtoken to the default ngrok.yml configuration file." 

Now you are ready to start building the project, start with:

```
docker compose build
```

```
docker compose up -d
```


# Ports

```4040: Ngrok web interface```
```8080: application```
```8081: Jenkins web interface```
```9000: SonarQube web interface```

Open the Jenkins port and to find the authenticator token use the following lines in your terminal  
<br />
1. ```
   docker ps
   ```
   find the jenkins container
  
   ```
   docker logs <container_id>
   ```

   *************************************************************
   Jenkins initial setup is required. An admin user has been created...
   Admin password: xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
   *************************************************************
   <br />
3. - Use that password to login to Jenkins when you open the port
  
   - Intall the recommended plugins
  
   - Now when you are inside of Jenkins make a new item and name it something like "Demo"
  
   - Select Pipeline
  
   - Click Ok
  
   - Scroll down to Pipeline > Definition and choose:
  
   - Check: GitHub hook trigger for GITScm polling
  
   - Definition: Pipeline script from SCM
  
   - SCM: Git
  
   - Repository URL: https://github.com/boumster/ENSF400-Final-Project
  
   - branch: main
  
   - make sure the jenkins file path is ```jenkins/Jenkinsfile```
  
   - Save and click build now on the left side of the screen to make sure it builds.
  
   - Now any pushes or pull requests should automate the build.
   <br />
4. - You can also access the reports in SonarQube by opening the port

   - Log in with ```username: admin``` ```password: admin```
  
   - You will be asked to make a new password it can be ```username: admin``` ```password: password```
  
   - Now you can find the code reports in SonarQube


