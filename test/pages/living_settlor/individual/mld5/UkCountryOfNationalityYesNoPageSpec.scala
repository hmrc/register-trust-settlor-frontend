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

package pages.living_settlor.individual.mld5

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class UkCountryOfNationalityYesNoPageSpec extends PageBehaviours {

  private val gbCountry: String    = "GB"
  private val nonGbCountry: String = "FR"

  "UkCountryOfNationalityYesNoPage" must {

    beRetrievable[Boolean](UkCountryOfNationalityYesNoPage(0))

    beSettable[Boolean](UkCountryOfNationalityYesNoPage(0))

    beRemovable[Boolean](UkCountryOfNationalityYesNoPage(0))

    "implement cleanup logic" when {
      "NO selected and previous selection was YES" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers = userAnswers
            .set(CountryOfNationalityPage(0), gbCountry)
            .success
            .value
            .set(UkCountryOfNationalityYesNoPage(0), false)
            .success
            .value

          result.get(CountryOfNationalityPage(0)) mustNot be(defined)
        }
    }

    "not implement cleanup logic" when {

      "NO selected and previous selection was NO" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers = userAnswers
            .set(CountryOfNationalityPage(0), nonGbCountry)
            .success
            .value
            .set(UkCountryOfNationalityYesNoPage(0), false)
            .success
            .value

          result.get(CountryOfNationalityPage(0))       must be(defined)
          result.get(CountryOfNationalityPage(0)).get mustBe nonGbCountry
        }

      "NO selected and no previous selection" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers = userAnswers
            .set(UkCountryOfNationalityYesNoPage(0), false)
            .success
            .value

          result.get(CountryOfNationalityPage(0)) mustNot be(defined)
        }
    }

    "set CountryOfNationalityPage to GB" when {
      "YES selected" in
        forAll(arbitrary[UserAnswers]) { userAnswers =>
          val result: UserAnswers = userAnswers
            .set(UkCountryOfNationalityYesNoPage(0), true)
            .success
            .value

          result.get(CountryOfNationalityPage(0))       must be(defined)
          result.get(CountryOfNationalityPage(0)).get mustBe gbCountry
        }
    }
  }

}
