pipeline {
    agent {
        docker {
            image 'maven:3-alpine'
            label 'docker'
        }
    }
    stages {
        stage('Example') {
            steps {
                echo 'Hello World'
            }
        }
        stage('Checkout') {
            steps {
                git 'https://github.com/joostvdg/keep-watching'
            }
        }
        stage('Example Build') {
            steps {
                sh 'mvn -B clean verify'
            }
        }
    }
    post {
        always {
            echo 'I will always say Hello again!'
        }
    }
}