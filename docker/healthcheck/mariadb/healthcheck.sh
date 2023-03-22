#!/bin/bash

# MariaDB health check shell script

user="root"
pass="root"

# Check availability
if (! (mysqladmin status --host=localhost --user=$user --password=$pass)); then
  exit 1
fi

# Check databases
mysql --host=localhost --user=root --password=root dev  -e "SHOW TABLES"
mysql --host=localhost --user=root --password=root test -e "SHOW TABLES"
mysql --host=localhost --user=root --password=root prod -e "SHOW TABLES"