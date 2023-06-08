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