String webhookToken = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
    com.cloudbees.plugins.credentials.common.IdCredentials.class, 
    jenkins.model.Jenkins.instance, 
    null, 
    null
).find{it.id == "JENKINS-DOCKER1-UPDATE-CONTAINER-VERSIONS-TOKEN"}.getSecret()

pipelineJob ('Docker1-Update-Container-Versions') {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        github('mfugate1/server-management', 'ssh', 'github.com')
                        credentials('github-ssh')
                    }
                    extensions {
                        localBranch()
                    }
                    branch('main')
                }
                lightweight()
                scriptPath('docker1/update-container-versions/Jenkinsfile')
            }
        }
    }
    properties {
        parameters {
            parameterDefinitions {
                booleanParam {
                    name('forceUpdate')
                }
            }
        }
        pipelineTriggers {
            triggers {
                cron {
                    spec('H 6 * * *')
                }
                GenericTrigger {
                    causeString('Webhook')
                    token(webhookToken)
                    regexpFilterText('')
                    regexpFilterExpression('')
                    genericVariables {
                        genericVariable {
                            expressionType('JSONPath')
                            key('forceUpdate')
                            value('$.forceUpdate')
                        }
                    }
                }
            }
        }
    }
}