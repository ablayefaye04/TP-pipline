// Pipeline declaratif Jenkins : meme chaine que GitHub Actions (build -> test -> deploy)
pipeline {
    agent any

    tools {
        // Nom de l'installation Maven declaree dans Manage Jenkins > Tools
        maven 'Maven3'
    }

    options {
        timeout(time: 15, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    triggers {
        // Verifie le depot Git toutes les ~2 minutes (alternative simple au webhook)
        pollSCM('H/2 * * * *')
    }

    stages {
        stage('Build') {
            steps {
                sh 'mvn -B clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn -B verify'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                    archiveArtifacts artifacts: 'target/site/jacoco/**', allowEmptyArchive: true
                }
            }
        }

        stage('Archive') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Deploy') {
            // "branch 'main'" ne marche qu'en pipeline multibranch ; ici on teste la variable Git
            when {
                expression { env.GIT_BRANCH == 'origin/main' || env.BRANCH_NAME == 'main' }
            }
            steps {
                sh 'chmod +x deploy.sh && ./deploy.sh'
            }
        }
    }

    post {
        success { echo 'Pipeline OK : build, tests et deploiement reussis' }
        failure { echo 'Pipeline en echec : voir la Console Output' }
    }
}
