#!/usr/bin/env bash

if ! which docker &>/dev/null; then
    echo "Installing docker..."
    apt-get update
    apt-get install -y \
        ca-certificates \
        curl \
        gnupg \
        lsb-release

    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | gpg --dearmor -o /usr/share/keyrings/docker-archive-keyring.gpg

    echo "deb [arch=$(dpkg --print-architecture) signed-by=/usr/share/keyrings/docker-archive-keyring.gpg] https://download.docker.com/linux/ubuntu \
    $(lsb_release -cs) stable" | tee /etc/apt/sources.list.d/docker.list > /dev/null

    apt-get update
    apt-get install docker-ce docker-ce-cli containerd.io

    usermod -aG docker $(whoami)
    echo
fi

if ! which docker-compose &>/dev/null; then
    echo "Installing docker-compose..."
    curl -L "https://github.com/docker/compose/releases/download/v2.2.2/docker-compose-linux-x86_64" -o /usr/local/bin/docker-compose
    chmod +x /usr/local/bin/docker-compose
    echo
fi

if ! id -u jenkins &>/dev/null; then
    echo "Creating jenkins user..."
    useradd jenkins -s /bin/bash
    usermod -aG docker jenkins
    echo
fi

cd /home/jenkins

SECRETS=jenkins-secrets.properties
touch $SECRETS
docker run -it --rm -e VAULT=overlord-vault -v $SECRETS:/secrets mcr.microsoft.com/azure-cli:latest bash -c 'az login; \
    echo AZ_VAULT_URL=$(az keyvault secret show --vault-name overlord-vault --name AZ-VAULT-URL | jq -r ".value") > /secrets; \
    echo AZURE_CLIENT_ID=$(az keyvault secret show --vault-name overlord-vault --name AZURE-CLIENT-ID | jq -r ".value") >> /secrets; \
    echo AZURE_CLIENT_SECRET=$(az keyvault secret show --vault-name overlord-vault --name AZURE-CLIENT-SECRET | jq -r ".value") >> /secrets; \
    echo AZURE_SUBSCRIPTION_ID=$(az keyvault secret show --vault-name overlord-vault --name AZURE-SUBSCRIPTION-ID | jq -r ".value") >> /secrets; \
    echo AZURE_TENANT=$(az keyvault secret show --vault-name overlord-vault --name AZURE-TENANT | jq -r ".value") >> /secrets; \
    echo JENKINS_SSH_KEY=$(az keyvault secret show --vault-name overlord-vault --name JENKINS-SSH-KEY | jq -r ".value") >> /secrets'

wget https://raw.githubusercontent.com/mfugate1/server-management/main/truenas-docker/docker-compose.yaml
chown jenkins:jenkins $SECRETS docker-compose.yaml

sudo -u jenkins docker-compose up -d