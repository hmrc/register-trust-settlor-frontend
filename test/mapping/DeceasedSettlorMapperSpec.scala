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

package mapping

import base.SpecBase
import models.pages.FullName
import models.{Identification, UserAnswers, WillType}
import pages.deceased_settlor._
import pages.deceased_settlor.mld5._

class DeceasedSettlorMapperSpec extends SpecBase {

  private val name: FullName = FullName("Joe", None, "Bloggs")
  private val nino: String   = "AA000000A"

  "DeceasedSettlor mapper" must {

    val mapper: DeceasedSettlorMapper = injector.instanceOf[DeceasedSettlorMapper]

    "map user answers to deceased settlor model" when {

      "5mld" when {

        "country of nationality and residency unknown" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorsNamePage, name)
            .success
            .value
            .set(SettlorDateOfDeathYesNoPage, false)
            .success
            .value
            .set(SettlorDateOfBirthYesNoPage, false)
            .success
            .value
            .set(CountryOfNationalityYesNoPage, false)
            .success
            .value
            .set(SettlorsNationalInsuranceYesNoPage, true)
            .success
            .value
            .set(SettlorNationalInsuranceNumberPage, nino)
            .success
            .value
            .set(CountryOfResidenceYesNoPage, false)
            .success
            .value

          val result = mapper.build(userAnswers).get

          result mustBe WillType(
            name = name,
            dateOfBirth = None,
            dateOfDeath = None,
            identification = Some(
              Identification(
                nino = Some(nino),
                address = None
              )
            ),
            countryOfResidence = None,
            nationality = None
          )

        }

        "UK country of nationality and residency" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorsNamePage, name)
            .success
            .value
            .set(SettlorDateOfDeathYesNoPage, false)
            .success
            .value
            .set(SettlorDateOfBirthYesNoPage, false)
            .success
            .value
            .set(CountryOfNationalityYesNoPage, true)
            .success
            .value
            .set(CountryOfNationalityInTheUkYesNoPage, true)
            .success
            .value
            .set(SettlorsNationalInsuranceYesNoPage, true)
            .success
            .value
            .set(SettlorNationalInsuranceNumberPage, nino)
            .success
            .value
            .set(CountryOfResidenceYesNoPage, true)
            .success
            .value
            .set(CountryOfResidenceInTheUkYesNoPage, true)
            .success
            .value

          val result = mapper.build(userAnswers).get

          result mustBe WillType(
            name = name,
            dateOfBirth = None,
            dateOfDeath = None,
            identification = Some(
              Identification(
                nino = Some(nino),
                address = None
              )
            ),
            countryOfResidence = Some("GB"),
            nationality = Some("GB")
          )

        }

        "Non-UK country of nationality and residency" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorsNamePage, name)
            .success
            .value
            .set(SettlorDateOfDeathYesNoPage, false)
            .success
            .value
            .set(SettlorDateOfBirthYesNoPage, false)
            .success
            .value
            .set(CountryOfNationalityYesNoPage, true)
            .success
            .value
            .set(CountryOfNationalityInTheUkYesNoPage, false)
            .success
            .value
            .set(CountryOfNationalityPage, "ES")
            .success
            .value
            .set(SettlorsNationalInsuranceYesNoPage, true)
            .success
            .value
            .set(SettlorNationalInsuranceNumberPage, nino)
            .success
            .value
            .set(CountryOfResidenceYesNoPage, true)
            .success
            .value
            .set(CountryOfResidenceInTheUkYesNoPage, false)
            .success
            .value
            .set(CountryOfResidencePage, "FR")
            .success
            .value

          val result = mapper.build(userAnswers).get

          result mustBe WillType(
            name = name,
            dateOfBirth = None,
            dateOfDeath = None,
            identification = Some(
              Identification(
                nino = Some(nino),
                address = None
              )
            ),
            countryOfResidence = Some("FR"),
            nationality = Some("ES")
          )

        }

      }

    }

  }
}
