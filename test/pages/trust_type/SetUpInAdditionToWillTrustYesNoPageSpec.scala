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

package pages.trust_type

import models.UserAnswers
import models.pages.Status.Completed
import models.pages.{DeedOfVariation, FullName}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.{DeceasedSettlorStatus, LivingSettlorStatus}
import sections.{DeceasedSettlor, LivingSettlors}

class SetUpInAdditionToWillTrustYesNoPageSpec extends PageBehaviours {

  private val name: FullName = FullName("Joe", None, "Bloggs")

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
              .set(pages.deceased_settlor.SettlorsNamePage, name).success.value
              .set(DeceasedSettlorStatus, Completed).success.value

            val result = answers.set(page, false).success.value

            result.get(DeceasedSettlor) must not be defined
        }
      }

        "set to true" in {
          forAll(arbitrary[UserAnswers]) {
            initial =>
              val answers: UserAnswers = initial.set(page, false).success.value
                .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill).success.value
                .set(pages.living_settlor.individual.SettlorIndividualNamePage(0), name).success.value
                .set(LivingSettlorStatus(0), Completed).success.value
                .set(pages.living_settlor.business.SettlorBusinessNamePage(1), name.toString).success.value
                .set(LivingSettlorStatus(1), Completed).success.value

              val result = answers.set(page, true).success.value

              result.get(HowDeedOfVariationCreatedPage) must not be defined
              result.get(LivingSettlors) must not be defined
          }
        }

      }
    }
  }


