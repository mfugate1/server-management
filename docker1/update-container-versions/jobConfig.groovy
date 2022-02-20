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
                    genericVariables {
                        genericVariable {
                            expressionType: 'JSONPath',
                            key: 'forceUpdate',
                            value: '$.forceUpdate'
                        }
                    }
                }
            }
        }
    }
}