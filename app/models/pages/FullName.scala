/*
 * Copyright 2022 HM Revenue & Customs
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

package models.pages

import play.api.libs.json._

case class FullName(firstName: String, middleName: Option[String], lastName: String) {

  override def toString = s"$firstName $lastName"

  def displayFullName: String = middleName match {
    case Some(middleName) => s"$firstName $middleName $lastName"
    case None => this.toString
  }

}

object FullName {

  implicit lazy val formats: OFormat[FullName] = Json.format[FullName]

}
