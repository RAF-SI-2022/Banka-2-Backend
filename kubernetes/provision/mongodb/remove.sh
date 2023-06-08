#!/usr/bin/env bash

set -euo pipefail

helm uninstall mongodb
kubectl pvc delete datadir-mongodb-mongodb-sharded-configsvr-0
kubectl pvc delete datadir-mongodb-mongodb-sharded-shard0-data-0
kubectl pvc delete datadir-mongodb-mongodb-sharded-shard1-data-0