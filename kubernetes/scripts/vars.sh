#################################
# Define all service names here #
#################################

SERVICES="flyway"
SERVICES+=" users"
SERVICES+=" main"
SERVICES+=" otc"
export SERVICES="${SERVICES}"

#######################################################################
# Define all non-Helm services here; each must have its own folder in #
# kubernetes/provision, with an install.sh file                       #
#######################################################################

EXTRA_SERVICES="mongodb"
export EXTRA_SERVICES="${EXTRA_SERVICES}"