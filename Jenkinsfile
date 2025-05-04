pipeline {
  agent any
     environment {
                DOCKERHUB_CREDENTIALS_ID = 'Docker_Hub'
                DOCKERHUB_REPO = 'armasnevolainen/study_shelf'
                DOCKER_IMAGE_TAG = 'latest_v1'
            }
    tools {
      jdk 'JDK17'
    }
    stages {

      stage('checking'){
        steps {
          git branch: 'armas-branch', url: 'https://github.com/santten/StudyShelf.git'
        }
      }

      stage('build'){
        steps {
          bat 'mvn clean compile -DskipTests'
        }
      }

      stage('Prepare Test Environment') {
         steps {
            // Create test resources directory if it doesn't exist (Windows syntax)
            bat 'if not exist "src\\test\\resources" mkdir src\\test\\resources'

            // Create the correctly named properties file
            bat 'echo google.translate.api.key=DUMMY_KEY_FOR_TESTS > src\\test\\resources\\translate-api.properties'
          }
      }

      stage('tests') {
                   steps {
                      bat "mvn test jacoco:report"
                  }
                   post {
                          always {
                            junit '**/target/surefire-reports/TEST-*.xml'
                            jacoco execPattern: '**/target/jacoco.exec',
                                   classPattern: '**/target/classes',
                                   sourcePattern: '**/src/main/java',
                                   exclusionPattern: '**/test/**, **/infrastructure/repository/**, **/infrastructure/config/**, **/presentation/**'
                          }
                        }
              }

      stage('Build Docker Image') {
            steps {
              script {
               bat "docker build -t ${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG} --build-arg SKIP_CREDENTIALS=true ."
              }
            }
          }

          stage('Push Docker Image to Docker Hub') {
            steps {
              script {
                docker.withRegistry('https://index.docker.io/v1/', DOCKERHUB_CREDENTIALS_ID) {
                  docker.image("${DOCKERHUB_REPO}:${DOCKER_IMAGE_TAG}").push()
                }
              }
            }
          }

          stage('Cleanup') {
            steps {
              bat '''
                if exist temp\\credentials (
                  rmdir /S /Q temp\\credentials
                )
              '''
            }
          }
    }
}