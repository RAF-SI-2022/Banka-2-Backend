#######################################################################
# Define all non-Helm services here; each must have its own folder in #
# kubernetes/provision, with an install.sh file                       #
#######################################################################

extras="mongodb"
extras=$(echo $extras | xargs)

########################
# Deploy to production #
########################

apt-get -y install grep
installed=$(helm list --no-headers 2>/dev/null | grep "$namespace")

cd kubernetes

if [[ -n "${installed}" ]]; then
  echo "Upgrading Helm chart"
  cd charts/bank-2
  helm upgrade $namespace . \
    --reuse-values \
    --set $side.imageVer=$imageVer \
    -n $namespace
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
  helm install $namespace $namespace \
    --set $side.imageVer=$imageVer \
    -f values-$env.yaml \
    -n $namespace
fi