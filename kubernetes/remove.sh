#!/usr/bin/env bash

set -euo pipefail

################################################################################
#                                                                              #
#                                   remove.sh                                  #
#                                                                              #
# Removes the installed Kubernetes configuration. Some residue may stay.       #
#                                                                              #
################################################################################


#################
# Remove charts #
#################

echo "Deleting chart"

helm uninstall ${NAMESPACE} || true

###################
# Remove services #
###################

echo "Removing services"

extras="mongodb"
extras=$(echo $extras | xargs)

cd kubernetes/provision
for extra in $extras
do
  cd $extra
  chmod +x remove.sh
  bash ./remove.sh
  cd ..
done