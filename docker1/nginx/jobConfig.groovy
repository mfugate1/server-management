String nginxDir = __FILE__ - "${hudson.model.Executor.currentExecutor().getRemote()}/"

String script = """\
echo "docker1 ansible_host=\${DOCKER1_IP} ansible_user=jenkins" > hosts
ansible-playbook -i hosts ${nginxDir}/playbook.yml
"""

job ("Docker1-Nginx-Playbook") {
    label("ansible")
    properties {
        disableConcurrentBuilds {
            abortPrevious(false)
        }
    }
    steps {
        shell(script)
    }
    triggers {
        GenericTrigger {
            regexpFilterExpression('')
            regexpFilterText('')
            tokenCredentialId('JENKINS-DOCKER1-NGINX-PLAYBOOK-TOKEN')
        }
    }
    wrappers {
        sshAgent('jenkins-ssh')
        withAzureKeyvault {
            azureKeyVaultSecrets {
                azureKeyVaultSecret {
                    envVariable('AZURE_CLIENT_ID')
                    name('AZURE-CLIENT-ID')
                    secretType('Secret')
                }
                azureKeyVaultSecret {
                    envVariable('AZURE_SECRET')
                    name('AZURE-CLIENT-SECRET')
                    secretType('Secret')
                }
                azureKeyVaultSecret {
                    envVariable('AZURE_SUBSCRIPTION_ID')
                    name('AZURE-SUBSCRIPTION-ID')
                    secretType('Secret')
                }
                azureKeyVaultSecret {
                    envVariable('AZURE_TENANT')
                    name('AZURE-TENANT')
                    secretType('Secret')
                }
                azureKeyVaultSecret {
                    envVariable('DOCKER1_IP')
                    name('DOCKER1-IP')
                    secretType('Secret')
                }
            }
        }
    }
}