#!/usr/bin/bash
docker build -t cloudberry-py-docs:latest .
docker tag cloudberry-py-docs:latest olliekrk/cloudberry-py-docs:latest
docker push olliekrk/cloudberry-py-docs:latest
