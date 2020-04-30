def call(Map pipelineParams) {

    podTemplate(
        name: 'maven-pod',
        label: 'maven-pod',
        containers: [
                containerTemplate(name: 'maven', image: 'maven:3.6.3-openjdk-11-slim')
        ],
        {
            node('maven-pod'){
                //container = the container label

                stage ('debug') {
                    steps {
                        echo "Branch:${pipelineParams.branch}"
                        echo "scmURL:${pipelineParams.url}"
                    }
                }
                stage('checkout git') {
                    steps {
                        git branch: pipelineParams.branch, credentialsId: 'deploy-key-shared-library', url: pipelineParams.url
                    }
                }

                stage('Build'){
                    container('maven'){
                        sh 'mvn clean package -DskipTests=true'
                    }
                }
            }
        })
}