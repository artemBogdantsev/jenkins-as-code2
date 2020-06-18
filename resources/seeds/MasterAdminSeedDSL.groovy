#!groovy

// This jobDSL script creates and Admin/Configure pipeline, which will act as
// our seeding and configuration pipeline for Jenkins. The Admin/Configure
// pipeline uses a shared central library hosted on GitLab.

folder('Admin') {
    description('Folder containing configuration and seed jobs')
}

pipelineJob("Admin/Configure") {
    parameters {
        // We can select a branch of the shared library which we want to use for seeding/configuration
        gitParam('revision') {
            type('BRANCH')
            sortMode('ASCENDING_SMART')
            defaultValue('origin/master')
        }
    }

    logRotator {
        numToKeep(50)
    }

    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url("git@172.17.0.1:devops/jenkins-as-code.git")
                        credentials("deploy-key-shared-library")
                    }

                    branch('$revision')
                }
            }

            // This is the config/seed pipeline within the shared repo
            scriptPath('resources/seeds/BootstrapSeedPipeline.groovy')
        }
    }
}