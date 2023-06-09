################################################################################
#                                                                              #
#                                  upgrade.sh                                  #
#                                                                              #
# Installs or upgrades the Kubernetes configuration of the project. NOTE! This #
# shell script must be run from the project root folder, NOT the /kubernetes   #
# folder. The script will access the relevant folders on its own.              #
#                                                                              #
################################################################################

chmod +x kubernetes/scripts/vars.sh
chmod +x kubernetes/scripts/build.sh
chmod +x kubernetes/scripts/deploy.sh
. kubernetes/scripts/vars.sh
bash kubernetes/scripts/build.sh
bash kubernetes/scripts/deploy.sh $1