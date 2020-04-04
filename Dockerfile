FROM jenkins/jenkins:2.164.3

ENV ARCH=linux_amd64

# Disable install wizard
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Dorg.jenkinsci.main.modules.sshd.SSHD.hostName=127.0.0.1"

# JCasC Plugin pointer to config/secret values
ENV SECRETS="/var/jenkins_home/"

USER root

# Install deps
RUN apt-get update -y \
  && apt-get install -y build-essential rsync

USER jenkins

# Add minimum jenkins setup
ADD resources/init.groovy.d /usr/share/jenkins/ref/init.groovy.d
ADD resources/seeds /usr/share/jenkins/ref/seeds

# Install plugins
COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /usr/share/jenkins/ref/plugins.txt