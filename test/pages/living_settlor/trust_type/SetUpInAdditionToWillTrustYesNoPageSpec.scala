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

package pages.living_settlor.trust_type

import models.UserAnswers
import models.pages.DeedOfVariation.ReplacedWill
import models.pages.KindOfTrust.Deed
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import sections.{DeceasedSettlor, LivingSettlors}

class SetUpInAdditionToWillTrustYesNoPageSpec extends PageBehaviours {

  "SetUpInAdditionToWillTrustYesNoPage" must {

    beRetrievable[Boolean](SetUpInAdditionToWillTrustYesNoPage)

    beSettable[Boolean](SetUpInAdditionToWillTrustYesNoPage)

    beRemovable[Boolean](SetUpInAdditionToWillTrustYesNoPage)

    "remove relevant data" when {

      val page = SetUpInAdditionToWillTrustYesNoPage

      "set to false" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, true).success.value
              .set(HowDeedOfVariationCreatedPage, ReplacedWill).success.value

            val result = answers.set(page, false).success.value

            result.get(DeceasedSettlor) must not be defined
            result.get(HowDeedOfVariationCreatedPage) must not be defined
        }
      }

        "set to true" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers: UserAnswers = initial.set(page, false).success.value
                .set(KindOfTrustPage, Deed).success.value

              val result = answers.set(page, true).success.value

              result.get(LivingSettlors) must not be defined
          }
        }

      }
    }
  }


