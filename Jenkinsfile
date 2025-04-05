pipeline {
    agent any

    environment {
        IMAGE_NAME = 'alvinlong2311/demo-app'
        COMMIT_HASH = "${env.GIT_COMMIT}"
        SONARQUBE_ENV = 'alvinlong2311' // Name configured in Jenkins global config
    }

    tools {
        jdk 'JDK-17'           // Name as configured in Jenkins
        maven 'Maven-3.8.6'    // Or Gradle if that's your build tool
    }

    options {
        skipDefaultCheckout(true)
        timestamps()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                sh 'mvn clean install'
            }
            post {
                success {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Code Quality - SonarQube') {
            steps {
                withSonarQubeEnv("${SONARQUBE_ENV}") {
                    sh 'mvn sonar:sonar'
                }
            }
        }

        stage('Security Analysis - OWASP DependencyCheck') {
            steps {
                sh '''
                mkdir -p dependency-check
                dependency-check/bin/dependency-check.sh --project demo-app --scan . --format ALL --out dependency-check
                '''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'dependency-check/**'
                }
            }
        }

        stage('Performance Testing - JMeter') {
            steps {
                sh '''
                jmeter -n -t jmeter/test-plan.jmx -l jmeter/results.jtl
                '''
            }
            post {
                always {
                    archiveArtifacts artifacts: 'jmeter/results.jtl'
                }
            }
        }

        stage('Generate Javadoc') {
            steps {
                sh 'mvn javadoc:javadoc'
            }
            post {
                always {
                    archiveArtifacts artifacts: '**/target/site/apidocs/**/*'
                }
            }
        }

        stage('Docker Build & Push') {
            steps {
                script {
                    def tag = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}".replaceAll('/', '-')
                    sh "docker build -t ${IMAGE_NAME}:${tag} ."
                    sh "docker tag ${IMAGE_NAME}:${tag} ${IMAGE_NAME}:latest"
                    withCredentials([usernamePassword(credentialsId: 'dockerhub-creds', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                        sh '''
                        echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin
                        docker push ${IMAGE_NAME}:${tag}
                        docker push ${IMAGE_NAME}:latest
                        '''
                    }
                }
            }
        }

        stage('Deploy to Codespace Environment') {
            steps {
                sh './scripts/deploy.sh' // You need to create this if not already present
            }
        }

    }

    post {
        failure {
            mail to: 'team@example.com',
                 subject: "Pipeline failed: ${env.JOB_NAME} [${env.BUILD_NUMBER}]",
                 body: "Check Jenkins for details: ${env.BUILD_URL}"
        }
    }
}
