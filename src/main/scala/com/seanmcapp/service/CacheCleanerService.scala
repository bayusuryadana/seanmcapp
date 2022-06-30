package com.seanmcapp.service

import com.seanmcapp.repository.CacheRepo
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext.Implicits.global

class CacheCleanerService(cacheRepo: CacheRepo) extends ScheduledTask {

  def run: Any = {
    cacheRepo.getAll().map(_.map { cache =>
      if (cache.expiry.exists(epoch => new DateTime(epoch*1000).isBeforeNow)) {
        cacheRepo.delete(cache.feature)
      }
    })
  }

}
