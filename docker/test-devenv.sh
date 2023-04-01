#!/bin/sh

# Run docker and test working
sudo dockerd &
sudo docker run hello-world

# Test make init
cd /home/project
rm -rf lib/jdk-*
make init

# Fetch JDK dir
wd=$(ls lib | egrep "jdk-.*")
wd=$(pwd)/lib/$wd

# Set JAVA_HOME
JAVA_HOME=$wd
export JAVA_HOME="$wd"

# Test make test
sudo service docker start
make test