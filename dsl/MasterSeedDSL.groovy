#!groovy

// This jobDSL script creates and DevOps/Seeding pipeline, which will act as
// our seeding pipeline for your Jenkins jobs.

folder('DevOps') {
    description('Folder containing configuration and seed jobs')
}

pipelineJob("DevOps/Project Seeding") {
    parameters {
        stringParam("repo", "dasense_platform/ansible-installer.git", "Repo to seed from")
        stringParam("revision", "develop", "Git Token of Jenkins credential to access your Repo")
        stringParam("scriptpath", "jobDSL", "Location of seeds pipeline")
    }

    logRotator {
        numToKeep(50)
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('git@abra.dasense.de:$repo')
                        credentials("deploy-key-shared-library")
                    }

                    branch('$revision')
                }
            }

            // This is the config/seed pipeline within the shared repo
            scriptPath('$scriptpath/Jenkinsfile')
        }
    }
}