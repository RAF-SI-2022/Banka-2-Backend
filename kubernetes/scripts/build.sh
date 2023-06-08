################################
# Build and push Docker images #
################################

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