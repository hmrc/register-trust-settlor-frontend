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
import models.pages.IndividualOrBusiness.{Business, Individual}
import models.pages.{FullName, KindOfTrust, Status, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.{DeceasedSettlorStatus, LivingSettlorStatus}
import pages.deceased_settlor._
import pages.living_settlor._
import pages.behaviours.PageBehaviours
import pages.deceased_settlor.SettlorsNamePage
import pages.living_settlor.business.SettlorBusinessNamePage
import pages.living_settlor.individual.{SettlorAddressUKPage, SettlorAddressUKYesNoPage, SettlorIndividualDateOfBirthPage, SettlorIndividualDateOfBirthYesNoPage, SettlorIndividualIDCardYesNoPage, SettlorIndividualNINOPage, SettlorIndividualNINOYesNoPage, SettlorIndividualNamePage, SettlorIndividualPassportYesNoPage}

class SetUpAfterSettlorDiedYesNoPageSpec extends PageBehaviours {

  "SetUpAfterSettlorDiedPage" must {

    beRetrievable[Boolean](SetUpAfterSettlorDiedYesNoPage)

    beSettable[Boolean](SetUpAfterSettlorDiedYesNoPage)

    beRemovable[Boolean](SetUpAfterSettlorDiedYesNoPage)
  }

  "when becoming a living settlor" must {

    "remove relevant data and Nino when SetUpAfterSettlorDiedPage set to false" in {
      forAll(arbitrary[UserAnswers], arbitrary[String]) {
        (initial, str) =>
          val answers: UserAnswers = initial
            .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
            .set(SettlorsNamePage, FullName(str,None, str)).success.value
            .set(SettlorDateOfDeathYesNoPage, true).success.value
            .set(SettlorDateOfDeathPage, LocalDate.now).success.value
            .set(SettlorDateOfBirthYesNoPage, true).success.value
            .set(SettlorsDateOfBirthPage, LocalDate.now.minusDays(10)).success.value
            .set(SettlorsNationalInsuranceYesNoPage, true).success.value
            .set(SettlorNationalInsuranceNumberPage, str).success.value
            .set(DeceasedSettlorStatus, Status.Completed).success.value

          val result = answers.set(SetUpAfterSettlorDiedYesNoPage, false).success.value

          result.get(SettlorsNamePage) mustNot be(defined)
          result.get(SettlorDateOfDeathYesNoPage) mustNot be(defined)
          result.get(SettlorDateOfDeathPage) mustNot be(defined)
          result.get(SettlorDateOfBirthYesNoPage) mustNot be(defined)
          result.get(SettlorsDateOfBirthPage) mustNot be(defined)
          result.get(SettlorsNationalInsuranceYesNoPage) mustNot be(defined)
          result.get(SettlorNationalInsuranceNumberPage) mustNot be(defined)
          result.get(DeceasedSettlorStatus) mustNot be(defined)
      }
    }

    "remove relevant data and addresses when SetUpAfterSettlorDiedPage set to false" in {
      forAll(arbitrary[UserAnswers], arbitrary[String]) {
        (initial, str) =>
          val answers: UserAnswers = initial.set(SettlorsNamePage, FullName(str,None, str)).success.value
            .set(SettlorDateOfDeathYesNoPage, true).success.value
            .set(SettlorDateOfDeathPage, LocalDate.now).success.value
            .set(SettlorDateOfBirthYesNoPage, true).success.value
            .set(SettlorsDateOfBirthPage, LocalDate.now.minusDays(10)).success.value
            .set(SettlorsNationalInsuranceYesNoPage, false).success.value
            .set(SettlorsLastKnownAddressYesNoPage, true).success.value
            .set(WasSettlorsAddressUKYesNoPage, true).success.value
            .set(SettlorsUKAddressPage, UKAddress(str, str, Some(str), Some(str), str)).success.value
            .set(DeceasedSettlorStatus, Status.Completed).success.value

          val result = answers.set(SetUpAfterSettlorDiedYesNoPage, false).success.value

          result.get(SettlorsNamePage) mustNot be(defined)
          result.get(SettlorDateOfDeathYesNoPage) mustNot be(defined)
          result.get(SettlorDateOfDeathPage) mustNot be(defined)
          result.get(SettlorDateOfBirthYesNoPage) mustNot be(defined)
          result.get(SettlorsDateOfBirthPage) mustNot be(defined)
          result.get(SettlorsNationalInsuranceYesNoPage) mustNot be(defined)
          result.get(SettlorsLastKnownAddressYesNoPage) mustNot be(defined)
          result.get(WasSettlorsAddressUKYesNoPage) mustNot be(defined)
          result.get(SettlorsUKAddressPage) mustNot be(defined)
          result.get(DeceasedSettlorStatus) mustNot be(defined)

      }
    }
  }

  "when becoming a deceased settlor" must {

    "remove trust type data and data for Nino individual when SetUpAfterSettlorDiedPage is set to true" in {
      forAll(arbitrary[UserAnswers], arbitrary[String]) {
        (initial, str) =>
          val answers: UserAnswers = initial
            // settlor with nino
            .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
            .set(HoldoverReliefYesNoPage, true).success.value
            .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
            .set(SettlorIndividualNamePage(0), FullName("First", None,"Last")).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(0), true).success.value
            .set(SettlorIndividualDateOfBirthPage(0), LocalDate.of(2010,10,10)).success.value
            .set(SettlorIndividualNINOYesNoPage(0), true).success.value
            .set(SettlorIndividualNINOPage(0), "JP121212A").success.value
            .set(LivingSettlorStatus(0), Status.Completed).success.value
            // settlor with address
            .set(SettlorIndividualOrBusinessPage(1), Individual).success.value
            .set(SettlorIndividualNamePage(1), FullName("First", None,"Last")).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(1), true).success.value
            .set(SettlorIndividualDateOfBirthPage(1), LocalDate.of(2010,10,10)).success.value
            .set(SettlorIndividualNINOYesNoPage(1), false).success.value
            .set(SettlorAddressUKYesNoPage(1), true).success.value
            .set(SettlorAddressUKPage(1), UKAddress(
              line1 = "line 1",
              line2= "Newcastle",
              postcode = "NE981ZZ"
            )).success.value
            .set(SettlorIndividualPassportYesNoPage(1), false).success.value
            .set(SettlorIndividualIDCardYesNoPage(1), false).success.value
            .set(LivingSettlorStatus(1), Status.Completed).success.value
            // business settlor
            .set(SettlorIndividualOrBusinessPage(2), Business).success.value
            .set(SettlorBusinessNamePage(2), "Fake Business").success.value

          val result = answers.set(SetUpAfterSettlorDiedYesNoPage, true).success.value

          result.get(KindOfTrustPage) mustNot be(defined)
          result.get(HoldoverReliefYesNoPage) mustNot be(defined)

          result.get(SettlorIndividualOrBusinessPage(0)) mustNot be(defined)
          result.get(SettlorIndividualNamePage(0)) mustNot be(defined)
          result.get(SettlorIndividualDateOfBirthYesNoPage(0)) mustNot be(defined)
          result.get(SettlorIndividualDateOfBirthPage(0)) mustNot be(defined)
          result.get(SettlorIndividualNINOYesNoPage(0)) mustNot be(defined)
          result.get(SettlorIndividualNINOPage(0)) mustNot be(defined)
          result.get(LivingSettlorStatus(0)) mustNot be(defined)

          result.get(SettlorIndividualOrBusinessPage(1)) mustNot be(defined)
          result.get(SettlorIndividualNamePage(1)) mustNot be(defined)
          result.get(SettlorIndividualDateOfBirthYesNoPage(1)) mustNot be(defined)
          result.get(SettlorIndividualDateOfBirthPage(1)) mustNot be(defined)
          result.get(SettlorIndividualNINOYesNoPage(1)) mustNot be(defined)
          result.get(SettlorAddressUKYesNoPage(1)) mustNot be(defined)
          result.get(SettlorAddressUKPage(1)) mustNot be(defined)
          result.get(SettlorIndividualPassportYesNoPage(1)) mustNot be(defined)
          result.get(SettlorIndividualIDCardYesNoPage(1)) mustNot be(defined)
          result.get(LivingSettlorStatus(1)) mustNot be(defined)

          result.get(SettlorIndividualOrBusinessPage(2)) mustNot be(defined)
          result.get(SettlorBusinessNamePage(2)) mustNot be(defined)

      }
    }

  }

}
