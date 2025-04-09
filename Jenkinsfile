pipeline {
    agent any
    environment {
        IMAGE_NAME = "burtonjong/ensf400fp"
    }
    tools {
        jdk 'JDK_11'
    }
    stages {
        stage('Check Java Version') {

            steps {
                sh 'java -version'
            }

        }

        stage('Build and Run Docker Image') {
            steps {
                script {
                    // Build Docker image using Dockerfile.app
                    sh 'docker build -t $IMAGE_NAME -f Dockerfile.app .'
                    sh 'docker run -d -p 8080:8080 $IMAGE_NAME:latest'
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh 'chmod +x ./gradlew'
                    sh 'docker exec my_container ./gradlew build'
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    sh 'docker exec my_container ./gradlew check' 
                }
            }
        }
    }
}
