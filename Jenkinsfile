pipeline {
    agent none
    options {
        timeout(time: 10, unit: 'MINUTES')
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }
    stages {
        stage('Example') {
            steps {
                echo 'Hello World'
            }
        }
        stage('Checkout') {
            agent { label 'docker' }
            steps {
                git 'https://github.com/joostvdg/keep-watching'
            }
        }
        stage('Maven Build') {
            agent {
                docker {
                    image 'maven:3-alpine'
                    label 'docker'
                    args  '-v /home/joost/.m2:/root/.m2'
                }
            }
            steps {
                sh 'mvn -B clean package'
            }
            post {
                success {
                    junit 'target/surefire-reports/**/*.xml'
                }
            }
        }
        stage('Docker Build') {
            agent { label 'docker' }
            steps {
                sh 'docker build --tag=keep-watching-be .'
            }
        }
    }
    post {
        always {
            echo 'This will always run'
        }
        success {
            echo 'SUCCESS!'
        }
        failure {
            echo "We Failed"
        }
        unstable {
            echo "We're unstable"
        }
        changed {
            echo "Status Changed: [From: $currentBuild.previousBuild.result, To: $currentBuild.result]"
        }
    }
}