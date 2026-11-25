/*
 * Copyright 2026 HM Revenue & Customs
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

import play.api.libs.json.{Json, OFormat, Reads, Writes}

import scala.language.implicitConversions

final case class UKAddress(
  line1: String,
  line2: String,
  line3: Option[String] = None,
  line4: Option[String] = None,
  postcode: String
) extends Address

object UKAddress {

  given formats: OFormat[UKAddress] = Json.format[UKAddress]
}

final case class InternationalAddress(
  line1: String,
  line2: String,
  line3: Option[String] = None,
  country: String
) extends Address

object InternationalAddress {

  given formats: OFormat[InternationalAddress] = Json.format[InternationalAddress]
}

sealed trait Address

object Address {

  given reads: Reads[Address] =

    UKAddress.formats
      .map[Address](identity)
      .orElse(
        InternationalAddress.formats.map[Address](identity)
      )

  given writes: Writes[Address] = Writes {
    case address: UKAddress            => Json.toJson(address)(using UKAddress.formats)
    case address: InternationalAddress => Json.toJson(address)(using InternationalAddress.formats)
  }

}
