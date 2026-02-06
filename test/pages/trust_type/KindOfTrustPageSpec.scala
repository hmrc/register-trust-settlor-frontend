/*
 * Copyright 2026 HM Revenue & Customs
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
import models.pages.Status._
import models.pages.{DeedOfVariation, FullName, KindOfBusiness, KindOfTrust}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours
import pages.living_settlor.business.{SettlorBusinessTimeYesNoPage, SettlorBusinessTypePage}
import pages.{DeceasedSettlorStatus, deceased_settlor => deceasedPages}
import sections.DeceasedSettlor

import java.time.LocalDate

class KindOfTrustPageSpec extends PageBehaviours {

  private val name: FullName = FullName("Joe", None, "Bloggs")

  "KindOfTrustPage" must {

    beRetrievable[KindOfTrust](KindOfTrustPage)

    beSettable[KindOfTrust](KindOfTrustPage)

    beRemovable[KindOfTrust](KindOfTrustPage)

    "implement cleanup" when {

      def userAnswers(initial: UserAnswers, date: LocalDate): UserAnswers =
        initial
          .set(SetUpInAdditionToWillTrustYesNoPage, false)
          .success
          .value
          .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill)
          .success
          .value
          .set(HoldoverReliefYesNoPage, true)
          .success
          .value
          .set(EfrbsYesNoPage, true)
          .success
          .value
          .set(EfrbsStartDatePage, date)
          .success
          .value
          .set(SettlorBusinessTypePage(0), KindOfBusiness.Trading)
          .success
          .value
          .set(SettlorBusinessTimeYesNoPage(0), true)
          .success
          .value
          .set(SettlorBusinessTypePage(1), KindOfBusiness.Investment)
          .success
          .value
          .set(SettlorBusinessTimeYesNoPage(1), false)
          .success
          .value
          .set(deceasedPages.SettlorsNamePage, name)
          .success
          .value
          .set(DeceasedSettlorStatus, Completed)
          .success
          .value

      "Deed selected" in
        forAll(arbitrary[UserAnswers], arbitrary[LocalDate]) { (initial, date) =>
          val answers: UserAnswers = userAnswers(initial, date)

          val result = answers.set(KindOfTrustPage, KindOfTrust.Deed).success.value

          result.get(SetUpInAdditionToWillTrustYesNoPage) mustBe defined
          result.get(HowDeedOfVariationCreatedPage)       mustBe defined
          result.get(HoldoverReliefYesNoPage) mustNot be(defined)
          result.get(EfrbsYesNoPage) mustNot be(defined)
          result.get(EfrbsStartDatePage) mustNot be(defined)
          result.get(SettlorBusinessTypePage(0)) mustNot be(defined)
          result.get(SettlorBusinessTimeYesNoPage(0)) mustNot be(defined)
          result.get(SettlorBusinessTypePage(1)) mustNot be(defined)
          result.get(SettlorBusinessTimeYesNoPage(1)) mustNot be(defined)
          result.get(DeceasedSettlor)                     mustBe defined
        }

      "Intervivos selected" in
        forAll(arbitrary[UserAnswers], arbitrary[LocalDate]) { (initial, date) =>
          val answers: UserAnswers = userAnswers(initial, date)

          val result = answers.set(KindOfTrustPage, KindOfTrust.Intervivos).success.value

          result.get(SetUpInAdditionToWillTrustYesNoPage) mustNot be(defined)
          result.get(HowDeedOfVariationCreatedPage) mustNot be(defined)
          result.get(HoldoverReliefYesNoPage) mustBe defined
          result.get(EfrbsYesNoPage) mustNot be(defined)
          result.get(EfrbsStartDatePage) mustNot be(defined)
          result.get(SettlorBusinessTypePage(0)) mustNot be(defined)
          result.get(SettlorBusinessTimeYesNoPage(0)) mustNot be(defined)
          result.get(SettlorBusinessTypePage(1)) mustNot be(defined)
          result.get(SettlorBusinessTimeYesNoPage(1)) mustNot be(defined)
          result.get(DeceasedSettlor) mustNot be(defined)
        }

      "FlatManagement selected" in
        forAll(arbitrary[UserAnswers], arbitrary[LocalDate]) { (initial, date) =>
          val answers: UserAnswers = userAnswers(initial, date)

          val result = answers.set(KindOfTrustPage, KindOfTrust.FlatManagement).success.value

          result.get(SetUpInAdditionToWillTrustYesNoPage) mustNot be(defined)
          result.get(HowDeedOfVariationCreatedPage) mustNot be(defined)
          result.get(HoldoverReliefYesNoPage) mustNot be(defined)
          result.get(EfrbsYesNoPage) mustNot be(defined)
          result.get(EfrbsStartDatePage) mustNot be(defined)
          result.get(SettlorBusinessTypePage(0)) mustNot be(defined)
          result.get(SettlorBusinessTimeYesNoPage(0)) mustNot be(defined)
          result.get(SettlorBusinessTypePage(1)) mustNot be(defined)
          result.get(SettlorBusinessTimeYesNoPage(1)) mustNot be(defined)
          result.get(DeceasedSettlor) mustNot be(defined)
        }

      "HeritageMaintenanceFund selected" in
        forAll(arbitrary[UserAnswers], arbitrary[LocalDate]) { (initial, date) =>
          val answers: UserAnswers = userAnswers(initial, date)

          val result = answers.set(KindOfTrustPage, KindOfTrust.HeritageMaintenanceFund).success.value

          result.get(SetUpInAdditionToWillTrustYesNoPage) mustNot be(defined)
          result.get(HowDeedOfVariationCreatedPage) mustNot be(defined)
          result.get(HoldoverReliefYesNoPage) mustNot be(defined)
          result.get(EfrbsYesNoPage) mustNot be(defined)
          result.get(EfrbsStartDatePage) mustNot be(defined)
          result.get(SettlorBusinessTypePage(0)) mustNot be(defined)
          result.get(SettlorBusinessTimeYesNoPage(0)) mustNot be(defined)
          result.get(SettlorBusinessTypePage(1)) mustNot be(defined)
          result.get(SettlorBusinessTimeYesNoPage(1)) mustNot be(defined)
          result.get(DeceasedSettlor) mustNot be(defined)
        }

      "Employees selected" in
        forAll(arbitrary[UserAnswers], arbitrary[LocalDate]) { (initial, date) =>
          val answers: UserAnswers = userAnswers(initial, date)

          val result = answers.set(KindOfTrustPage, KindOfTrust.Employees).success.value

          result.get(SetUpInAdditionToWillTrustYesNoPage) mustNot be(defined)
          result.get(HowDeedOfVariationCreatedPage) mustNot be(defined)
          result.get(HoldoverReliefYesNoPage) mustNot be(defined)
          result.get(EfrbsYesNoPage)                  mustBe defined
          result.get(EfrbsStartDatePage)              mustBe defined
          result.get(SettlorBusinessTypePage(0))      mustBe defined
          result.get(SettlorBusinessTimeYesNoPage(0)) mustBe defined
          result.get(SettlorBusinessTypePage(1))      mustBe defined
          result.get(SettlorBusinessTimeYesNoPage(1)) mustBe defined
          result.get(DeceasedSettlor) mustNot be(defined)
        }
    }
  }

}
