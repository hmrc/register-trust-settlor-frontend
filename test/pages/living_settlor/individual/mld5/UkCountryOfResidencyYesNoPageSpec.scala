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

package pages.living_settlor.individual.mld5

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class UkCountryOfResidencyYesNoPageSpec extends PageBehaviours {

  private val gbCountry: String    = "GB"
  private val nonGbCountry: String = "FR"

  "UkCountryOfResidencyYesNoPage" must {

    beRetrievable[Boolean](UkCountryOfResidencyYesNoPage(0))

    beSettable[Boolean](UkCountryOfResidencyYesNoPage(0))

    beRemovable[Boolean](UkCountryOfResidencyYesNoPage(0))

    "implement cleanup logic" when {
      "NO selected and previous selection was YES" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers = userAnswers
            .set(CountryOfResidencyPage(0), gbCountry)
            .success
            .value
            .set(UkCountryOfResidencyYesNoPage(0), false)
            .success
            .value

          result.get(CountryOfResidencyPage(0)) mustNot be(defined)
        }
    }

    "not implement cleanup logic" when {

      "NO selected and previous selection was NO" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers = userAnswers
            .set(UkCountryOfResidencyYesNoPage(0), false)
            .success
            .value
            .set(CountryOfResidencyPage(0), nonGbCountry)
            .success
            .value
            .set(UkCountryOfResidencyYesNoPage(0), false)
            .success
            .value

          result.get(CountryOfResidencyPage(0))       must be(defined)
          result.get(CountryOfResidencyPage(0)).get mustBe nonGbCountry
        }

      "NO selected and no previous selection" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers = userAnswers
            .set(UkCountryOfResidencyYesNoPage(0), false)
            .success
            .value

          result.get(CountryOfResidencyPage(0)) mustNot be(defined)
        }
    }

    "set CountryOfNationalityPage to GB" when {
      "YES selected" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers = userAnswers
            .set(UkCountryOfResidencyYesNoPage(0), true)
            .success
            .value

          result.get(CountryOfResidencyPage(0))       must be(defined)
          result.get(CountryOfResidencyPage(0)).get mustBe gbCountry
        }
    }
  }

}
