#!groovy

def projects = [
    "Olezhik-parent-pom": [folder: "Olezhik", level: 0, repo: "<...>.git", script: "mavenBuildPodNoMerge"],
    "Olezhik-distribution": [folder: "Olezhik", level: 10, repo: "<...>.git", script: "mavenBuildPodNoMerge"],
    "Olezhik-installation": [folder: "Olezhik", level: 11, repo: "<...>.git", script: "mavenBuildPodNoMerge"],
]

projects.each{ project_name, project_settings ->

    folder(project_settings.folder)

    pipelineJob(project_settings.folder + '/' + project_name) {

        description(project_name)
        keepDependencies(false)

        parameters {
            stringParam("branch", "develop", "")
            stringParam("scmURL", "git@172.17.0.1:" + project_settings.repo, "")
            booleanParam('tests', true, "Enable Unit Tests")
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
${project_settings.script}(
    branch: "\$params.branch", 
    url: "\$params.scmURL",
    tests: "\$params.tests"
)
                """)
            }
        }
    }
}

for (level in 1..11) {
    def level_jobs = projects.findAll{ id, project ->
        project.level == level
    }

    if (level < 10) {
        level = "0" + level
    }

    listView("Level " + level) {
        description("")
        filterBuildQueue()
        filterExecutors()
        jobs {
            level_jobs*.key.each{
                names('Olezhik/' + it)
            }
        }
        recurse(true)
        columns {
            status()
            weather()
            name()
            lastSuccess()
            lastFailure()
            lastDuration()
            buildButton()
        }
    }
}