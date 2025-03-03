pipeline {
  agent any
    tools {
      jdk 'JDK17'
    }
    stages {
      stage('checking'){
        steps {
          git branch: 'main', url: 'https://github.com/santten/StudyShelf.git'
        }
      }

      stage('build'){
        steps {
          bat 'mvn clean install'
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
                   exclusionPattern: '**/test/**'
          }
        }
      }
    }
}