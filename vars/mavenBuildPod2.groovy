def call(Map pipelineParams) {

    def label = "worker-pod-${UUID.randomUUID().toString()}"

    podTemplate(
        label: label,
        cloud: 'kubernetes',
        containers: [
            containerTemplate(
                name: 'maven',
                image: 'maven:3.6.3-openjdk-11-slim',
                ttyEnabled: true,
                resourceRequestCpu: "500m",
                resourceLimitCpu: "1000m",
                resourceRequestMemory: "1Gi",
                resourceLimitMemory: "2Gi",
                command: 'cat'
            )
    ]) {
        node(label) {
            stage('Get a Maven project') {
                git 'https://github.com/jenkinsci/kubernetes-plugin.git'
                try {
                    container('maven') {
                        stage('Build a Maven project') {
                            sh 'mvn -B clean install'
                        }
                    }
                } catch (Exception e) {
                    containerLog 'maven'
                    throw e
                }

            }
        }
    }
}