/*
 * Copyright 2023 HM Revenue & Customs
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
import models.IdentificationOrgType
import models.pages.Address
import play.api.libs.functional.syntax._
import play.api.libs.json.{Reads, __}

final case class BusinessSettlor(name: String,
                                 utr: Option[String],
                                 countryOfResidence: Option[String],
                                 address: Option[Address],
                                 companyType: Option[String],
                                 companyTime: Option[Boolean]) extends Settlor {

  val identification: Option[IdentificationOrgType] = (utr, address) match {
    case (None, None) => None
    case _ => Some(IdentificationOrgType(utr, buildAddress(address)))
  }
}

object BusinessSettlor extends SettlorReads {

  implicit lazy val reads: Reads[BusinessSettlor] = (
    (__ \ "businessName").read[String] and
      (__ \ "utr").readNullable[String] and
      (__ \ "countryOfResidence").readNullable[String] and
      readAddress() and
      (__ \ "companyType").readNullable[String] and
      (__ \ "companyTime").readNullable[Boolean]
    )(BusinessSettlor.apply _)

}
