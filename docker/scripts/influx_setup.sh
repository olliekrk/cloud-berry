#!/bin/bash
# todo: find a way to execute the influx setup command automatically inside the influxdb-cb container
influx setup --username "$INFLUXDB_ADMIN_USER" --password "$INFLUXDB_ADMIN_PASSWORD" --org "$INFLUXDB_DEFAULT_ORG" --bucket "$INFLUXDB_DEFAULT_BUCKET" --token "$INFLUXDB_ADMIN_TOKEN" --force
