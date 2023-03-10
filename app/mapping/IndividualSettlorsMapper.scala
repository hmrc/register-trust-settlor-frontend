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

package mapping

import mapping.reads.IndividualSettlor
import models.Settlor
import models.YesNoDontKnow.{No, Yes}

class IndividualSettlorsMapper extends Mapping[Settlor, IndividualSettlor] {

  override def settlorType(settlor: IndividualSettlor): Settlor =
    Settlor(
      aliveAtRegistration = settlor.aliveAtRegistration,
      name = settlor.name,
      dateOfBirth = settlor.dateOfBirth,
      identification = settlor.identification,
      countryOfResidence = settlor.countryOfResidence,
      nationality = settlor.nationality,
      legallyIncapable = {
        (settlor.aliveAtRegistration, settlor.hasMentalCapacity) match {
          case (false, _) => None
          case (true, Some(Yes)) => Some(false)
          case (true, Some(No)) => Some(true)
          case (_, _) => None
        }
      }
    )
}
