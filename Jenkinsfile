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
                    
                    // Run the container in detached mode and capture the container ID
                    def containerId = sh(script: 'docker run -p 8080:8080 $IMAGE_NAME:latest', returnStdout: true).trim()
                    
                    // Set the container ID as an environment variable for later use
                    env.CONTAINER_ID = containerId
                }
            }
        }

        // stage('Run Build Tests') {
        //     steps {
        //         script {
        //             sh 'chmod +x ./gradlew'
        //             // Run gradlew inside the container using the container ID
        //             sh "docker exec ${env.CONTAINER_ID} ./gradlew build"
        //             sh "docker exec ${env.CONTAINER_ID} ./gradlew check"
        //         }
        //     }
        // }

        // stage('Run Tests') {
        //     steps {
        //         script {
        //             // Run tests inside the container using the container ID
        //             sh "docker exec ${env.CONTAINER_ID} ./gradlew -Dlog4j2.disableJmx=true -Dlog4j.shutdownHookEnabled=false apprun"
        //         }
        //     }
        // }

        post {
            always {
                sh 'docker stop $CONTAINER_ID || true'
                sh 'docker rm $CONTAINER_ID || true'
            }
        }   
    }
}
