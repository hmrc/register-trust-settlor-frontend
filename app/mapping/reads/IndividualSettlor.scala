/*
 * Copyright 2021 HM Revenue & Customs
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

import mapping.IdentificationMapper.{buildAddress, buildPassport}
import models.IdentificationType
import models.pages.{Address, FullName, PassportOrIdCardDetails}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, __}

import java.time.LocalDate

final case class IndividualSettlor(name: FullName,
                                   dateOfBirth: Option[LocalDate],
                                   nino: Option[String],
                                   address: Option[Address],
                                   passport: Option[PassportOrIdCardDetails],
                                   idCard: Option[PassportOrIdCardDetails],
                                   countryOfResidence: Option[String],
                                   nationality: Option[String],
                                   hasMentalCapacity: Option[Boolean]) extends Settlor {

  def passportOrId: Option[PassportOrIdCardDetails] = if (passport.isDefined) passport else idCard

  val identification: Option[IdentificationType] = (nino, passportOrId, address) match {
    case (None, None, None) => None
    case _ => Some(IdentificationType(nino, buildPassport(passportOrId), buildAddress(address)))
  }
}

object IndividualSettlor extends SettlorReads {

  implicit lazy val reads: Reads[IndividualSettlor] = (
    (__ \ "name").read[FullName] and
      (__ \ "dateOfBirth").readNullable[LocalDate] and
      (__ \ "nino").readNullable[String] and
      readAddress() and
      (__ \ "passport").readNullable[PassportOrIdCardDetails] and
      (__ \ "idCard").readNullable[PassportOrIdCardDetails] and
      (__ \ "countryOfResidency").readNullable[String] and
      (__ \ "countryOfNationality").readNullable[String] and
      (__ \ "mentalCapacityYesNo").readNullable[Boolean]
    )(IndividualSettlor.apply _)

}
