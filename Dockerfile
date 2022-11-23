# first stage
FROM hseeberger/scala-sbt:11.0.6_1.3.8_2.13.1 AS build
COPY ./ ./
RUN sbt clean compile universal:packageZipTarball

## second stage
FROM openjdk:8-jre-alpine3.9 
COPY --from=build /root/target/universal/seanmcapp-latest.tgz /seanmcapp.tgz
RUN tar -xf /seanmcapp.tgz
#RUN apk add --no-cache bash
#CMD ["bash"]
ENTRYPOINT ["./seanmcapp-latest/bin/seanmcapp"]

