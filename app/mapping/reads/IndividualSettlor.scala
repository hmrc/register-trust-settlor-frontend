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

import java.time.LocalDate

import models.pages.{Address, FullName, PassportOrIdCardDetails}
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, __}

final case class IndividualSettlor(name: FullName,
                                   dateOfBirth: Option[LocalDate],
                                   nino: Option[String],
                                   address : Option[Address],
                                   passport: Option[PassportOrIdCardDetails],
                                   idCard: Option[PassportOrIdCardDetails]) extends Settlor {

  def passportOrId: Option[PassportOrIdCardDetails] = if (passport.isDefined) passport else idCard
}

object IndividualSettlor extends SettlorReads {

  implicit lazy val reads: Reads[IndividualSettlor] = {
    ((__ \ "name").read[FullName] and
      (__ \ "dateOfBirth").readNullable[LocalDate] and
      (__ \ "nino").readNullable[String] and
      readAddress() and
      (__ \ "passport").readNullable[PassportOrIdCardDetails] and
      (__ \ "idCard").readNullable[PassportOrIdCardDetails])(IndividualSettlor.apply _)
  }
}

