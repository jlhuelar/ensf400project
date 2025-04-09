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
                bat 'java -version'
            }

        }

        stage('Checkout') {
            steps {
                // Checkout your Git repository
                checkout scm
            }
        }
        
        stage('Build Docker Image') {
            steps {
                script {
                    // Build Docker image using Dockerfile.app
                    sh 'docker build -t $IMAGE_NAME -f Dockerfile.app .'
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
                    sh './gradlew runAllTests'
                }
            }
        }
    }
}
