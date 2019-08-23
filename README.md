# Seanmcapp
[![CircleCI](https://circleci.com/gh/bayusuryadana/seanmcapp.svg?style=svg)](https://circleci.com/gh/bayusuryadana/seanmcapp)
[![codecov](https://codecov.io/gh/bayusuryadana/seanmcapp/branch/master/graph/badge.svg)](https://codecov.io/gh/bayusuryadana/seanmcapp)
[![Codacy Badge](https://api.codacy.com/project/badge/Grade/c80ce1baef8543eaaa730e45b3cc6c02)](https://www.codacy.com/app/bayusuryadana/seanmcapp?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=bayusuryadana/seanmcapp&amp;utm_campaign=Badge_Grade)

## Feature
API:
- CBC API
- Dota API

Scheduled job:
- Igrow
- Birthday
- Amartha
- AirVisual

## Prerequisites
1. Scala & SBT (automatically installed if you use intellij IDEA)
2. Docker
 
## Infrastructure
1. Postgre SQL 
2. Storage service such as Amazon S3

## Contribute
for local development run `sbt -Dconfig.resource=application-local.conf run`, it might not perfectly works since you don't have some of my credentials such as Instagram or Telegram account

0. test only for particular class -> `sbt "testOnly *ClassNameSpec"` (yes, with asterix character) for unit test or `sbt "it:testOnly *ClassNameSpec"` for integration-tests
1. please run `sbt clean compile`, `sbt test` and `sbt it:test` (this one will require docker to complete) before raising a pull request
2. wait for code review

feel free to contact me at bayusuryadana@gmail.com  
happy coding ^^
