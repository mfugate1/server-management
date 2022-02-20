pipelineJob ('Update-Docker1-Versions') {
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
        pipelineTriggers {
            triggers {
                cron {
                    spec('H 6 * * *')
                }
            }
        }
    }
}