/*
 * Copyright 2021 HM Revenue & Customs
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

package pages.deceased_settlor

import models.UserAnswers
import models.pages.UKAddress
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class SettlorsNINoYesNoPageSpec extends PageBehaviours {

  "SettlorsNINoYesNoPage" must {

    beRetrievable[Boolean](SettlorsNationalInsuranceYesNoPage)

    beSettable[Boolean](SettlorsNationalInsuranceYesNoPage)

    beRemovable[Boolean](SettlorsNationalInsuranceYesNoPage)
  }

  "remove SettlorNinoPage when settlorsNationalInsuranceYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(SettlorNationalInsuranceNumberPage, str).success.value
        val result = answers.set(SettlorsNationalInsuranceYesNoPage, false).success.value

        result.get(SettlorNationalInsuranceNumberPage) mustNot be(defined)
    }
  }

  "remove relevant Data when SettlorsNationalInsurancePage is set to true" in {
    forAll(arbitrary[UserAnswers], arbitrary[String]) {
      (initial, str) =>
        val answers: UserAnswers = initial.set(SettlorsLastKnownAddressYesNoPage, true).success.value
          .set(WasSettlorsAddressUKYesNoPage, true).success.value
        .set(SettlorsUKAddressPage, UKAddress(str, str, Some(str), Some(str), str)).success.value

        val result = answers.set(SettlorsNationalInsuranceYesNoPage, true).success.value

        result.get(SettlorsLastKnownAddressYesNoPage) mustNot be(defined)
        result.get(WasSettlorsAddressUKYesNoPage) mustNot be(defined)
        result.get(SettlorsUKAddressPage) mustNot be(defined)
    }
  }
}
