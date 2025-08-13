pipeline {
    agent any

    environment {
        BUILD_OK = true
     }

    options {
        timestamps()
    }

    stages {
        stage('Cloning reporting service'){
           steps {
              script {
                 dir('reporting-service') {
                     echo "checkout reporting service"
                     git branch: 'main', credentialsId: 'github', url: 'https://github.com/Karthikbhatkarkada/reporting-service.git'
            }
          }
         }
        }
 
        stage('Build Service') {
            steps {
                script {
                    try {
                        dir('reporting-service') {
                            echo " Building microservice..."
                            sh 'cp /var/lib/jenkins/application.properties ${WORKSPACE}/reporting-service/src/main/resources/application.properties'
                            sh 'rm -rf ${WORKSPACE}/reporting-service/src/test'
                            sh 'mvn dependency:resolve -X'
                            sh './mvnw package -DskipTests -X'
                        }
                    } catch (err) {
                        error("Build failed: ${err.message}")
                    }
                }
            }
        }
           
    post {
        success {
            echo "Build completed successfully!"
        }
        failure {
            echo "Build failed!"
        }
        always {
            echo "Cleaning workspace..."
            cleanWs()
        }
    }
}
