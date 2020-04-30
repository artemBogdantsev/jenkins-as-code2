#!groovy

projects = [
    [folder: "Dasense Platform", name: "Dasense Pipeline Setup", script: "nightlyBuild"],
    [folder: "Dasense Platform", name: "Dasense Docker Images", script: "printDockerVersion"]
]

for(project in projects) { 
    folder("${project.folder}")

    pipelineJob("${project.folder}/${project.name}") {

        description("${project.name}")
        keepDependencies(false)

        if(project.script == "nightlyBuild") {
            parameters {
                stringParam("APPSERVERNAME", "vg-tst-app", "")
                stringParam("APPSERVERIP", "172.19.90.185", "")
                stringParam("CSSERVERNAME", "vg-tst-cs", "")
                stringParam("CSSERVERIP", "172.19.90.186", "")
                stringParam("BRANCH", "RELEASE", "")
                stringParam("VERSION", "3.6", "")
            }
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
${project.script}()
                """)
            }
        }
    }
}