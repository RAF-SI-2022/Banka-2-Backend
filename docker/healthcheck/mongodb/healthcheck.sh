#!/bin/bash

# MongoDB health check shell script

user="root"
pass="root"

# Check availability & dbs
mongosh "mongodb://localhost:27017" --username $user --password $pass --eval "use dev"
mongosh "mongodb://localhost:27017" --username $user --password $pass --eval "use test"
mongosh "mongodb://localhost:27017" --username $user --password $pass --eval "use prod"