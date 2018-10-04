# Event Sourced and CQRS Bank Account

This project is a basic example of an event sourced and CQRS application.

The persistence layer is simulated by actors and stored in memory.

## Launch the application

Execute the following commands in the root of the project:

```
$ sbt
> ~run
```

The application is accessible through this URL: `http://localhost:9000/`

## Available routes

### Create an account

`POST /accounts`

### List all acounts

`GET /accounts`

### Show an account

`GET /accounts/<UUID>`

### Make a deposit to an account

`POST /accounts/<UUID>/deposit?amount=<AMOUNT>`
