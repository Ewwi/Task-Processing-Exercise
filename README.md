# Asynchronous task processing

## Table of content

- [Task description](#task-description)
- [Building and running application](#building-and-running-application)
- [Application API](#application-api)
- [Interacting with application](#interacting-with-application)
- [Other important information](#other-important-information)

## Task description

This project has been created in order to train Task Processing.
Task's description and requirements are in [this file](<TASK.md>).

## Building and running application

Project is build using Docker Multi-Stage Build,
so you don't need Maven / Java etc. You only need Docker.

Build and run application with command (might take
up to 3 minutes on first time, so be patient):

Linux:
```bash
docker run \
    -p 8080:8080 \
    -p 9090:9090 \
    -it $(docker build -q .)
```
Windows:
```bash
docker build -q .
```
```bash
docker run -p 8080:8080 -p 9090:9090 -it <sha>
```

These variables are externalized:

```
TASK-PROCESSING-TIME-IN-MS                # default is 30000
TASK-PROCESSING-UPDATE-INTERVAL-IN-MS     # default is 100
TASK-PROCESSING-THREAD-POOL-SIZE          # default is 5
TASK-PROCESSING-QUEUE-CAPACITY            # default is 100
```

You can pass them when starting container:

```bash
docker run \
    -p 8080:8080 \
    -p 9090:9090 \
    -e TASK-PROCESSING-TIME-IN-MS=5000 \
    -it $(docker build -q .)
```

Maven wrapper is optionally attached if you want to use it.

## Application API

You can view API (when app is running) here - http://localhost:9090/actuator/swagger-ui/index.html

API is almost exactly the same as it is in task description.

A few differences:

- task states are upper-cased Strings
- task will initially be in `NEW` state. Once processing will
  start, it will transition into `RUNNING` state
- `FINISHED` tasks have no `progress` field
- `progress` field is a numerical value between 0 and 100
- if task's processing fails or if it's interrupted, it's state will be TERMINATED

## Interacting with application

You can just use any HTTP client. Here are some examples with
[HTTPie](https://github.com/httpie/httpie):

```bash
http POST :8080/tasks base=2 exponent=2
http GET :8080/tasks
http GET :8080/tasks/1
```

## Other important information

Application state/data is ephemeral. Tasks won't survive
instance's restart.
