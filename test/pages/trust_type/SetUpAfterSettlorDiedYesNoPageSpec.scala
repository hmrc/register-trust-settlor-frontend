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

package pages.trust_type

import java.time.LocalDate

import models.UserAnswers
import models.pages.{DeedOfVariation, FullName, KindOfTrust, Status}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.deceased_settlor.SettlorsNamePage
import pages.living_settlor.business.SettlorBusinessNamePage
import pages.living_settlor.individual.SettlorIndividualNamePage
import pages.{DeceasedSettlorStatus, LivingSettlorStatus}
import sections.{DeceasedSettlor, LivingSettlors}

class SetUpAfterSettlorDiedYesNoPageSpec extends PageBehaviours {

  "SetUpAfterSettlorDiedPage" must {

    beRetrievable[Boolean](SetUpAfterSettlorDiedYesNoPage)

    beSettable[Boolean](SetUpAfterSettlorDiedYesNoPage)

    beRemovable[Boolean](SetUpAfterSettlorDiedYesNoPage)
  }

  "implement cleanup" when {

    "no selected" in {

      forAll(arbitrary[UserAnswers], arbitrary[String]) {
        (initial, str) =>
          val answers: UserAnswers = initial
            .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
            .set(SettlorsNamePage, FullName(str, None, str)).success.value
            .set(DeceasedSettlorStatus, Status.Completed).success.value

          val result = answers.set(SetUpAfterSettlorDiedYesNoPage, false).success.value

          result.get(DeceasedSettlor) mustNot be(defined)
      }
    }

    "yes selected" in {

      forAll(arbitrary[UserAnswers], arbitrary[String], arbitrary[LocalDate]) {
        (initial, str, date) =>
          val answers: UserAnswers = initial
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
            .set(SetUpInAdditionToWillTrustYesNoPage, false).success.value
            .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill).success.value
            .set(HoldoverReliefYesNoPage, true).success.value
            .set(EfrbsYesNoPage, true).success.value
            .set(EfrbsStartDatePage, date).success.value
            .set(SettlorIndividualNamePage(0), FullName(str, None, str)).success.value
            .set(LivingSettlorStatus(0), Status.Completed).success.value
            .set(SettlorBusinessNamePage(0), str).success.value
            .set(LivingSettlorStatus(1), Status.Completed).success.value

          val result = answers.set(SetUpAfterSettlorDiedYesNoPage, true).success.value

          result.get(KindOfTrustPage) mustNot be(defined)
          result.get(SetUpInAdditionToWillTrustYesNoPage) mustNot be(defined)
          result.get(HowDeedOfVariationCreatedPage) mustNot be(defined)
          result.get(HoldoverReliefYesNoPage) mustNot be(defined)
          result.get(EfrbsYesNoPage) mustNot be(defined)
          result.get(EfrbsStartDatePage) mustNot be(defined)
          result.get(LivingSettlors) mustNot be(defined)
      }
    }
  }

}
