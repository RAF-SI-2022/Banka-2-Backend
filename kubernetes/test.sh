################################################################################
#                                                                              #
#                                    test.sh                                   #
#                                                                              #
# Tests the setup of the Kubernetes stack. DANGER: this script will WIPE all   #
# databases. PROCEED WITH CAUTION! Only run in DEV environment!                #
#                                                                              #
################################################################################

# Load services
chmod +x kubernetes/scripts/vars.sh
. kubernetes/scripts/vars.sh

# Number of attempts
max_attempts=10

# Interval between attempts (in seconds)
interval=30

# Current attempt
attempt=1

all_ready=true
while [ $attempt -le $max_attempts ]
do
  echo "Checking if services pods are running #$attempt"
  all_ready=true

  services=$(echo ${SERVICES} | xargs)
  for service in $services
  do

    # Regex of the pod
    regex="$service-.*"

    # Fetch the status of the pod
    pod_status=$(kubectl get pods -n ${NAMESPACE} | grep -E "$regex" | awk '{print $3}')

    # Check if the pod is running
    if [ "$pod_status" != "Running" ]
    then
      all_ready=false
      break
    fi
  done

  if [ "$all_ready" = true ]
  then
    break
  fi

  # Wait for the specified interval
  sleep $interval

  # Increase the attempt counter
  ((attempt++))
done

if [ "$all_ready" != true ]
then
  echo "Service pods did not reach the 'Running' status after $max_attempts attempts."
  exit 1
fi

cmd=$(cat <<-CMD
  apt-get update -y;
  apt-get install -y python3 python3-pip;
  cd src/main/resources;

  echo \"import os\" >> env_extr.py;
  echo \"import re\" >> env_extr.py;

  echo \"with open\('application.properties'\) as file:\" >> env_extr.py;
  echo \"    properties = [line.strip\(\) for line in file if '=' in line]\" >> env_extr.py;

  echo \"keys = [prop.split\('='\)[0] for prop in properties]\" >> env_extr.py;

  echo \"for key in keys:\" >> env_extr.py;
  echo \"    for name, value in os.environ.items\(\):\" >> env_extr.py;
  echo \"        if re.search\(key, value\) or re.search\(key, name\):\" >> env_extr.py;
  echo \"            print\(f\'{name}={value}\'\)\" >> env_extr.py;
  python3 env_extr.py > application-temp.properties;
  cd ../../..;
  mvn test -Dspring.profiles.active=temp -DargLine=-Dspring.profiles.active=temp
CMD
)

# Run test on each pod
for service in $services
do
  echo "Testing pod $service"

  # Regex of the pod
  regex="$service-.*"

  # Fetch the status of the pod
  name=$(kubectl get pods -n ${NAMESPACE} | grep -E "$regex" | awk '{print $1}')

  if kubectl exec $name -- /bin/bash -c "$cmd"
  then
    echo "Pod $service passed tests"
  else
    echo "Pod $service failed tests"
    exit 1
  fi
done