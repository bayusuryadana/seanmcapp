package com.seanmcapp.scheduler

import com.seanmcapp.SchedulerForTest
import com.seanmcapp.mock.repository.PlayerRepoMock
import com.seanmcapp.util.parser.decoder.{PlayerResponse, ProfileResponse}
import org.mockito.Mockito.when
import org.scalatest.{AsyncWordSpec, Matchers}

import scala.io.Source

class DotaMetadataSchedulerSpec extends AsyncWordSpec with Matchers with SchedulerForTest {

  "DotaMetadataScheduler should return correctly" in {
    val dota = new DotaMetadataScheduler(startTime, interval, PlayerRepoMock, http)
    val baseurl = "https://api.opendota.com/api/players/"
    PlayerRepoMock.playersList.map { player =>
      val mockResponse = Source.fromResource("scheduler/dota/player_" + player.id + ".json").mkString
      when(http.sendRequest(baseurl + player.id)).thenReturn(mockResponse)
    }
    val expected = List(
      PlayerResponse(ProfileResponse("hnymnky", "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/d1/d145e4465a020a67d8bcdefb362dae8019d2af4f_full.jpg"),None),
      PlayerResponse(ProfileResponse("travengers", "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/c0/c03e59d51d93b3df4c6ea5f49a246ef1ed2836e6_full.jpg"),Some(43)),
      PlayerResponse(ProfileResponse("SeanmcrayZ", "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/88/88b1333d6f78f9426fb51141c6d5fa8254b6e798_full.jpg"),Some(54)),
      PlayerResponse(ProfileResponse("OMEGALUL", "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/58/5899046336c4a14358467c331a9de24f6daded9f_full.jpg"),Some(67)),
      PlayerResponse(ProfileResponse("lightzard", "https://steamcdn-a.akamaihd.net/steamcommunity/public/images/avatars/d3/d36ccfdfde5f66d6a109e2252d94337471277cf4_full.jpg"),None)
    )

    dota.task.map { res =>
      res shouldBe expected
    }
  }

}
