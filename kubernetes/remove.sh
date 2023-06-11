#!/usr/bin/env bash

set -euo pipefail

################################################################################
#                                                                              #
#                                   remove.sh                                  #
#                                                                              #
# Removes the installed Kubernetes configuration. Some residue may stay.       #
#                                                                              #
################################################################################


#############################
# Remove charts and residue #
#############################

echo "Deleting chart"

helm uninstall ${NAMESPACE} -n ${NAMESPACE} || true
kubectl delete pvc storage-relational-0 -n ${NAMESPACE} || true
kubectl delete pvc storage-relational-1 -n ${NAMESPACE} || true
kubectl delete pvc storage-relational-2 -n ${NAMESPACE} || true

###################
# Remove services #
###################

echo "Removing services"

chmod +x kubernetes/scripts/vars.sh
. kubernetes/scripts/vars.sh

cd kubernetes/provision
extras=$(echo ${EXTRA_SERVICES} | xargs)
for extra in $extras
do
  cd $extra
  chmod +x remove.sh
  bash ./remove.sh || true
  cd ..
done