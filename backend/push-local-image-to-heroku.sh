#!/usr/bin/env bash
docker login --email=joostvdg@gmail.com --password="ea405d9e-76ff-4881-acbd-327c28efa3be" registry.heroku.com
docker tag keep-watching-be-img registry.heroku.com/keep-watching/web
docker push registry.heroku.com/keep-watching/web
