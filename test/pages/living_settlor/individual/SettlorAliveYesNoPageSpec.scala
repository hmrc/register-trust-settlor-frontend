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

package pages.living_settlor.individual

import models.{UserAnswers, YesNoDontKnow}
import pages.behaviours.PageBehaviours
import org.scalacheck.Arbitrary.arbitrary
import pages.living_settlor.individual.mld5.MentalCapacityYesNoPage

class SettlorAliveYesNoPageSpec extends PageBehaviours {

  "SettlorAliveYesNoPage" must {

    beRetrievable[Boolean](SettlorAliveYesNoPage(0))

    beSettable[Boolean](SettlorAliveYesNoPage(0))

    beRemovable[Boolean](SettlorAliveYesNoPage(0))
  }

  "remove data for mental capacity question" when {

    val page = SettlorAliveYesNoPage(0)

    "set to false" in {
      forAll(arbitrary[UserAnswers]) { initial =>
        val answers: UserAnswers = initial
          .set(page, true)
          .success
          .value
          .set(MentalCapacityYesNoPage(0), YesNoDontKnow.Yes)
          .success
          .value

        val result = answers.set(page, false).success.value

        result.get(MentalCapacityYesNoPage(0)) must not be defined
      }
    }
  }
}
