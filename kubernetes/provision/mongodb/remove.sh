#!/usr/bin/env bash

set -euo pipefail

if [[ -z "${NAMESPACE}" ]]; then
  echo "NAMESPACE not declared"
  exit 1
fi

helm uninstall mongodb -n ${NAMESPACE} || true
kubectl delete pvc datadir-mongodb-mongodb-sharded-configsvr-0 -n ${NAMESPACE} || true
kubectl delete pvc datadir-mongodb-mongodb-sharded-shard0-data-0 -n ${NAMESPACE} || true
kubectl delete pvc datadir-mongodb-mongodb-sharded-shard1-data-0 -n ${NAMESPACE} || true