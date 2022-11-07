/*
 * Copyright 2022 HM Revenue & Customs
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

import models.UserAnswers
import models.pages.{InternationalAddress, PassportOrIdCardDetails, UKAddress}
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

import java.time.LocalDate

class SettlorIndividualNINOYesNoPageSpec extends PageBehaviours {

  "SettlorIndividualNINOYesNoPage" must {

    beRetrievable[Boolean](SettlorIndividualNINOYesNoPage(0))

    beSettable[Boolean](SettlorIndividualNINOYesNoPage(0))

    beRemovable[Boolean](SettlorIndividualNINOYesNoPage(0))

    "remove relevant data" when {

      val page = SettlorIndividualNINOYesNoPage(0)

      "set to true" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, false).success.value
              .set(SettlorAddressYesNoPage(0), true).success.value
              .set(SettlorAddressUKYesNoPage(0), true).success.value
              .set(SettlorAddressUKPage(0), UKAddress("", "", None, None, "")).success.value
              .set(SettlorAddressInternationalPage(0), InternationalAddress("", "", None, "")).success.value
              .set(SettlorIndividualPassportYesNoPage(0), true).success.value
              .set(SettlorIndividualPassportPage(0), PassportOrIdCardDetails("", "", LocalDate.now())).success.value
              .set(SettlorIndividualIDCardYesNoPage(0), true).success.value
              .set(SettlorIndividualIDCardPage(0), PassportOrIdCardDetails("", "", LocalDate.now())).success.value

            val result = answers.set(page, true).success.value

            result.get(SettlorAddressYesNoPage(0)) must not be defined
            result.get(SettlorAddressUKYesNoPage(0)) must not be defined
            result.get(SettlorAddressUKPage(0)) must not be defined
            result.get(SettlorAddressInternationalPage(0)) must not be defined
            result.get(SettlorIndividualPassportYesNoPage(0)) must not be defined
            result.get(SettlorIndividualPassportPage(0)) must not be defined
            result.get(SettlorIndividualIDCardYesNoPage(0)) must not be defined
            result.get(SettlorIndividualIDCardPage(0)) must not be defined
        }
      }

      "set to false" in {
        forAll(arbitrary[UserAnswers]) {
          initial =>
            val answers: UserAnswers = initial.set(page, true).success.value
              .set(SettlorIndividualNINOPage(0), "QQ123456C").success.value

            val result = answers.set(page, false).success.value

            result.get(SettlorIndividualNINOPage(0)) must not be defined
        }
      }
    }
  }
}
