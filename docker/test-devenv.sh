#!/bin/sh

# Run docker and test working
sudo dockerd &
sudo service docker start
sudo docker run hello-world

# Test make init
make init

# Fetch JDK dir
wd=$(ls lib | egrep "jdk-.*")
wd=$(pwd)/lib/$wd

# Set JAVA_HOME
JAVA_HOME=$wd
export JAVA_HOME="$wd"

# Test make test
make test