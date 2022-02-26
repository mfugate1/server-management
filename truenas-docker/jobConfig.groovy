String script = '''\
cat << EOF > update-jenkins.sh
docker-compose pull
sleep 5
docker-compose up -d --force-recreate
EOF
cat << EOF > jenkins-secrets.properties
AZ_VAULT_URL=$AZ_VAULT_URL
AZURE_CLIENT_ID=$AZURE_CLIENT_ID
AZURE_CLIENT_SECRET=$AZURE_CLIENT_SECRET
AZURE_SUBSCRIPTION_ID=$AZURE_SUBSCRIPTION_ID
AZURE_TENANT=$AZURE_TENANT
JENKINS_SSH_KEY=$JENKINS_SSH_KEY
EOF
chmod +x update-jenkins.sh
scp -o StrictHostKeyChecking=no update-jenkins.sh truenas-docker/docker-compose.yaml jenkins-secrets.properties jenkins@${TRUENAS_DOCKER_IP}:~/
ssh -o StrictHostKeyChecking=no jenkins@${TRUENAS_DOCKER_IP} 'nohup ./update-jenkins.sh > /dev/null 2>&1 &'
'''

job ('Truenas-Docker-Apply-Updates') {
    label('docker')
    triggers {
        GenericTrigger {
            regexpFilterExpression('')
            regexpFilterText('')
            tokenCredentialId('JENKINS-TRUENAS-DOCKER-UPDATE-TOKEN')
        }
    }
    wrappers {
        sshAgent('jenkins-ssh')
        withAzureKeyvault {
            azureKeyVaultSecrets {
                azureKeyVaultSecret {
                    envVariable('AZ_VAULT_URL')
                    name('AZ-VAULT-URL')
                    secretType('Secret')
                }
                azureKeyVaultSecret {
                    envVariable('AZURE_CLIENT_ID')
                    name('AZURE-CLIENT-ID')
                    secretType('Secret')
                }
                azureKeyVaultSecret {
                    envVariable('AZURE_CLIENT_SECRET')
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
                    envVariable('JENKINS_SSH_KEY')
                    name('JENKINS-SSH-KEY')
                    secretType('Secret')
                }
                azureKeyVaultSecret {
                    envVariable('TRUENAS_DOCKER_IP')
                    name('TRUENAS-DOCKER-IP')
                    secretType('Secret')
                }
            }
        }
    }
    steps {
        shell(script)
    }
}