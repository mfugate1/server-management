import hudson.FilePath
import hudson.model.Executor

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
    ansiblePlaybook credentialsId: 'jenkins-ssh', disableHostKeyChecking: true, inventory: 'hosts', playbook: playbook
'''.stripIndent()

FilePath workspace = Executor.currentExecutor().getCurrentWorkspace().child("server-management/playbooks/")

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
            choices(workspace.list().findAll{it.endsWith(".yml")})
        }
    }
}