freeStyleJob("Playbook--Apt-Update") {
    scm {
        github("mfugate1/server-management", "refs/heads/main")
    }
    triggers {
        cron {
            spec("H 6 * * *")
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

String playbookScript = '''\
node ('built-in') {
    git branch: 'main', url: 'https://github.com/mfugate1/server-management'
    withCredentials([azureServicePrincipal('az-vault')]) {
        ansiblePlaybook credentialsId: 'jenkins-ssh', disableHostKeyChecking: true, inventory: 'hosts', playbook: "playbooks/${params.playbook}"
    }
}
'''

File workspace = new File(__FILE__ - "/jobConfig.groovy")

pipelineJob("Run-Ansible-Playbook") {
    definition {
        cps {
            script(playbookScript)
            sandbox(true)
        }
    }
    parameters {
        choice {
            name("playbook")
            choices(workspace.listFiles().collect{it.getName()}.findAll{it.endsWith(".yml")})
        }
    }
}