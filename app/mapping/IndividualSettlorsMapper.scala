/*
 * Copyright 2020 HM Revenue & Customs
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

package mapping

import javax.inject.Inject
import mapping.reads.{BusinessSettlor, IndividualSettlor}
import models.UserAnswers
import models.pages.PassportOrIdCardDetails

class IndividualSettlorsMapper @Inject()(nameMapper: NameMapper, addressMapper: AddressMapper) extends Mapping[List[Settlor]] {

   def build(userAnswers: UserAnswers): Option[List[Settlor]] = {
     val settlors = userAnswers.get(mapping.reads.LivingSettlors).getOrElse(List.empty[IndividualSettlor])

     val mappedSettlors = settlors.flatMap {
       case ls: IndividualSettlor =>
       Some(Settlor(nameMapper.build(ls.name), ls.dateOfBirth, identificationMap(ls)))
       case _: BusinessSettlor =>
         None
     }

     mappedSettlors match {
       case Nil => None
       case _ => Some(mappedSettlors)
     }
  }

  private def identificationMap(settlor: IndividualSettlor): Option[IdentificationType] = {

    val identificationType = IdentificationType(
      settlor.nino,
      passportOrIdMap(settlor.passportOrId),
      {
        (settlor.ukAddress, settlor.internationalAddress) match {
          case (None, None) => None
          case (Some(address), _) => Some(addressMapper.build(address))
          case (_, Some(address)) => Some(addressMapper.build(address))
        }
      }
    )

    identificationType match {
      case IdentificationType(None, None, None) => None
      case _ => Some(identificationType)
    }
  }

  private def passportOrIdMap(passportOrIdCardDetails: Option[PassportOrIdCardDetails]): Option[PassportType] = {
    passportOrIdCardDetails map { passportOrIdCardDetails =>
      PassportType(passportOrIdCardDetails.cardNumber, passportOrIdCardDetails.expiryDate, passportOrIdCardDetails.country)
    }
  }
}
