#!groovy

// Shared Library Example
projects = [
    [name: "ServiceA"],
    [name: "ServiceB"],
    [name: "ServiceC"]
]

folder("Services")

for(project in projects) {
    pipelineJob("Services/${project.name}") {
    
        logRotator {
            numToKeep(50)
        }
    
        definition {
            cps {
                sandbox(true)
                script("""@Library('jenkins-as-code@master') _
printDockerVersion()
                """)
            }
        }
    }
}

// Use views if you like to have view tabs alongside with folders
listView("Services") {
    description("")
    filterBuildQueue()
    filterExecutors()
    jobs {
        regex(/Service.*/)
    }
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