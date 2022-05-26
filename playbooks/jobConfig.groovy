freeStyleJob("Playbook--Apt-Update") {
    scm {
        github("mfugate1/server-management", "refs/heads/main")
    }
    triggers {
        cron {
            spec("H 1 * * *")
        }
    }
    steps {
        ansiblePlaybookBuilder {
            playbook("playbooks/apt-update.yml")
            inventory {
                inventoryPath {
                    path("hosts")
                }
            }
            credentialsId("jenkins-ssh")
            disableHostKeyChecking(true)
        }
    }
}
