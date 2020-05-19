#!groovy​

def call(Map pipelineParams) {

    pipeline {
        agent any
        stages {
            stage ('debug') {
                steps {
                    echo "Branch:${pipelineParams.branch}"
                    echo "scmURL:${pipelineParams.url}"
                }
            }
            stage('checkout git') {
                git branch: pipelineParams.branch, credentialsId: 'deploy-key-shared-library', url: pipelineParams.url
            }

            stage('build') {
                sh 'mvn clean package -DskipTests=true'
            }

            stage ('test') {
                parallel (
                    "unit tests": { sh 'mvn test' },
                    "integration tests": { sh 'mvn integration-test' }
                )
            }
        }
    }
}