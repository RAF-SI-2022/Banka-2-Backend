#!/usr/bin/env bash

set -euo pipefail

NAMESPACE="$(kubectl config view --minify | grep -Po 'namespace: \K.*')"

if [[ -n "${NAMESPACE}" ]]; then
  echo "Namespace not declared"
fi

# Add Helm repo
helm repo add bitnami https://charts.bitnami.com/bitnami || true

# Refresh Helm repos
helm repo update

# Install MongoDB chart
helm upgrade --install mongodb bitnami/mongodb-sharded \
  --set auth.enabled=true \
  --namespace "${NAMESPACE}"

# Provision DB
chmod +x provision.sh
bash provision.sh