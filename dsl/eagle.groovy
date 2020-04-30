#!groovy

projects = [
    [folder: "Eagle", repo: "EAGLE/eagle-parent-pom.git", name: "eagle-parent-pom", script: "mavenBuildPodNoMerge"],
    [folder: "Eagle", repo: "ndos/n-common.git", name: "n-common", script: "mavenBuildPodNoMerge"],
]

for(project in projects) { 
    folder("${project.folder}")

    pipelineJob("${project.folder}/${project.name}") {

        description("${project.name}")
        keepDependencies(false)

        parameters {
            stringParam("branch", "develop", "")
            stringParam("scmURL", "git@172.19.90.8:${project.repo}", "")
        }

        configure {
            it / 'properties' / 'com.sonyericsson.rebuild.RebuildSettings' {
                'autoRebuild'('false')
                'rebuildDisabled'('false')
            }
        }

        logRotator {
            numToKeep(50)
        }

        definition {
            cps {
                sandbox(true)
                script("""@Library('jenkins-as-code@master') _
${project.script}(
    branch: "\$params.branch", 
    url: "\$params.scmURL"
)
                """)
            }
        }
    }
}