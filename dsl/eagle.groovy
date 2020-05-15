#!groovy

def projects = [
    "eagle-parent-pom": [folder: "Eagle", level: 0, repo: "EAGLE/eagle-parent-pom.git", script: "mavenBuildPodNoMerge"],
    "n-microservice-parent": [folder: "Eagle", level: 1, repo: "ndos/n-microservice-parent.git", script: "mavenBuildPodNoMerge"],
    "n-common": [folder: "Eagle", level: 1, repo: "ndos/n-common.git", script: "mavenBuildPodNoMerge"],
    "n-failsafe": [folder: "Eagle", level: 1, repo: "ndos/n-failsafe.git", script: "mavenBuildPodNoMerge"],
    "n-config": [folder: "Eagle", level: 1, repo: "ndos/n-config.git", script: "mavenBuildPodNoMerge"],
    "e-queryparser": [folder: "Eagle", level: 1, repo: "EAGLE/e-queryparser.git", script: "mavenBuildPodNoMerge"],
    "e-config": [folder: "Eagle", level: 1, repo: "EAGLE/e-config.git", script: "mavenBuildPodNoMerge"],
    "n-hbase": [folder: "Eagle", level: 2, repo: "ndos/n-hbase.git", script: "mavenBuildPodNoMerge"],
    "n-index": [folder: "Eagle", level: 2, repo: "ndos/n-index.git", script: "mavenBuildPodNoMerge"],
    "n-hdfs": [folder: "Eagle", level: 2, repo: "ndos/n-hdfs.git", script: "mavenBuildPodNoMerge"],
    "n-kafka": [folder: "Eagle", level: 2, repo: "ndos/n-kafka.git", script: "mavenBuildPodNoMerge"],
    "e-ingest-common": [folder: "Eagle", level: 2, repo: "EAGLE/e-ingest-common.git", script: "mavenBuildPodNoMerge"],
    "n-hbase-impl": [folder: "Eagle", level: 3, repo: "ndos/n-hbase-impl.git", script: "mavenBuildPodNoMerge"],
    "n-auth": [folder: "Eagle", level: 3, repo: "ndos/n-auth.git", script: "mavenBuildPodNoMerge"],
    "n-auth-ldap": [folder: "Eagle", level: 4, repo: "ndos/n-auth-ldap.git", script: "mavenBuildPodNoMerge"],
    "n-index-elastic": [folder: "Eagle", level: 4, repo: "ndos/n-index-elastic.git", script: "mavenBuildPodNoMerge"],
    "n-index-spaces": [folder: "Eagle", level: 4, repo: "ndos/n-index-spaces.git", script: "mavenBuildPodNoMerge"],
    "e-index-document": [folder: "Eagle", level: 4, repo: "EAGLE/e-index-document.git", script: "mavenBuildPodNoMerge"],
    "n-converter": [folder: "Eagle", level: 5, repo: "ndos/n-converter.git", script: "mavenBuildPodNoMerge"],
    "e-hbase-document": [folder: "Eagle", level: 5, repo: "EAGLE/e-hbase-document.git", script: "mavenBuildPodNoMerge"],
    "e-autocomplete": [folder: "Eagle", level: 5, repo: "EAGLE/e-autocomplete.git", script: "mavenBuildPodNoMerge"],
    "n-spaces": [folder: "Eagle", level: 5, repo: "ndos/n-spaces.git", script: "mavenBuildPodNoMerge"],
    "e-index-document-intrafind": [folder: "Eagle", level: 5, repo: "EAGLE/e-index-document-intrafind.git", script: "mavenBuildPodNoMerge"],
    "e-path-mapping": [folder: "Eagle", level: 6, repo: "EAGLE/e-path-mapping.git", script: "mavenBuildPodNoMerge"],
    "n-converter-tika": [folder: "Eagle", level: 6, repo: "ndos/n-converter-tika.git", script: "mavenBuildPodNoMerge"],
    "e-comments": [folder: "Eagle", level: 7, repo: "EAGLE/e-comments.git", script: "mavenBuildPodNoMerge"],
    "e-spaces-documents": [folder: "Eagle", level: 7, repo: "EAGLE/e-spaces-documents.git", script: "mavenBuildPodNoMerge"],
    "n-mongodb-impl": [folder: "Eagle", level: 7, repo: "ndos/n-mongodb-impl.git", script: "mavenBuildPodNoMerge"],
    "e-likes": [folder: "Eagle", level: 8, repo: "EAGLE/e-likes.git", script: "mavenBuildPodNoMerge"],
    "e-ingest": [folder: "Eagle", level: 8, repo: "EAGLE/e-ingest.git", script: "mavenDockerBuildPodNoMerge"],
    "e-backend": [folder: "Eagle", level: 9, repo: "EAGLE/e-backend.git", script: "mavenBuildPodNoMerge"],
    "e-ingest-tika": [folder: "Eagle", level: 9, repo: "EAGLE/e-ingest-tika.git", script: "mavenBuildPodNoMerge"],
    "n-mapreduce-jobs": [folder: "Eagle", level: 10, repo: "ndos/n-mapreduce-jobs.git", script: "mavenBuildPodNoMerge"],
    "e-rabbitmq-hbase": [folder: "Eagle", level: 11, repo: "EAGLE/e-rabbitmq-hbase.git", script: "mavenBuildPodNoMerge"],
    "e-ingest-scanner": [folder: "Eagle", level: 11, repo: "EAGLE/e-ingest-scanner.git", script: "mavenBuildPodNoMerge"],
    "eagle-installation": [folder: "Eagle", level: 11, repo: "EAGLE/eagle-installation.git", script: "mavenBuildPodNoMerge"],
]

projects.each{ project_name, project_settings ->

    folder(project_settings.folder)

    pipelineJob(project_settings.folder + '/' + project_name) {

        description(project_name)
        keepDependencies(false)

        parameters {
            stringParam("branch", "develop", "")
            stringParam("scmURL", "git@172.19.90.8:" + project_settings.repo, "")
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
                names('Eagle/' + it)
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