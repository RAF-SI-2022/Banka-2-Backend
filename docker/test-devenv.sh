#!/bin/sh

# Run docker and test working
sudo dockerd &
sudo docker run hello-world

# Test run init
cd /home/project
rm -rf lib/jdk-*
./run init

# Fetch JDK dir
wd=$(ls lib | egrep "jdk-.*")
wd=$(pwd)/lib/$wd

# Set JAVA_HOME
JAVA_HOME=$wd
export JAVA_HOME="$wd"

# Test run test
sudo service docker start
./run test