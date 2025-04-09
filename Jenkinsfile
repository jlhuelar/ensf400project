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
                    sh 'docker run -it -p 8080:8080 burtonjong/ensf400fp:latest'
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh 'chmod +x ./gradlew'
                    sh './gradlew apprun'
                }
            }
        }

        stage('Run Tests') {
            steps {
                script {
                    sh './gradlew check'
                }
            }
        }
    }
}
