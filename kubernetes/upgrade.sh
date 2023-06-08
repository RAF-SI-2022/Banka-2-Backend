################################################################################
#                                                                              #
#                                  upgrade.sh                                  #
#                                                                              #
# Installs or upgrades the Kubernetes configuration of the project. NOTE! This #
# shell script must be run from the project root folder, NOT the /kubernetes   #
# folder. The script will access the relevant folders on its own.              #
#                                                                              #
################################################################################

################################
# Build and push Docker images #
################################

docker login -u $HARBOR_USERNAME -p $HARBOR_PASSWORD harbor.k8s.elab.rs

commit_hash=$(git rev-parse HEAD)
tag_name=$(git tag --points-at HEAD)
imageVer="$commit_hash"

##########################################################################
# Environment/namespace vars, should be predefined before running script #
##########################################################################

# env="prod"
# side="backend"
# namespace="banka-2-$env"

#################################
# Define all service names here #
#################################

services="flyway"
services+=" users"
services+=" main"
services+=" otc"
services=$(echo $services | xargs)

##############################
# Define all image tags here #
##############################

image_tags="latest"
image_tags+=" ${commit_hash}"
image_tags+=" ${tag_name}"
image_tags=$(echo $image_tags | xargs)

for service in $services
do
  for tag in $image_tags
  do
    echo "Building image for service '$service'"
    docker build -t harbor.k8s.elab.rs/banka-2/$service:builder -f ./docker/$service.Dockerfile .
    docker tag harbor.k8s.elab.rs/banka-2/$service:builder harbor.k8s.elab.rs/banka-2/$service:$tag
    docker push harbor.k8s.elab.rs/banka-2/$service:$tag
  done
done

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