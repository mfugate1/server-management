String docker1Ip = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
    com.cloudbees.plugins.credentials.common.IdCredentials.class, 
    jenkins.model.Jenkins.instance, 
    null, 
    null
).find{it.id == "DOCKER1-IP"}.getSecret()

job ("Docker1-Apply-Updates") {
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