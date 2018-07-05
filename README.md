# Seanmcapp
API that provide something that you need for your future. Currently im hosting in heroku (free). 
These are the endpoint lists:
- **GET** / --> welcome screen
- **GET** /api?method= --> fill it with `latest` and `random`, it will give you a photo respectively with the name itself
- **GET** /sync --> sync data from source to DB (which mostly cause timeout for the response, 
but the information will fetch properly). I have cron job for trigger this endpoint every 2 hours.
- **POST** /webhook --> endpoint for telegram api, which has 4 commands (/getRandom (*cbc*), /getLatest, /subscribe, 
/unsubscribe). Subscribed user will get new notification if sync process have new data.
- **POST** /broadcast --> endpoint for broadcasting to all telegram users

## Prerequisites
1. Scala & SBT (usually it will be automatically installed if u are using intellij)

## Optional (for production use)
(if u want to test in production mode by yourself)
1. PostgreSQL 
2. instagram account (username and password)
3. telegram bot (api and bot name)

## How to run (locally)
1. `sbt -Dconfig.resource=/dev.conf run` (it will run using all mock in your local)

## How to run (production)
1. replace all those variables at `/src/main/resources/application.conf`
2. make database schema. Please refer this to `--file not yet created--`. 
3. `sbt run` and its done ... :)

## How to contribute (pleaseeeeeeeee....)
0. discuss with me first if you want and needed
1. create your own changes
2. dont forget to create new unit and flow test
3. `sbt test` should pass all the test
4. make a pull request

feel free to contact me at seanmcrayz@yahoo.com  
happy coding ^^