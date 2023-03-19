#!/bin/bash

# Exchange database (MongoDB) health check shell script

user="root"
pass="root"

# Check availability & dbs
mongosh "mongodb://localhost:27017" --username $user --password $pass --eval "use exchange-dev"
mongosh "mongodb://localhost:27017" --username $user --password $pass --eval "use exchange-test"
mongosh "mongodb://localhost:27017" --username $user --password $pass --eval "use exchange-prod"