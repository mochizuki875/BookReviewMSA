# BookReview BFF
BookReview BFF is one of the compornent of BookReview MSA.

## Prerequisites

```
openjdk version "11.0.2" 2019-01-15
OpenJDK Runtime Environment 18.9 (build 11.0.2+9)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.2+9, mixed mode)
org.springframework.boot 2.6.7
```

## Run

```
# ./gradlew bootRun
```

or

```
# ./gradlew build .
# java -jar build/libs/BookReviewBFF-0.0.1-SNAPSHOT.jar
```

### Docker

```
# docker build -t bookreview-bff .
# docker run -d -p 8080:8080 --name bookreview-bff bookreview-bff
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
|`BOOK_API_URL`              |Set `Book API` URL.(ex `http://book-api:8080/api/book`)                                       |`http://<Book API Host Name>:<Book API Port>/api/book`|
|`REVIEW_API_URL`            |Set `Review API` URL.(ex `http://review-api:8080/api/review`)                                 |`http://<Review API Host Name>:<Review API Port>/api/review`|
|`JAVA_LOG_LEVEL`            |Set log level.(Default: `INFO`)                                                               |`TRACE`/`DEBUG`/`INFO`/`WARN`/`ERROR`/`FATAL`/`OFF`|