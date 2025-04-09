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
                    // Build the Docker image
                    sh 'docker build -t $IMAGE_NAME -f Dockerfile.app .'

                    // Run the container in detached mode
                    def containerId = sh(script: 'docker run -d -p 8080:8080 $IMAGE_NAME:latest ./gradlew -Dlog4j2.disableJmx=true -Dlog4j.shutdownHookEnabled=false apprun', returnStdout: true).trim()
                    env.CONTAINER_ID = containerId
                }
            }
        }

        // stage('Run Tests') {
        //     steps {
        //         script {
        //             // Run tests inside the container
        //             sh "docker exec ${env.CONTAINER_ID} ./gradlew test"
        //         }
        //     }
        // }

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
    }
    post {
        always {
            script {
                if (env.CONTAINER_ID) {
                    sh "docker stop ${env.CONTAINER_ID}"
                    sh "docker rm ${env.CONTAINER_ID}"
                } else {
                    echo "No container to clean up"
                }
            }
        }
    }
}
