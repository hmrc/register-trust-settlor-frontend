/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package models

import play.api.Logging
import play.api.libs.functional.syntax._
import play.api.libs.json._
import queries.{Gettable, Settable}
import viewmodels.{SettlorBusinessViewModel, SettlorIndividualViewModel}

import scala.util.{Failure, Success, Try}

trait ReadableUserAnswers {
  val data: JsObject
  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    Reads.at(page.path).reads(data) match {
      case JsSuccess(value, _) => Some(value)
      case JsError(_)          =>
        None
    }
}

case class ReadOnlyUserAnswers(data: JsObject) extends ReadableUserAnswers

object ReadOnlyUserAnswers {
  implicit lazy val formats: OFormat[ReadOnlyUserAnswers] = Json.format[ReadOnlyUserAnswers]
}

final case class UserAnswers(
  draftId: String,
  data: JsObject = Json.obj(),
  internalAuthId: String,
  isTaxable: Boolean = true,
  existingTrustUtr: Option[String] = None
) extends Logging {

  def checkDeceasedSettlorNino(
    deceasedSettlorNino: String,
    existingTrusteeNino: Option[String] = None,
    existingBeneficiaryNino: Option[String] = None,
    existingProtectorNino: Option[String] = None
  ): Boolean =
    existingTrusteeNino.contains(deceasedSettlorNino) || existingBeneficiaryNino.contains(
      deceasedSettlorNino
    ) || existingProtectorNino.contains(deceasedSettlorNino)

  def get[A](page: Gettable[A])(implicit rds: Reads[A]): Option[A] =
    getAtPath(page.path)

  def getAtPath[A](path: JsPath)(implicit rds: Reads[A]): Option[A] =
    Reads.at(path).reads(data) match {
      case JsSuccess(value, _) => Some(value)
      case JsError(errors)     =>
        logger.info(s"Tried to read path $path errors: $errors")
        None
    }

  def set[A](page: Settable[A], value: A)(implicit writes: Writes[A]): Try[UserAnswers] = {

    val updatedData: Try[JsObject] = data.setObject(page.path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors)       =>
        logger.warn(s"Unable to set path ${page.path} due to errors")
        Failure(JsResultException(errors))
    }

    updatedData.flatMap { d =>
      val updatedAnswers = copy(data = d)
      page.cleanup(Some(value), updatedAnswers)
    }
  }

  def remove[A](query: Settable[A]): Try[UserAnswers] = {

    val updatedData = data.removeObject(query.path) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_)            =>
        Success(data)
    }

    updatedData.flatMap { d =>
      val updatedAnswers = copy(data = d)
      query.cleanup(None, updatedAnswers)
    }
  }

  def deleteAtPath(path: JsPath): Try[UserAnswers] =
    data
      .removeObject(path)
      .map(obj => copy(data = obj))
      .fold(
        _ => Success(this),
        result => Success(result)
      )

  val settlors: LivingSettlors = {
    val living = this.get(sections.LivingSettlors).getOrElse(List.empty)
    LivingSettlors(
      living.collect { case x: SettlorIndividualViewModel => x },
      living.collect { case x: SettlorBusinessViewModel => x }
    )
  }
}

object UserAnswers {

  implicit lazy val reads: Reads[UserAnswers] = (
    (__ \ "_id").read[String] and
      (__ \ "data").read[JsObject] and
      (__ \ "internalId").read[String] and
      (__ \ "isTaxable").readWithDefault[Boolean](true) and
      (__ \ "existingTrustUtr").readNullable[String]
  )(UserAnswers.apply _)

  implicit lazy val writes: OWrites[UserAnswers] = (
    (__ \ "_id").write[String] and
      (__ \ "data").write[JsObject] and
      (__ \ "internalId").write[String] and
      (__ \ "isTaxable").write[Boolean] and
      (__ \ "existingTrustUtr").writeNullable[String]
  )(unlift(UserAnswers.unapply))
}
