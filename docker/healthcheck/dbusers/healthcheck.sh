#!/bin/bash

# Users database (MariaDB) health check shell script

user="root"
pass="root"

# Check availability
if (! (mysqladmin status --host=localhost --user=$user --password=$pass)); then
  exit 1
fi

# Check databases
mysql --host=localhost --user=root --password=root users-dev  -e "SHOW TABLES"
mysql --host=localhost --user=root --password=root users-test -e "SHOW TABLES"
mysql --host=localhost --user=root --password=root users-prod -e "SHOW TABLES"