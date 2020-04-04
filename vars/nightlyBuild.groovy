def call(Map config) {
    node('master') {
        triggers {
            //cron('H 21 * * 1-5')
            //cron('* * * * *')
            parameterizedCron('''
            H 21 * * 1-5 %APPSERVERNAME=vg-tst-app;APPSERVERIP=172.19.90.185;CSSERVERNAME=vg-tst-cs;CSSERVERIP=172.19.90.186;BRANCH=DEVELOP;VERSION=3.7
            H 00 * * 1-5 %APPSERVERNAME=vg-tst-apprelease;APPSERVERIP=172.19.90.205;CSSERVERNAME=vg-tst-csrelease;CSSERVERIP=172.19.90.206;BRANCH=RELEASE;VERSION=3.6
            H 03 * * 1-5 %APPSERVERNAME=vg-tst-appmaster;APPSERVERIP=172.19.90.207;CSSERVERNAME=vg-tst-csmaster;CSSERVERIP=172.19.90.208;BRANCH=MASTER;VERSION=3.6
           ''')
        }
        parameters {
            string(name: 'APPSERVERNAME', defaultValue: "vg-tst-app", description: "")
            string(name: 'APPSERVERIP', defaultValue: "172.19.90.185", description: "")
            string(name: 'CSSERVERNAME', defaultValue: "vg-tst-cs", description: "")
            string(name: 'CSSERVERIP', defaultValue: "172.19.90.186", description: "")
            string(name: 'BRANCH', defaultValue: "RELEASE", description: "")
            string(name: 'VERSION', defaultValue: "3.6", description: "")
        }
        stages {
            stage('Build Infrastructure') {
                steps {
                    build(job: 'build_infrastructure',
                            parameters:
                                    [string(name: 'APPSERVERNAME', value: "$params.APPSERVERNAME"),
                                     string(name: 'APPSERVERIP', value: "$params.APPSERVERIP"),
                                     string(name: 'CSSERVERNAME', value: "$params.CSSERVERNAME"),
                                     string(name: 'CSSERVERIP', value: "$params.CSSERVERIP")])
                }
            }
            stage('Build Environment') {
                steps {
                    build(job: 'build_environment', parameters:
                            [string(name: 'APPSERVER', value: "$params.APPSERVERNAME:$params.APPSERVERIP"),
                             string(name: 'CSSERVER', value: "$params.CSSERVERNAME:$params.CSSERVERIP"),
                             string(name: 'BRANCH', value: "$params.BRANCH"),
                             string(name: 'VERSION', value: "$params.VERSION")])
                }
            }
            stage('SetUp Dasense Environment') {
                steps {
                    build(job: 'setting_up_dasence_environment', parameters:
                            [string(name: 'APPSERVER_IP', value: "$params.APPSERVERIP"),
                             string(name: 'CSSERVER_IP', value: "$params.CSSERVERIP")])
                    sleep 20
                }
            }
            stage('Upload Application in DaSense') {
                steps {
                    //echo "Will be implmented once Built is stable"
                    build(job: 'upload_application_daSense', parameters: [string(name: 'APPSERVER_IP', value: "$params.APPSERVERIP")], propagate: false)
                }
            }
            stage('Test DaSence') {
                steps {
                    // build 'dasense-rest-integration-nighty'
                    build(job: 'dasense-rest-integration-nighty', parameters:
                            [string(name: 'BRANCH', value: "$params.BRANCH"),
                             string(name: 'APPSERVER_IP', value: "$params.APPSERVERIP"),
                             string(name: 'CODESPACESERVER_NAME', value: "$params.CSSERVERNAME"),
                             string(name: 'VERSION', value: "$params.VERSION")])
                }
            }
        }
    }
}