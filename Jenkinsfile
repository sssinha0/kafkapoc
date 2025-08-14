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
    stage('Install Dependencies') {
      steps {
        sh 'npm ci'
      }
    }
    stage('Build') {
      steps {
        sh 'ng build --configuration=production'
      }
    }
    stage('Docker Build & Push') {
      steps {
        script {
          sh "whoami"
          sh "docker build -t kafkapoc:latest ."
          // sh "echo $DOCKER_PASSWORD | sudo docker login -u $DOCKER_USERNAME --password-stdin"
          // sh "sudo docker push $DOCKER_IMAGE"
        }
      }
    }
    stage('Deploy') {
      steps {
        script{
          sh: "docker run -d -p 80:80 kafkapoc:latest"
        }
        // Your deploy logic here (could be Kubernetes, etc.)
      }
    }
  }
}
