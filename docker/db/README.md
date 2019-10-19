how to make dockerize postgres with pre-populated data

https://medium.com/@sherryhsu/set-up-postgresql-database-using-with-production-data-using-docker-f164694341f1

- dump or create your DB schema and data as SQL file
<br/>`pg_dump --host=<host> --port=<port> --username=<username> --password --dbname=<dbname> > backup.sql`

- create dockerfile like below, these lines will download the postgres itself and copy the `backup.sql` to `/docker-entrypoint-initdb.d/` as all the sql scripts will be automatically run during container startup
```
FROM postgres:11.4-alpine 
COPY backup.sql /docker-entrypoint-initdb.d/
RUN chmod a+r /docker-entrypoint-initdb.d/*
```

- build and publish to your docker repo
```
$ docker build -t "seanmcrayz/seanmcpostgres:latest" . 
$ docker login -u "$DOCKER_USER" -p "$DOCKER_PASS" 
$ docker push "seanmcrayz/seanmcpostgres:latest"
```

done :)