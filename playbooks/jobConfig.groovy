String cronSchedule = '''\
5 2 * * * %playbook=docker-prune.yml
5 3 * * * %playbook=apt-update.yml
'''

String playbookScript = '''\
node ('built-in') {
    List credentialsToLoad = [
        'FITNESS-DB-PASSWORD',
        'HASS-ALEXA-CLIENT-ID',
        'HASS-ALEXA-CLIENT-SECRET',
        'HASS-DB-PASSWORD',
        'LIVINGROOM-TABLET-FULLY-KIOSK-PASSWORD',
        'SLEEPIQ-USERNAME',
        'SLEEPIQ-PASSWORD',
        'XIAOMI-VACUUM-TOKEN',
        'XIAOMI-VACUUM-USERNAME',
        'XIAOMI-VACUUM-PASSWORD'
    ]

    credentialsToLoad = credentialsToLoad.collect{string(credentialsId: it, variable: it)}

    git branch: 'main', url: 'https://github.com/mfugate1/server-management'
    withCredentials(credentialsToLoad) {
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
    properties {
        pipelineTriggers {
            triggers {
                parameterizedCron {
                    parameterizedSpecification(cronSchedule)
                }
            }
        }
    }
}
