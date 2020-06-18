#!groovy

// This jobDSL script creates and DevOps/Seeding pipeline, which will act as
// our seeding pipeline for your Jenkins jobs.

folder('DevOps') {
    description('Folder containing configuration and seed jobs')
}

pipelineJob("DevOps/Project Seeding") {
    parameters {
        stringParam("repo", "<REPO ID>/ansible-installer.git", "Repo to seed from")
        stringParam("revision", "develop", "Revision branch")
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
                        url('git@172.17.0.1:$repo')
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