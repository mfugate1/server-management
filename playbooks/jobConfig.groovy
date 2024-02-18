import com.cloudbees.plugins.credentials.Credentials
import com.cloudbees.plugins.credentials.CredentialsProvider
import jenkins.model.Jenkins

String webhookToken = CredentialsProvider.lookupCredentials (
    Credentials.class,
    Jenkins.get(),
    null,
    null
).find{it.id == 'JENKINS_WEBHOOK_TOKEN'}.secret

String cronSchedule = '''\
5 1 * * * %playbook=paperless-backup.yml
5 2 * * * %playbook=docker-prune.yml
5 3 * * * %playbook=apt-update.yml
'''

String playbookScript = '''\
currentBuild.displayName = params.playbook
node ('built-in') {
    List credentialsToLoad = [
        'AWS_ACCESS_KEY',
        'AWS_SECRET_KEY',
        'FRIGATE_OUTER_GARAGE_CAMERA_PWD',
        'HASS-DB-PASSWORD',
        'HASS_MARIADB_PASSWORD',
        'HASS_MARIADB_ROOT_PASSWORD',
        'HASS_TOKEN',
        'HASS_URL',
        'LIVINGROOM-TABLET-FULLY-KIOSK-PASSWORD',
        'PAPERLESS_MARIADB_PASSWORD',
        'PAPERLESS_MARIADB_ROOT_PASSWORD',
        'PAPERLESS_SECRET_KEY',
        'PAPERLESS_URL',
        'PSN_USER',
        'PSN_NPSSO',
        'SPOTIFY_SP_DC',
        'SPOTIFY_SP_KEY'
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
