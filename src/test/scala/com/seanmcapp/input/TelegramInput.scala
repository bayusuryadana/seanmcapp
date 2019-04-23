package com.seanmcapp.input

import spray.json._

object TelegramInput {

  val privateChat =
    """{
      |  "update_id":126555239,
      |  "message":{
      |    "message_id":21956,
      |    "from":{
      |      "id":274852283,
      |      "is_bot":false,
      |      "first_name":"Bayu",
      |      "last_name":"Suryadana",
      |      "language_code":"en-TH"
      |    },
      |    "chat":{
      |      "id":274852283,
      |      "first_name":"Bayu",
      |      "last_name":"Suryadana",
      |      "type":"private"
      |    },
      |    "date":1529257485,
      |    "text":"/cari_bahan_ciol",
      |    "entities":[
      |      {
      |        "offset":0,
      |        "length":16,
      |        "type":"bot_command"
      |      }
      |    ]
      |  }
      |}""".stripMargin.parseJson

  val groupChat =
    """{
      |  "update_id": 126558029,
      |  "message": {
      |    "entities": [
      |      {
      |        "offset": 0,
      |        "length": 26,
      |        "type": "bot_command"
      |      }
      |    ],
      |    "text": "/cari_bahan_ciol@seanmcbot",
      |    "chat": {
      |      "id": -111546505,
      |      "title": "Arrasy Abroad 2 : September Ceria ft. Seanmcukari",
      |      "type": "group",
      |      "all_members_are_administrators": false
      |    },
      |    "message_id": 23975,
      |    "date": 1532868862,
      |    "from": {
      |      "first_name": "Muhammad Redho",
      |      "is_bot": false,
      |      "username": "redhoyasa",
      |      "id": 143635997,
      |      "last_name": "Ayassa"
      |    }
      |  }
      |}""".stripMargin.parseJson

  val callbackQuery =
    """{
      |  "update_id": 126555240,
      |  "callback_query": {
      |    "id": "1180481570874991328",
      |    "from": {
      |      "id": 274852283,
      |      "is_bot": false,
      |      "first_name": "Bayu",
      |      "last_name": "Suryadana",
      |      "language_code": "en-TH"
      |    },
      |    "message": {
      |      "message_id": 21958,
      |      "from": {
      |        "id": 354236808,
      |        "is_bot": true,
      |        "first_name": "seanmcbot",
      |        "username": "seanmcbot"
      |      },
      |      "chat": {
      |        "id": 274852283,
      |        "first_name": "Bayu",
      |        "last_name": "Suryadana",
      |        "type": "private"
      |      },
      |      "date": 1529257530,
      |      "photo": [
      |        {
      |          "file_id": "AgADBAADzqcxG9PwPFF-YEvdudKwkotXkRoABKPeW26f-rzLqAUDAAEC",
      |          "file_size": 1346,
      |          "width": 90,
      |          "height": 90
      |        },
      |        {
      |          "file_id": "AgADBAADzqcxG9PwPFF-YEvdudKwkotXkRoABHVxo7FW73XnqQUDAAEC",
      |          "file_size": 16785,
      |          "width": 320,
      |          "height": 320
      |        },
      |        {
      |          "file_id": "AgADBAADzqcxG9PwPFF-YEvdudKwkotXkRoABL6VCn-VnndUqgUDAAEC",
      |          "file_size": 43175,
      |          "width": 640,
      |          "height": 640
      |        }
      |      ],
      |      "caption": "Luthfi Mutia. Peternakan 2014\n@unpad.geulis",
      |      "caption_entities": [
      |        {
      |          "offset": 30,
      |          "length": 6,
      |          "type": "mention"
      |        }
      |      ]
      |    },
      |    "chat_instance": "-4549706067381289510",
      |    "data": "4:1369453443161098174"
      |  }
      |}""".stripMargin.parseJson

}
