#!groovy

job('Say Hello World') {
    steps {
        shell('echo "Hello World!"')
    }
}