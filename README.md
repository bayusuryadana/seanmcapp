# Seanmcapp
![Scala CI](https://github.com/bayusuryadana/seanmcapp/workflows/Scala%20CI/badge.svg?branch=master)
[![codecov](https://codecov.io/gh/bayusuryadana/seanmcapp/branch/master/graph/badge.svg)](https://codecov.io/gh/bayusuryadana/seanmcapp)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c80ce1baef8543eaaa730e45b3cc6c02)](https://www.codacy.com/app/bayusuryadana/seanmcapp?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=bayusuryadana/seanmcapp&amp;utm_campaign=Badge_Grade)

## Feature
API: CBC, Dota, Wallet
Scheduler: AirVisual, Birthday, DotaMetadata, DsdaJakarta, IGrow, NCov

## Prerequisites
1. Scala & SBT (automatically installed if you use intellij IDEA)
2. Docker (optional: for IT testing or running in local)
 
## Infrastructure
1. Postgre SQL 
2. Amazon S3 (using Minio for IT test)

## Contribute
### Compile
- `sbt clean compile` to clean all cache and compile the code

### Unit Test
- `sbt test` to run all the unit test
- `sbt "testOnly *ClassNameSpec"` (yes, with asterix character) run only particular test class

### Integration test
run `docker-compose up -d` for running necessary service dependencies

- `sbt it:test` to run all the IT test (all the test inside `it` folder)
- `sbt "it:testOnly *ClassNameSpec"` for run specific class integration tests

### Local run
run `sbt -Dconfig.resource=application-local.conf run`. It might not perfectly works since you don't have some of my credentials such as Instagram or Telegram account

## Closing
feel free to contact me at bayusuryadana@gmail.com  
happy coding ^^
