# JSON Validator

This project is a JSON Validator written using the Play Framework with a PostgresSQL Database. 
The aim of this project is to validate JSON files against uploaded schemas, if the json is valid it should be returned to the user with any `null` columns removed. 

## Initial Setup

#### Dependencies
This project was built using on a Windows 10 machine using the following dependencies:
* Java - 1.8.0_302
* sbt - 1.5.5
* PostgresSQL - 13.4

#### Setting up Java and sbt
1. Download the relevant JDK for your OS from [here](https://www.oracle.com/uk/java/technologies/javase/javase-jdk8-downloads.html)
2. Set your `JAVA_HOME` environment variable to point to the downloaded JDK and add it to your `PATH` environment variable with `/bin` appended to the end
3. Download the relevant SBT for your OS from [here](https://www.scala-sbt.org/download.html)
4. Set your `SBT_HOME` environment variable to point to the downloaded SBT version and add it to your `PATH` environment variable with `/bin` appended to the end
5. Test you have set `JAVA_HOME` and `SBT_HOME` correctly by opening a new terminal and entering `java -verions` and `sbt --version`, the output should indicate you have java `1.8.0_302` and sbt `1.5.5`

#### Setting up PostgresSQL
1. Download the relevant PostgresSQl for your OS from [here](https://www.postgresql.org/download/)
2. Create an environment variable `POSTGRES_HOME` and point it to your downloaded PostgresSQL folder, add it to your `PATH` environment variable with `/bin` appended to the end
3. Test you have set `POSTGRES_HOME` correcly by opening a new terminal and entering `postgres -V`, the output should indicate you have `13.4` set up 
4. Create an environment variable `PGDATA` to point to an empty folder on your machine
5. Run the command `initdb`, this should initialise your PostgresSQL database and populate the folder you pointed `PGDATA` at

## Application Overview
### Running the Application
To run the application you will need to start the frountend, see `Starting the frountend (Play Framework)`, as well as stating the backend, see `Starting the backend (PostgresSQL database)`.
#### Starting the frountend (Play Framework)
To start the frounend navigate to the root of the `json-formatter-snowplow` project and run the command `sbt run`. 
You should see an output similar to the following:
```
λ sbt run
[info] welcome to sbt 1.5.2 (Temurin Java 1.8.0_302)
...
--- (Running the application, auto-reloading is enabled) ---

[info] p.c.s.AkkaHttpServer - Listening for HTTP on /0:0:0:0:0:0:0:0:9000

(Server started, use Enter to stop and go back to the console...)
```
this indicates the frountend of the application has starting and will recieve requests on `localhost:9000`.
#### Starting the backend (PostgresSQL database)
To start the backend open a new terminal and run the command `postgres`. 
You should see an output similar to the following:
```
λ postgres
2021-09-14 07:49:42.910 BST [2760] LOG:  starting PostgreSQL 13.4, compiled by Visual C++ build 1914, 64-bit
2021-09-14 07:49:42.923 BST [2760] LOG:  listening on IPv6 address "::1", port 5432
2021-09-14 07:49:42.924 BST [2760] LOG:  listening on IPv4 address "127.0.0.1", port 5432
2021-09-14 07:49:43.038 BST [1840] LOG:  database system was interrupted; last known up at 2021-09-13 09:10:08 BST
2021-09-14 07:49:43.828 BST [1840] LOG:  database system was not properly shut down; automatic recovery in progress
2021-09-14 07:49:43.835 BST [1840] LOG:  redo starts at 0/167EAB8
2021-09-14 07:49:43.851 BST [1840] LOG:  invalid record length at 0/16962E8: wanted 24, got 0
2021-09-14 07:49:43.851 BST [1840] LOG:  redo done at 0/16962B0
2021-09-14 07:49:44.004 BST [2760] LOG:  database system is ready to accept connections

```
this indicates the the PostgresSQL database has starting and is ready to recieve requests from the frountent.

### Endpoints Overview
This application has three endpoints:
```
POST    /schema/SCHEMAID        - Upload a JSON Schema with unique `SCHEMAID`
GET     /schema/SCHEMAID        - Download a JSON Schema with unique `SCHEMAID`

POST    /validate/SCHEMAID      - Validate a JSON document against the JSON Schema identified by `SCHEMAID`
```
#### POST /schema/SCHEMAID
The POST Schema endpoint uploads a schema which can then be used to validate against. For example to upload a JSON schema the curl command would look similar to:
`curl http://localhost:9000/schema/config-schema -X POST -d @config-schema.json` were `config-schema.json` is a local file containing the schema and `config-schema` is the id I want to assign to the schema.
If the schema is uploaded successfull the following response will be returned: 
```
{
    "action":"uploadSchema",
    "id":"test-schema",
    "status":"success"
}
```


The `SCHEMAID` should be unique, if a schema already exists with that `SCHEMAID` the following error will be returned:
```
{
    "action":"uploadSchema",
    "id":"config-schema",
    "status":"error",
    "message":"Json schema upload Failed. Does the id you are using for the schema already exist?"
}
```

#### GET /schema/SCHEMAID 
The GET Schema endpoint returns a schema which can then be used to validate against. For example to get a JSON schema the curl command would look similar to:
`curl http://localhost:9000/schema/config-schema -X GET` were `config-schema` is the id of the schema I want to retrieve.
If a schema exists with the `SCHEMAID` I parse in I should get a response similar to:
```
{
    "action":"getSchema",
    "id":"config-schema",
    "status":"success",
    "message":"{\"schema_Id\":\"config-schema\",\"schema\":\"..."}"}
```
If a schema with `SCHEMAID` does not exist I should get a response similar to:
```
{
    "action":"getSchema",
    "id":"not-a-schema",
    "status":"error",
    "message":"No Valid JSON schema found with schemaId: not-a-schema."
}
```
#### POST /validate/SCHEMAID
