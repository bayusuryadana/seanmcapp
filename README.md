# seanmcapp
API that provide something that you need for your future. Currently im hosting in heroku (free). 
These are the endpoint lists:
- **GET** / --> welcome screen
- **GET** /getRandom --> return random photo details
- **GET** /getLatest --> return latest synced photo details
- **GET** /sync --> sync data from source to DB (which mostly cause timeout for the response, 
but the information will fetch properly). I have cron job for trigger this endpoint every 2 hours.
- **POST** /webhook --> endpoint for telegram api, which has 4 commands (/getRandom (*cbc*), /getLatest, /subscribe, 
/unsubscribe). Subscribed user will get new notification if sync process have new data.

## Prerequisites
1. Scala
2. SBT
3. PostgreSQL (you can manually change ^_^)
4. instagram account (username and password)
5. telegram bot (api and bot name)

## How to run
1. replace all those variables at `/src/main/resources/application.conf`
2. make database schema following this file (only 2 tables, customers and photos). Please refer this directory
`/src/main/scala/com/seanmcapp/repository/`. (Next iteration i'll make automation tests, so you don't need to use any DB)
3. `sbt run` and its done ... :)
