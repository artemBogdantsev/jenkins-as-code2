#!groovy​

def call(Map config) {
    node('master') {
        stage('Hello') {
            echo('Hello World')
        }

        stage('Version') {
            sh('sudo docker version')
        }
    }
}
