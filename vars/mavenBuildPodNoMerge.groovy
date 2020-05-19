#!groovy​

def call(Map pipelineParams) {

    def label = "maven-pod-${UUID.randomUUID().toString()}"

    podTemplate(
        label: label,
        cloud: 'kubernetes',
        yaml: libraryResource('pod_templates/maven11.yaml')
/* another approach:
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
        ]
*/
    ) {
        node(label){
            //container = the container label

            stage ('Debug') {
                echo "Branch: ${pipelineParams.branch}"
                echo "scmURL: ${pipelineParams.url}"
                echo "tests: ${pipelineParams.tests.toString()}"
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
                if (pipelineParams.tests.toBoolean()) {
                    container('maven'){
                        sh 'mvn test -X'
                    }
                }else{
                    echo "Skip Tests"
                }
            }

            stage('Deploy to Nexus') {
                container('maven') {
                    sh 'mvn deploy -DskipTests'
                }
            }

            stage('Publish Test Coverage Report') {
                step([$class: 'JacocoPublisher',
                      execPattern: '**/build/jacoco/*.exec',
                      classPattern: '**/build/classes',
                      sourcePattern: 'src/main/java',
                      exclusionPattern: 'src/test*'
                ])
            }
        }
    }
}