# DenktMit eG :: Testsupport :: Spring Test Initializers
The DenktMit eG spring testsupport repository contains useful bits and 
pieces we continuously reuse in our Spring based development

This project provides Spring test context initializer useful for managing
container provided external services.

## The initializers
### PostgresInitializer

### KeycloakInitializer


## Local development
Please note, that any paths used in this documentation are relative to this
projects root directory

### Requirements
The following runtimes and tools are required to build and run this software:

| Requirements   | Version                       | Installation                                                                    |
|----------------|-------------------------------|---------------------------------------------------------------------------------|
| Docker         | latest                        | See [Docker](https://docs.docker.com/engine/install)                            |
| Docker-Compose | `2.x` (`2.21.0`)              | See [Docker](https://docs.docker.com/engine/install)                            |
| Java JDK       | `17.x` (currently `17.0.8.1`) | See [Eclipse Temurin JDK](https://adoptium.net/de/temurin/releases/?version=19) |
| Maven          | `3.x` (currently `3.9.5`)     | See [Apache Maven](https://maven.apache.org/install.html)                       |

Notice, that instead of installing Maven, you can also make use of the
Maven wrapper provided within this repository. Wherever you see a global
maven action call like `mvn install`, you can use `./mvnw clean install`
instead.

### Build and run the application
Basically you will have to ensure, that you service dependencies are [README.md](README.md)
available. Start them with docker-compose and then build with maven
and start the application

#### Start service dependencies provided by docker-compose
This repository is accompanied by a **docker-compose.dev.yaml** file. It
provides single goto requirement for the build dependencies. Just make
sure to have it started before building the project

```bash
docker compose --env-file .env-dev -f docker-compose.dev.yaml up -d
```

Once you are done with it, you can just stop it, so it does not use up
your system resources

```bash
docker compose  --env-file .env-dev -f docker-compose.dev.yaml stop
```

and restart it, when you need it again

```bash
docker compose  --env-file .env-dev -f docker-compose.dev.yaml start
```

In case you need a fresh start, you can tell docker-compose to tear down
everything and start anew.

```bash
# Read the .env file into environment variables
ENV_FILE="$(pwd)/.env-dev"
source ${ENV_FILE}
# Tear down docker containers and remove volumes
docker compose  --env-file .env-dev -f docker-compose.dev.yaml down
# Remove the persistent docker volumes
docker volume rm ${COMPOSE_PROJECT_NAME}_db-it-data
```

#### Prepare the database with testdata
The pom.xml file of the persistence module defines Flyway executions to
fill the dev and it databases with testdata.

Fill integration test database with testdata (done automatically in maven build)
```bash
./mvnw flyway:migrate@fill-it -Dflyway.configFiles=../.flyway.it.conf -f ./persistence/pom.xml
```