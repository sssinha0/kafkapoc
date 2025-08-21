@Library('Shared@feature/shared') _
pipeline {
  agent any
  environment {
    DOCKER_IMAGE = 'yourdockerid/kafkapooc'
  }
  stages {
    stage("hello"){
      steps{
        script{
          clone()
        }
      }
    }
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
    stage("push to Docker Hub"){
      steps {
        script {
          withCredentials([usernamePassword(
                        credentialsId: 'dockerHub',  // This is the ID you set in Jenkins
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]){
          sh  "docker login -u $DOCKER_USER -p $DOCKER_PASS"
          sh "docker image tag kafkapoc:latest $DOCKER_USER/kafkapoc:latest"
          sh "docker push $DOCKER_USER/kafkapoc:latest"
                    }
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
