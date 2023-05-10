#!/bin/sh

# Run docker and test working
sudo dockerd &
sudo docker run hello-world

# Test run init
cd /home/project
run init

# Test make test
sudo service docker start
run test