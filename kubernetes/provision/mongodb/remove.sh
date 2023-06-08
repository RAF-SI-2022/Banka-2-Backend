#!/usr/bin/env bash

set -euo pipefail

helm uninstall mongodb || true
kubectl delete pvc datadir-mongodb-mongodb-sharded-configsvr-0 || true
kubectl delete pvc datadir-mongodb-mongodb-sharded-shard0-data-0 || true
kubectl delete pvc datadir-mongodb-mongodb-sharded-shard1-data-0 || true