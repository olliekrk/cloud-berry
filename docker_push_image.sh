#!/bin/bash
docker login -u "$DOCKER_USERNAME" -p "$DOCKER_SECRET"
docker push olliekrk/cloudberry:latest
