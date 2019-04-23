package com.seanmcapp

import spray.json._

// TODO: implement integration test, to test serde
object InputJSON {

  val telegramPrivateChatInput =
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

  val telegramGroupChatInput =
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

  val callbackQueryChatInput =
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

  val instagramResponse =
    """{
      |  "data": {
      |    "user": {
      |      "edge_owner_to_timeline_media": {
      |        "count": 647,
      |        "page_info": {
      |          "has_next_page": true,
      |          "end_cursor": "AQBDekAeKDKo3YIlGK3W6G-AP5_ud8eQgUBc6n7rAG0S7odgpRMiiguo5tJ3rVYJdUaKq3O2N_TJxdCwNr7xXmEtIEdeJD2s8hEJVY3m0sQq5w"
      |        },
      |        "edges": [
      |          {
      |            "node": {
      |              "id": "1832730068440590249",
      |              "__typename": "GraphImage",
      |              "edge_media_to_caption": {
      |                "edges": [
      |                  {
      |                    "node": {
      |                      "text": "Pamela Sidarta. FTUI\u201918"
      |                    }
      |                  }
      |                ]
      |              },
      |              "shortcode": "BlvKkl6lCepkYu3s3J2U77oyPJC11ue5-xRt5M0",
      |              "edge_media_to_comment": {
      |                "count": 177
      |              },
      |              "comments_disabled": false,
      |              "taken_at_timestamp": 1500000000,
      |              "dimensions": {
      |                "height": 1350,
      |                "width": 1080
      |              },
      |              "display_url": "https://instagram.fbkk8-2.fna.fbcdn.net/vp/65f17984866f6bb4e6bff1db5bbe6234/5C135B45/t51.2885-15/e35/37895376_1902483769828463_1029784315489157120_n.jpg",
      |              "edge_media_preview_like": {
      |                "count": 12991
      |              },
      |              "owner": {
      |                "id": "1435973343"
      |              },
      |              "thumbnail_src": "https://instagram.fbkk8-2.fna.fbcdn.net/vp/37c66e4ee2fd8b7a4fde3e22a6ee8a3f/5C1436DD/t51.2885-15/sh0.08/e35/c0.135.1080.1080/s640x640/37895376_1902483769828463_1029784315489157120_n.jpg",
      |              "thumbnail_resources": [
      |                {
      |                  "src": "https://instagram.fbkk8-2.fna.fbcdn.net/vp/976db61873197b81a8f1fdf251888565/5BDA1F4A/t51.2885-15/e35/c0.135.1080.1080/s150x150/37895376_1902483769828463_1029784315489157120_n.jpg",
      |                  "config_width": 150,
      |                  "config_height": 150
      |                },
      |                {
      |                  "src": "https://instagram.fbkk8-2.fna.fbcdn.net/vp/35f37cc653c6c74a80c02111ee62276a/5C11814C/t51.2885-15/e35/c0.135.1080.1080/s240x240/37895376_1902483769828463_1029784315489157120_n.jpg",
      |                  "config_width": 240,
      |                  "config_height": 240
      |                },
      |                {
      |                  "src": "https://instagram.fbkk8-2.fna.fbcdn.net/vp/de47fa49cd022ebf82baf32628c41b5b/5BF3C732/t51.2885-15/e35/c0.135.1080.1080/s320x320/37895376_1902483769828463_1029784315489157120_n.jpg",
      |                  "config_width": 320,
      |                  "config_height": 320
      |                },
      |                {
      |                  "src": "https://instagram.fbkk8-2.fna.fbcdn.net/vp/32e4f21086a9b4df85d9b640887dad01/5BFC0E75/t51.2885-15/e35/c0.135.1080.1080/s480x480/37895376_1902483769828463_1029784315489157120_n.jpg",
      |                  "config_width": 480,
      |                  "config_height": 480
      |                },
      |                {
      |                  "src": "https://instagram.fbkk8-2.fna.fbcdn.net/vp/37c66e4ee2fd8b7a4fde3e22a6ee8a3f/5C1436DD/t51.2885-15/sh0.08/e35/c0.135.1080.1080/s640x640/37895376_1902483769828463_1029784315489157120_n.jpg",
      |                  "config_width": 640,
      |                  "config_height": 640
      |                }
      |              ],
      |              "is_video": false
      |            }
      |          }
      |        ]
      |      }
      |    }
      |  },
      |  "status": "ok"
      |}""".stripMargin

}
