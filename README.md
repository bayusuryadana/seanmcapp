# Seanmcapp
API that provide something that you need for your future. Currently im hosting in heroku (free). 
These are the endpoint lists:
- **GET** / --> welcome screen
- **GET** /api/{method} --> fill it with `latest` and `random`, it will give you a photo respectively with the name itself
- **POST** /webhook --> endpoint for bot api, which has 4 commands (/getRandom (*cbc*), /getLatest, /subscribe, 
/unsubscribe and callback query for vote). Subscribed user will get new notification if sync process got new data.
- **GET** /birthday --> will check if today is one of my friend's birthday or not


## Prerequisites
1. Scala & SBT (usually it will be automatically installed if u are using intellij)

#### Optional (if u want to test in production mode by yourself)
1. MySQL 
2. telegram bot (api and bot name)

## How to run (locally)
in progress

## How to run (production)
in progress

feel free to contact me at seanmcrayz@yahoo.com  
happy coding ^^