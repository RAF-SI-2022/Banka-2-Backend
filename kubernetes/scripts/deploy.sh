##########################################################################
# Environment/namespace vars, should be predefined before running script #
##########################################################################

if [[ -z "${ENV}" ]]; then
  echo "ENV not declared"
  exit 1
fi
if [[ -z "${SIDE}" ]]; then
  echo "SIDE not declared"
  exit 1
fi
if [[ -z "${NAMESPACE}" ]]; then
  echo "NAMESPACE not declared"
  exit 1
fi

#######################################################################
# Define all non-Helm services here; each must have its own folder in #
# kubernetes/provision, with an install.sh file                       #
#######################################################################

extras="mongodb"
extras=$(echo $extras | xargs)

########################
# Deploy to production #
########################

commit_hash=$(git rev-parse HEAD)
image_ver="$commit_hash"
apt-get -y install grep
installed=$(helm list --no-headers 2>/dev/null | grep "${NAMESPACE}")

cd kubernetes

if [[ -n "${installed}" ]]; then
  echo "Upgrading Helm chart"
  cd charts/bank-2
  helm upgrade ${NAMESPACE} . \
    --reuse-values \
    --set ${SIDE}.imageVer=$image_ver \
    -n ${NAMESPACE}
else
  # Provision all services
  echo "Provisioning services"
  cd provision
  for extra in $extras
  do
    cd $extra
    chmod +x install.sh
    bash ./install.sh
    cd ..
  done

  # Install helm chart
  echo "Installing Helm chart"
  cd ../charts/bank-2
  helm install ${NAMESPACE} . \
    --set ${SIDE}.imageVer=$imageVer \
    -f values-${ENV}.yaml \
    -n ${NAMESPACE}
fi