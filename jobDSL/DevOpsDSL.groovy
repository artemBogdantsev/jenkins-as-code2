#!groovy

def githubApi = new URL("https://api.github.com/users/marcelbirkner/repos")
def projects = new groovy.json.JsonSlurper().parse(githubApi.newReader())

projects.each {
    def jobName=it.name
    def githubName=it.full_name
    def gitUrl=it.ssh_url
    println "Creating Job ${jobName} for ${gitUrl}"

    job("DevOps-${jobName}") {
        logRotator(-1, 10)
        scm {
            github(githubName, "master")
        }
        triggers {
            githubPush()
        }
    }
}

listView("DevOps Jobs") {
    description("")
    filterBuildQueue()
    filterExecutors()
    jobs {
        regex(/DevOps-.*/)
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