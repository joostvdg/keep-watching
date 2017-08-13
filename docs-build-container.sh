#!/usr/bin/env bash
TAGNAME="keep-watching-docs-image"

echo "# Building new image with tag: $TAGNAME"
docker build --tag=$TAGNAME .
