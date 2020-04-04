#!groovy

job('Seed World with Hello') {
    scm {
        git {
            remote {
                url ('git@abra.dasense.de:devops/jenkins-as-code.git')
                credentials("deploy-key-shared-library")
            }
        }
    }
    steps {
        dsl {
            external('jobDSL/hello_world.groovy') // use mask to seed several jobs
            // default behavior
            // removeAction('IGNORE')
            removeAction('DELETE')
        }
    }
}