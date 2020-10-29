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

package mapping

import java.time.LocalDate

import base.SpecBase
import models.UserAnswers
import models.pages.{FullName, InternationalAddress, UKAddress}
import pages.deceased_settlor._

class DeceasedSettlorMapperSpec extends SpecBase {

  private val name: FullName = FullName("Joe", None, "Bloggs")
  private val dob: LocalDate = LocalDate.parse("1996-02-03")
  private val dod: LocalDate = LocalDate.parse("2019-02-03")
  private val nino: String = "AA000000A"

  private val addressLine: String = "Line"
  private val postcode: String = "AB1 1AB"
  private val country: String = "FR"
  private val ukAddress: UKAddress = UKAddress(addressLine, addressLine, None, None, postcode)
  private val nonUkAddress: InternationalAddress = InternationalAddress(addressLine, addressLine, None, country)
  private val ukAddressType: AddressType = AddressType(addressLine, addressLine, None, None, Some(postcode), "GB")
  private val nonUkAddressType: AddressType = AddressType(addressLine, addressLine, None, None, None, country)

  "DeceasedSettlor mapper" must {

    val mapper: DeceasedSettlorMapper = injector.instanceOf[DeceasedSettlorMapper]

    "map user answers to deceased settlor model" when {

      "NINO" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(SettlorsNamePage, name).success.value
          .set(SettlorDateOfDeathYesNoPage, false).success.value
          .set(SettlorDateOfBirthYesNoPage, false).success.value
          .set(SettlorsNationalInsuranceYesNoPage, true).success.value
          .set(SettlorNationalInsuranceNumberPage, nino).success.value

        val result = mapper.build(userAnswers).get

        result mustBe WillType(
          name = name,
          dateOfBirth = None,
          dateOfDeath = None,
          identification = Some(Identification(
            nino = Some(nino),
            address = None
          ))
        )

      }

      "UK address" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(SettlorsNamePage, name).success.value
          .set(SettlorDateOfDeathYesNoPage, false).success.value
          .set(SettlorDateOfBirthYesNoPage, false).success.value
          .set(SettlorsNationalInsuranceYesNoPage, false).success.value
          .set(SettlorsLastKnownAddressYesNoPage, true).success.value
          .set(WasSettlorsAddressUKYesNoPage, true).success.value
          .set(SettlorsUKAddressPage, ukAddress).success.value

        val result = mapper.build(userAnswers).get

        result mustBe WillType(
          name = name,
          dateOfBirth = None,
          dateOfDeath = None,
          identification = Some(Identification(
            nino = None,
            address = Some(ukAddressType)
          ))
        )
      }

      "non-UK address" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(SettlorsNamePage, name).success.value
          .set(SettlorDateOfDeathYesNoPage, false).success.value
          .set(SettlorDateOfBirthYesNoPage, false).success.value
          .set(SettlorsNationalInsuranceYesNoPage, false).success.value
          .set(SettlorsLastKnownAddressYesNoPage, true).success.value
          .set(WasSettlorsAddressUKYesNoPage, false).success.value
          .set(SettlorsInternationalAddressPage, nonUkAddress).success.value

        val result = mapper.build(userAnswers).get

        result mustBe WillType(
          name = name,
          dateOfBirth = None,
          dateOfDeath = None,
          identification = Some(Identification(
            nino = None,
            address = Some(nonUkAddressType)
          ))
        )
      }

      "no identification" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(SettlorsNamePage, name).success.value
          .set(SettlorDateOfDeathYesNoPage, true).success.value
          .set(SettlorDateOfDeathPage, dod).success.value
          .set(SettlorDateOfBirthYesNoPage, true).success.value
          .set(SettlorsDateOfBirthPage, dob).success.value
          .set(SettlorsNationalInsuranceYesNoPage, false).success.value
          .set(SettlorsLastKnownAddressYesNoPage, false).success.value

        val result = mapper.build(userAnswers).get

        result mustBe WillType(
          name = name,
          dateOfBirth = Some(dob),
          dateOfDeath = Some(dod),
          identification = None
        )
      }
    }
  }
}
