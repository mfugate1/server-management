job ("Run-Docker-Compose") {
    label("built-in")
    wrappers {
        sshAgent("jenkins-ssh")
        withAzureKeyvault {
            azureKeyVaultSecrets {
                azureKeyVaultSecret {
                    envVariable("DOCKER1_IP")
                    name("DOCKER1-IP")
                    secretType("Secret")
                }
            }
        }
    }
    steps {
        remoteShell("jenkins@${DOCKER1_IP}:22") {
            command([
                "cd /docker/server-management/docker",
                "git pull",
                "docker-compose up -d"
            ])
        }
    }
}