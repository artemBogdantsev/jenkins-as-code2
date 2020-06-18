## Description

This repository contains a Jenkins-as-Code approach. 
Everything is tested and running with Jenkins `2.164.3` on minikube `v1.0.0`. 
The setup is based on docker, helm and git so it can be easily applied in different infrastructures.
Plugins and minimum setup are pre-baked inside a docker image. 
A configuration and seeding pipeline provisions Jenkins with configuration code from a central git repository. 
Configuration includes: ~~agents on demand (with terraform)~~, slack, GitLab, GitLab-oAuth, security settings, theming, ... 

## Running locally
Before we start please clone the repo or create a fork and keep all needed secrets in there
```
git clone git@<REPO URL>:devops/jenkins-as-code.git
cd jenkins-as-code
```

The following files with your secrets have to be created to run this prototype:

```
resources/helm/
|-- secret-files
    |-- default-setup-password          # pre-baked setup jenkins admin password
    |-- default-setup-user              # pre-baked setup jenkins admin name
    |-- deploy-key-shared-library       # private ssh deploy key
    |-- deploy-key-shared-library.pub   # public ssh deploy key
    |-- gitlab-token                    # GitLab Jenkins user deploy token
    |-- slack-token                     # Slack tocken  
    |-- ssh-agent-access-key            # private ssh key for agent access
    `-- ssh-agent-access-key.pub        # public ssh key for agent access
```

To get SSH keys just generate them like here:
```shell
ssh-keygen -o -f resources/helm/secret-files/deploy-key-shared-library
ssh-keygen -o -f resources/helm/secret-files/ssh-agent-access-key
```

After you have placed your secret files you can run:

```
mkdir bin # folder where you will place minicube and helm 
make get-tools-(linux|mac)
make minikube-start
make build-image
make helm-deploy
```

To be able to access a Jenkins instance, first grab the service IP 
```
kubectl get services
NAME         TYPE           CLUSTER-IP       EXTERNAL-IP   PORT(S)          AGE
kubernetes   ClusterIP      10.96.0.1        <none>        443/TCP          7h
jenkins-01   NodePort       10.100.125.120   <none>        8084:30362/TCP   13s

bin/minikube service jenkins-01 --url
http://192.168.99.101:30362
```

This should open Jenkins on [http://192.168.99.101/](http://192.168.99.101/).

### Links

- Job DSL API https://jenkinsci.github.io/job-dsl-plugin/
- Jenkins Plugins https://plugins.jenkins.io/

## Detailed Explanations

The following blog entries describe in more detail how this works:

- [Jenkins-as-Code Part I | Initial Setup](https://fishi.devtail.io/weblog/2019/01/06/jenkins-as-code-part-1/)
- [Jenkins-as-Code Part II | Configuration](https://fishi.devtail.io/weblog/2019/01/12/jenkins-as-code-part-2/)
- [Jenkins-as-Code Part III | JobDSL](https://fishi.devtail.io/weblog/2019/02/09/jenkins-as-code-part-3/)
