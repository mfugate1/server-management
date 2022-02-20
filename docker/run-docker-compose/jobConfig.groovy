job ("Run-Docker-Compose") {
    label("built-in")
    wrappers {
        sshAgent("jenkins-ssh")
    }
    steps {
        remoteShell("jenkins@192.168.1.86:22") {
            command([
                "cd /docker/server-management/docker",
                "git pull",
                "docker-compose up -d"
            ])
        }
    }
}