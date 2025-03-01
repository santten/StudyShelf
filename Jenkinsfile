pipeline {
    agent any

    tools {
        maven "maven399"
    }

    stages {
        stage('git check'){
            steps {
                git branch: 'main', url: 'https://github.com/santten/StudyShelf.git'
            }
        }

        stage('build') {
            steps {
                bat "mvn -Dmaven.test.failure.ignore=true clean package"
            }
        }

        stage('tests') {
                    steps {
                        bat "mvn test jacoco:report"
                    }

                    post {
                        always {
                            junit '**/target/surefire-reports/TEST-*.xml'
                            jacoco execPattern: '**/target/jacoco.exec'
                            jacoco classPattern: '**/target/classes'
                            jacoco sourcePattern: '**/src/main/java'
                            jacoco exclusionPattern: '**/test/**'
                        }
                    }
                }
    }
}