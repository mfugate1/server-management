import com.cloudbees.plugins.credentials.Credentials
import com.cloudbees.plugins.credentials.CredentialsProvider
import jenkins.model.Jenkins

String webhookToken = CredentialsProvider.lookupCredentials (
    Credentials.class,
    Jenkins.get(),
    null,
    null
).find{it.id == 'RUN-ANSIBLE-PLAYBOOK-TOKEN'}.secret

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
                GenericTrigger {
                    causeString('Webhook')
                    token(webhookToken)
                    regexpFilterText("")
                    regexpFilterExpression("")
                    overrideQuietPeriod(true)
                    genericRequestVariables {
                        genericRequestVariable {
                            key('playbook')
                            regexpFilter("")
                        }
                    }
                }
            }
        }
    }
}
