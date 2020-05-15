def call(Map pipelineParams) {

    def label = "maven-pod-${UUID.randomUUID().toString()}"

    podTemplate(
        label: label,
        cloud: 'kubernetes',
        yaml: libraryResource('pod_templates/maven11_docker.yaml')
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

            stage('Docker Build') {
                container('docker') {
                    def pom = readMavenPom file: 'pom.xml'
                    def release_version = pom.version.split('-')

                    echo "Release: ${release_version}"
                    echo "Artifact: ${pom.artifactId}"
                    echo "VERSION: ${pom.version}"

                    /*if (params.RELEASE == true) {
                        currentVersion = vars[0]
                    } else {
                        currentVersion = pom.version
                    }*/

                    if (fileExists("./target/${pom.artifactId}.exec")) {
                        artifact_jar = "${pom.artifactId}.exec"
                    } else {
                        artifact_jar = "${pom.artifactId}-${pom.version}.jar"
                    }

                    withCredentials([usernamePassword(credentialsId: 'docker-registry-user', usernameVariable: 'USERNAME', passwordVariable: 'PASSWORD')]) {
                        sh 'docker login -u $USERNAME -p $PASSWORD nexus-docker.local:32489'
                        sh "docker build --build-arg JAR_FILE=${artifact_jar} -t ${pom.artifactId}:${pom.version} ."
                        sh "docker tag ${pom.artifactId}:${pom.version} nexus-docker.local:32489/eagle/${pom.artifactId}:${pom.version}"
                        sh "docker push nexus-docker.local:32489/eagle/${pom.artifactId}:${pom.version}"
                    }
                }
            }
        }
    }
}