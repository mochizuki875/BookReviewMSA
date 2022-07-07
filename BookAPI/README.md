# Book API
Book API is one of the compornent of BookReview MSA.

## Run

```
# ./gradlew bootRun
```

or

```
# ./gradlew build
# java -jar build/libs/BookAPI-0.0.1-SNAPSHOT.jar
```

You can access H2 Console `http://127.0.0.1:8080/h2-console/` with below params.

|Name         |Value                   |
|:---         |:---                    |
|Driver Class |`org.h2.Driver`         |
|JDBC URL     |`jdbc:h2:mem:bookreview`|
|User Name    |`sa`                    |
|Password     |                        |

### Docker

```
# docker build -t book-api .
# docker run -d -p 8080:8080 --name book-api book-api
```

### Kubernetes

```
# kubectl apply -f Kubernetes/manifest.yaml
```

## Configuration

|Name                        |Description                                                                                   |Value|
|:---                        |:---                                                                                          |:---|
|`buildEnv`                  |If you set `prod`, using database(`PostgreSQL 14.x`) as application datastore.(Default: `dev`)|`dev`/`prod`|
|`SERVER_PORT`               |Set server port.(Default: `8080`)                                                             |`<Port>`|
|`REVIEW_API_URL`            |Set `Review API` URL.(ex `http://review-api:8080/api/review`)                                 |`http://<Review API Host Name>:<Review API Port>/api/review`|
|`SPRING_DATASOURCE_URL`     |Set database URL.(Default: `jdbc:postgresql://localhost:5432/bookreview`)                     |`jdbc:postgresql://<Host Name>:<Port>/<Database Name>`|
|`SPRING_DATASOURCE_USERNAME`|Set database user name.(Default: `postgres`)                                                  |`<USERNAME>`|
|`SPRING_DATASOURCE_PASSWORD`|Set database user password.(Default: `postgres`)                                              |`<PASSWORD>`|
|`JAVA_LOG_LEVEL`            |Set log level.(Default: `INFO`)                                                               |`TRACE`/`DEBUG`/`INFO`/`WARN`/`ERROR`/`FATAL`/`OFF`|