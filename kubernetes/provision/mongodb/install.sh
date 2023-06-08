#!/usr/bin/env bash

set -euo pipefail

if [[ -n "${NAMESPACE}" ]]; then
  NAMESPACE="$(kubectl config view --minify | grep -Po 'namespace: \K.*')"
fi
if [[ -n "${NAMESPACE}" ]]; then
  echo "NAMESPACE not declared"
  exit 1
fi

# Add Helm repo
helm repo add bitnami https://charts.bitnami.com/bitnami || true

# Refresh Helm repos
helm repo update

# Install MongoDB chart
helm upgrade --install mongodb bitnami/mongodb-sharded \
  --set auth.enabled=true \
  --NAMESPACE "${NAMESPACE}"

# Provision DB
chmod +x provision.sh
bash provision.sh