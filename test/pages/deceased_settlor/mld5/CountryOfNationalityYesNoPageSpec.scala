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

package pages.deceased_settlor.mld5

import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import pages.behaviours.PageBehaviours

class CountryOfNationalityYesNoPageSpec extends PageBehaviours {

  "CountryOfNationalityYesNoPage" must {

    beRetrievable[Boolean](CountryOfNationalityYesNoPage)

    beSettable[Boolean](CountryOfNationalityYesNoPage)

    beRemovable[Boolean](CountryOfNationalityYesNoPage)
  }

  "remove pages when CountryOfNationalityYesNoPage is set to false" in {
    forAll(arbitrary[UserAnswers]) {
      initial =>
        val answers: UserAnswers = initial.set(CountryOfNationalityInTheUkYesNoPage, false).success.value
          .set(CountryOfNationalityPage, "ES").success.value

        val result = answers.set(CountryOfNationalityYesNoPage, false).success.value

        result.get(CountryOfNationalityInTheUkYesNoPage) mustNot be(defined)
        result.get(CountryOfNationalityPage) mustNot be(defined)
    }
  }
}
