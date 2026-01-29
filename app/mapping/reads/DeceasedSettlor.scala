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

package mapping.reads

import mapping.IdentificationMapper.buildAddress
import models.Identification
import models.pages.{Address, FullName}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, __}

import java.time.LocalDate

final case class DeceasedSettlor(
  name: FullName,
  dateOfDeath: Option[LocalDate],
  dateOfBirth: Option[LocalDate],
  nino: Option[String],
  address: Option[Address],
  countryOfResidence: Option[String],
  nationality: Option[String]
) {

  val identification: Option[Identification] = (nino, address) match {
    case (None, None) => None
    case (Some(_), _) => Some(Identification(nino, None))
    case _            => Some(Identification(None, buildAddress(address)))
  }

}

object DeceasedSettlor extends SettlorReads {

  implicit lazy val reads: Reads[DeceasedSettlor] = (
    (__ \ "name").read[FullName] and
      (__ \ "dateOfDeath").readNullable[LocalDate] and
      (__ \ "settlorsDateOfBirth").readNullable[LocalDate] and
      (__ \ "nationalInsuranceNumber").readNullable[String] and
      readAddress() and
      (__ \ "countryOfResidence").readNullable[String] and
      (__ \ "countryOfNationality").readNullable[String]
  )(DeceasedSettlor.apply _)

}
