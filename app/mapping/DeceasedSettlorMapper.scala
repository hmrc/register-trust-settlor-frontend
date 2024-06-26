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

package mapping

import mapping.reads.DeceasedSettlor
import models.{UserAnswers, WillType}
import sections.{DeceasedSettlor => entity}

class DeceasedSettlorMapper extends {

  def build(userAnswers: UserAnswers): Option[WillType] =
    userAnswers.getAtPath(entity.path)(DeceasedSettlor.reads) map { settlor =>
      WillType(
        name = settlor.name,
        dateOfBirth = settlor.dateOfBirth,
        dateOfDeath = settlor.dateOfDeath,
        identification = settlor.identification,
        countryOfResidence = settlor.countryOfResidence,
        nationality = settlor.nationality
      )
    }

}
