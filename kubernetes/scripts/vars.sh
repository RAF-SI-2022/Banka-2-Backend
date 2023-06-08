#################################
# Define all service names here #
#################################

services="flyway"
services+=" users"
services+=" main"
services+=" otc"
export SERVICES=${SERVICES}

#######################################################################
# Define all non-Helm services here; each must have its own folder in #
# kubernetes/provision, with an install.sh file                       #
#######################################################################

EXTRA_SERVICES="mongodb"
export EXTRA_SERVICES=${EXTRA_SERVICES}