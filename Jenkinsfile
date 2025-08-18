pipeline {
  agent any
  environment {
    DOCKER_IMAGE = 'yourdockerid/kafkapooc'
  }
  stages {
    stage('Checkout') {
      steps {
        git branch: 'feature/frontend', url: 'https://github.com/sssinha0/kafkapoc.git'
      }
    }
    // stage('Install Dependencies') {
    //   steps {
    //     sh 'npm ci'
    //   }
    // }
    // stage('Build') {
    //   steps {
    //     sh 'ng build --configuration=production'
    //   }
    // }
    stage('Docker Build & Push') {
      steps {
        script {
          sh "whoami"
          sh "docker build -t kafkapoc:latest ."
        }
      }
    }
    stage('Deploy') {
      steps {
        script{
          sh "docker compose up -d"
        }
        // Your deploy logic here (could be Kubernetes, etc.)
      }
    }
  }
}
