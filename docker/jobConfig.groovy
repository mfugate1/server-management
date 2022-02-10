pipelineJob ('Update-Docker1-Versions') {
    definition {
        cpsScm {
            scm {
                git {
                    remote {
                        url('https://github.com/mfugate1/server-management.git')
                    }
                    branch('main')
                }
                lightweight()
                scriptPath('docker/Jenkinsfile')
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