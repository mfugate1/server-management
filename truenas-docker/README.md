# TrueNAS Docker Environment

Required OS: Ubuntu

To setup the environment for the first time (as root):

```
curl --silent https://raw.githubusercontent.com/mfugate1/server-management/main/truenas-docker/setup.sh -o setup.sh
chmod +x setup.sh
sudo ./setup.sh
```

The setup script cannot be run directly by piping the output from curl into bash because getting the Azure secrets requires user input.

After initial setup, Jenkins will automatically update the environment when changes are detected.