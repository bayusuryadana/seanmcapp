package com.seanmcapp.API

import com.seanmcapp.{InjectionTest, InputJSON}
import org.scalatest.{AsyncWordSpec, Matchers}

class TelegramAPISpec extends AsyncWordSpec with Matchers with InjectionTest {

  "private chat call" in {
    telegramAPI.flow(InputJSON.telegramPrivateChatInput).map { res =>
      res.toString should equal ("200")
    }
  }

  "group chat call" in {
    telegramAPI.flow(InputJSON.telegramGroupChatInput).map { res =>
      res.toString should equal ("200")
    }
  }

  "callback query (vote) chat call" in {
    telegramAPI.flow(InputJSON.callbackQueryChatInput).map { res =>
      res.toString should equal ("200")
    }
  }

}