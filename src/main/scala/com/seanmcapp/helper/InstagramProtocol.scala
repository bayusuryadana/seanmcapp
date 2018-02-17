package com.seanmcapp.helper

import com.seanmcapp.model.{InstagramNode, InstagramUser}
import spray.json._

object InstagramProtocol extends DefaultJsonProtocol {

  object InstagramNodeFormat extends RootJsonFormat[InstagramNode] {

    override def write(obj: InstagramNode) = {
      JsObject(
        "id" -> JsString(obj.id),
        "caption" -> JsString(obj.caption),
        "thumbnailSrc" -> JsString(obj.thumbnailSrc),
        "date" -> JsNumber(obj.date))
    }

    override def read(value: JsValue) = {
      value.asJsObject.getFields("id", "caption", "thumbnail_src", "date") match {
        case Seq(JsString(id), JsString(caption), JsString(thumbnail_src), JsNumber(date)) =>
          InstagramNode(id, caption, thumbnail_src, date.toLong)
        case _ => throw new DeserializationException("failed to deserialize InstagramNode")
      }
    }

  }

  object InstagramUserFormat extends RootJsonFormat[InstagramUser] {

    override def write(obj: InstagramUser) = {
      JsObject(
        "id" -> JsString(obj.id),
        "biography" -> JsString(obj.biography),
        "fullName" -> JsString(obj.fullName),
        "isPrivate" -> JsBoolean(obj.isPrivate),
        "username" -> JsString(obj.username),
        "nodes" -> obj.nodes.map(InstagramNodeFormat.write).toJson)
    }

    override def read(value: JsValue) = {
      val jsObject = value.asJsObject.getFields("user").map(_.asJsObject)
        .headOption.getOrElse(throw new DeserializationException("failed to deserialize InstagramUser at 'user'"))

      val nodesJsValue = jsObject.getFields("media").flatMap(_.asJsObject.getFields("nodes"))
      val field = jsObject.getFields("id", "biography", "full_name", "is_private", "username") ++ nodesJsValue
      field match {
        case (Seq(JsString(id), JsString(biography), JsString(fullName), JsBoolean(isPrivate), JsString(username),
        JsArray(nodes))) =>
          InstagramUser(id, biography, fullName, isPrivate, username, nodes.map(InstagramNodeFormat.read))
        case _ => throw new DeserializationException("failed to deserialize InstagramUser at field")
      }
    }

  }

}
