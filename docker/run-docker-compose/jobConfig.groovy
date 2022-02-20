def docker1Ip = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
    org.jenkinsci.plugins.azurekeyvaultplugin.credentials.string.AzureSecretStringCredentials.class, 
    Jenkins.instance, 
    null, 
    null).find{it.id == "DOCKER1-IP"}.getSecret()

job ("Run-Docker-Compose") {
    label("built-in")
    steps {
        remoteShell("jenkins@${docker1Ip}:22") {
            command([
                "cd /docker/server-management/docker",
                "git pull",
                "docker-compose up -d"
            ])
        }
    }
}