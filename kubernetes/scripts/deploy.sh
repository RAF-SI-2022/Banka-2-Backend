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
if [[ -z "${EXTRA_SERVICES}" ]]; then
  echo "EXTRA_SERVICES not declared"
  exit 1
fi

########################
# Deploy to production #
########################

commit_hash=$(git rev-parse HEAD)
image_ver="$commit_hash"
apt-get -y install grep
installed=$(helm list --no-headers 2>/dev/null | grep "${NAMESPACE}")

cd kubernetes

if [[ "$1" == "--update" || -n "${installed}" ]]; then
  echo "Upgrading Helm chart"
  cd helm/bank-2
  helm upgrade --install ${NAMESPACE} . \
    -f values-${ENV}.yaml \
    --set ${SIDE}.imageVer=$image_ver \
    -n ${NAMESPACE}
else
  # Provision all services
  echo "Provisioning services"
  cd provision
  extras=$(echo ${EXTRA_SERVICES} | xargs)
  for extra in $extras
  do
    cd $extra
    chmod +x install.sh
    bash ./install.sh
    cd ..
  done

  # Install helm chart
  echo "Installing Helm chart"
  cd ../helm/bank-2
  helm install ${NAMESPACE} . \
    -f values-${ENV}.yaml \
    --set ${SIDE}.imageVer=$image_ver \
    -n ${NAMESPACE}
fi