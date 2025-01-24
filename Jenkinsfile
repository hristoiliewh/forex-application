pipeline {
    agent any

    tools {
            maven 'Maven'
            jdk 'Java-17'
        }

    environment {
        DEV_IMAGE = 'hristoiliewh/forex-application:dev-latest'
        PROD_IMAGE = 'hristoiliewh/forex-application:prod-latest'
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_CREDENTIALS_ID = 'f614c061-ec5d-4d91-8445-5e190bbb03de'
        SERVER_IP = '44.200.113.124'
        SERVER_USER = 'ec2-user'
    }

    stages {
        stage('Checkout') {
            steps {
                script {
                    git branch: 'main', url: 'https://github.com/hristoiliewh/forex-application.git'
                }
            }
        }

        stage('Build') {
            steps {
                script {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    sh 'mvn test'
                }
            }
        }

        stage('Build Docker Image for Development') {
            steps {
                script {
                    sh 'docker build -t $DEV_IMAGE .'
                }
            }
        }

        stage('Push to Docker Registry for Development') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: '$DOCKER_CREDENTIALS_ID', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin $DOCKER_REGISTRY'
                    }
                    sh 'docker push $DEV_IMAGE'
                }
            }
        }

        stage('Build Docker Image for Production') {
            steps {
                script {
                    sh 'docker build -t $PROD_IMAGE .'
                }
            }
        }

        stage('Push to Docker Registry for Production') {
            steps {
                script {
                    withCredentials([usernamePassword(credentialsId: '$DOCKER_CREDENTIALS_ID', passwordVariable: 'DOCKER_PASSWORD', usernameVariable: 'DOCKER_USERNAME')]) {
                        sh 'echo $DOCKER_PASSWORD | docker login -u $DOCKER_USERNAME --password-stdin $DOCKER_REGISTRY'
                    }
                    sh 'docker push $PROD_IMAGE'
                }
            }
        }

        stage('Deploy to Development Server') {
             steps {
                 script {
                         sh """
                         sh $SERVER_USER@$SERVER_IP 'docker pull $DEV_IMAGE && docker run -d -p 8080:8080 $DEV_IMAGE'
                         """
                 }
             }
        }

        stage('Deploy to Production') {
            steps {
                script {
                          sh """
                          ssh $SERVER_USER@$SERVER_IP 'docker pull $PROD_IMAGE && docker run -d -p 8080:8080 $PROD_IMAGE'
                          """
                                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline succeeded'
        }

        failure {
            echo 'Pipeline failed'
        }
    }
}
