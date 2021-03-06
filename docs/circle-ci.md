# Circle CI

> CircleCI was founded in 2011 with the mission of giving every developer state-of-the-art automated testing and continuous integration tools.

Circle CI is a cloud Continuous Integration tool which allows you to build your Open Source GitHub applications for free.

Keep-Watching uses Circle CI to validate the code and the docker image before sending it to DockerHub and Heroku.

## Circle CI 1.0

Currently, the build is still using the Circle CI 1.0 API.

### Environment variables

To make sure we do not have to put passwords, tokens or other compromising information in the build file (see below), we use environment variables in the Circle CI configuration. 

These speak for themselves:

* DOCKER_EMAIL	
* DOCKER_PASS	
* DOCKER_USER	
* GITHUB_TOKEN	
* HEROKU_EMAIL	
* HEROKU_TOKEN	
* HEROKU_USER

### Build file

```yaml
machine:
  pre:
    - curl -sSL https://s3.amazonaws.com/circle-downloads/install-circleci-docker.sh | bash -s -- 1.10.0
  services:
    - docker
  java:
    version: oraclejdk8
  python:
      version: 2.7.9

general:
  artifacts:
    - "backend/target/keep-watching-be.jar"
  branches:
    ignore:
      - gh-pages # list of branches to ignore

dependencies:
  pre:
    - curl -L https://github.com/docker/compose/releases/download/1.8.0-rc2/docker-compose-`uname -s`-`uname -m` > ../bin/docker-compose && chmod +x ../bin/docker-compose
    - docker-compose pull
    - pip install -r mkdocs-requirements.txt
  override:
    - mvn dependency:resolve -P db
    - docker info

test:
  override:
    - sh ./test.sh

deployment:
  release:
    branch: master
    commands:
      - docker build --rm=false -t caladreas/keep-watching-be backend
      - docker login -e $DOCKER_EMAIL -u $DOCKER_USER -p $DOCKER_PASS
      - docker push caladreas/keep-watching-be
      - git remote add github https://${GITHUB_TOKEN}@github.com/joostvdg/keep-watching.git
      - mkdocs gh-deploy -r github --force
      - docker login --email=${HEROKU_EMAIL} --username=${HEROKU_USER} --password=${HEROKU_TOKEN} registry.heroku.com
      - docker tag caladreas/keep-watching-be registry.heroku.com/keep-watching/web
      - docker push registry.heroku.com/keep-watching/web
```
