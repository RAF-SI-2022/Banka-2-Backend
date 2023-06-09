#!/usr/bin/env bash

set -euo pipefail

if [[ -z "${NAMESPACE}" ]]; then
  NAMESPACE="$(kubectl config view --minify | grep -Po 'namespace: \K.*')"
fi
if [[ -z "${NAMESPACE}" ]]; then
  echo "NAMESPACE not declared"
  exit 1
fi

NEW_MONGODB_DB="${NEW_MONGODB_DB:-backend}"
NEW_MONGODB_USER="${NEW_MONGODB_USER:-admin}"
NEW_MONGODB_USER_PASSWORD="${NEW_MONGODB_USER_PASSWORD:-raf-si}"

export MONGODB_ROOT_PASSWORD=$(kubectl get secret --namespace "${NAMESPACE}" mongodb-mongodb-sharded -o jsonpath="{.data.mongodb-root-password}" | base64 -d)

echo "Creating monogo user ${NEW_MONGODB_USER} for database ${NEW_MONGODB_DB}..."

until kubectl --namespace="${NAMESPACE}" exec $(kubectl --namespace="${NAMESPACE}" get pods -o name | grep -m1 mongodb-sharded-mongos | cut -d'/' -f 2) -c mongos -- mongosh -u "root" -p "${MONGODB_ROOT_PASSWORD}" --authenticationDatabase admin --authenticationMechanism=SCRAM-SHA-256 --eval 'db.getSiblingDB("admin").createRole({ role: "SystemRole", privileges: [{resource:{db:"admin",collection:""},actions:["killop","inprog"]}, {resource:{db:"'"${NEW_MONGODB_DB}"'",collection:""},actions:["killop","inprog"]}], roles: [] });'
do
  echo "Error, trying again in 30s..."
  sleep 60
done

until kubectl --namespace="${NAMESPACE}" exec $(kubectl --namespace="${NAMESPACE}" get pods -o name | grep -m1 mongodb-sharded-mongos | cut -d'/' -f 2) -- mongosh -u "root" -p "${MONGODB_ROOT_PASSWORD}" --authenticationDatabase admin --authenticationMechanism=SCRAM-SHA-256 --eval 'db.getSiblingDB("admin").createUser({user:"'"${NEW_MONGODB_USER}"'",pwd:"'"${NEW_MONGODB_USER_PASSWORD}"'",roles:[{role:"readWrite",db:"'"${NEW_MONGODB_DB}"'"},{role:"SystemRole",db:"admin"},"clusterManager"]});'
do
  echo "Error, trying again in 30s..."
  sleep 60
done

echo "Enabling sharding for database ${NEW_MONGODB_DB}"

until kubectl --namespace="${NAMESPACE}" exec $(kubectl --namespace="${NAMESPACE}" get pods -o name | grep -m1 mongodb-sharded-mongos | cut -d'/' -f 2) -c mongos -- mongosh -u "root" -p "${MONGODB_ROOT_PASSWORD}" --authenticationDatabase admin --authenticationMechanism=SCRAM-SHA-256 --eval "sh.enableSharding('${NEW_MONGODB_DB}');"
do
  echo "Error, trying again in 30s..."
  sleep 60
done