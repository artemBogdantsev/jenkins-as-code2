def call(Map pipelineParams) {

    def label = "maven-pod-${UUID.randomUUID().toString()}"

    podTemplate(
        label: label,
        cloud: 'kubernetes',
        containers: [
            containerTemplate(
                name: 'maven',
                image: 'maven:3.6.3-openjdk-11-slim',
                ttyEnabled: true,
                command: 'cat'
            )
        ],
        volumes: [
            configMapVolume(configMapName: 'nexus-maven', mountPath: '/root/.m2/')
        ]) {
        node(label){
            //container = the container label

            stage ('Debug') {
                echo "Branch:${pipelineParams.branch}"
                echo "scmURL:${pipelineParams.url}"
            }
            stage('Checkout git') {
                git branch: pipelineParams.branch, credentialsId: 'deploy-key-shared-library', url: pipelineParams.url
            }

            stage('Build'){
                container('maven'){
                    sh 'mvn clean package -DskipTests=true'
                }
            }

            stage('Unit Tests') {
                container('maven'){
                    sh 'mvn test'
                }
            }

            stage('Deploy to Nexus') {
                container('maven') {
                    sh 'mvn deploy -DskipTests'
                }
            }
        }
    }
}