how to make dockerize postgres with pre-populated data

https://medium.com/@sherryhsu/set-up-postgresql-database-using-with-production-data-using-docker-f164694341f1

1. dump / create your DB schema and data as SQL file
pg_dump --host=<host> --port=<port> --username=<username> --password --dbname=<dbname> > backup.sql

2. create dockerfile like below, these lines will download the postgres itself and copy the backup.sql to /docker-entrypoint-initdb.d/ as all the sql scripts will be automatically run during container startup
FROM postgres:11.4-alpine 
COPY *.sql /docker-entrypoint-initdb.d/
RUN chmod a+r /docker-entrypoint-initdb.d/*

3. build and publisht to your docker repo
$ docker build -t "$IMAGE_NAME:$NEW_TAG" . 
$ docker login -u "$DOCKER_USER" -p "$DOCKER_PASS" 
$ docker push "$IMAGE_NAME:$NEW_TAG"

4. run
$ docker run -p 5432:5432 "$IMAGE_NAME:$NEW_TAG"