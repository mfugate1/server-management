pipelineJob ('Docker1-Update-Container-Versions') {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        github('mfugate1/server-management', 'ssh', 'github.com')
                        credentials(JENKINS_SSH_KEY)
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
        disableConcurrentBuilds {
            abortPrevious(false)
        }
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
                    tokenCredentialId('JENKINS-DOCKER1-UPDATE-CONTAINER-VERSIONS-TOKEN')
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