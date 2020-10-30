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
import models.pages.{FullName, InternationalAddress, PassportOrIdCardDetails, UKAddress}
import pages.living_settlor._
import pages.living_settlor.individual.{SettlorAddressInternationalPage, SettlorAddressUKPage, SettlorAddressUKYesNoPage, SettlorAddressYesNoPage, SettlorIndividualDateOfBirthPage, SettlorIndividualDateOfBirthYesNoPage, SettlorIndividualIDCardPage, SettlorIndividualIDCardYesNoPage, SettlorIndividualNINOPage, SettlorIndividualNINOYesNoPage, SettlorIndividualNamePage, SettlorIndividualPassportPage, SettlorIndividualPassportYesNoPage}

class IndividualSettlorsMapperSpec extends SpecBase {

  private val name: FullName = FullName("Joe", None, "Bloggs")
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val nino: String = "AA000000A"

  private val addressLine: String = "Line"
  private val postcode: String = "AB1 1AB"
  private val country: String = "FR"
  private val ukAddress: UKAddress = UKAddress(addressLine, addressLine, None, None, postcode)
  private val nonUkAddress: InternationalAddress = InternationalAddress(addressLine, addressLine, None, country)
  private val ukAddressType: AddressType = AddressType(addressLine, addressLine, None, None, Some(postcode), "GB")
  private val nonUkAddressType: AddressType = AddressType(addressLine, addressLine, None, None, None, country)

  private val number: String = "1234567890"
  private val passportOrIdCard: PassportOrIdCardDetails = PassportOrIdCardDetails(country, number, date)
  private val passportOrIdCardType: PassportType = PassportType(number, date, country)

  "IndividualSettlors mapper" must {

    val mapper: IndividualSettlorsMapper = injector.instanceOf[IndividualSettlorsMapper]

    "map user answers to individual settlor model" when {

      "no settlors" in {

        val result = mapper.build(emptyUserAnswers)

        result mustBe None
      }
      
      "one settlor" when {

        val index: Int = 0

        "NINO" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorIndividualNamePage(index), name).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), true).success.value
            .set(SettlorIndividualNINOPage(index), nino).success.value

          val result = mapper.build(userAnswers).get

          result mustBe List(Settlor(
            name = name,
            dateOfBirth = None,
            identification = Some(IdentificationType(
              nino = Some(nino),
              passport = None,
              address = None
            ))
          ))
        }

        "UK address and passport" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorIndividualNamePage(index), name).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorAddressYesNoPage(index), true).success.value
            .set(SettlorAddressUKYesNoPage(index), true).success.value
            .set(SettlorAddressUKPage(index), ukAddress).success.value
            .set(SettlorIndividualPassportYesNoPage(index), true).success.value
            .set(SettlorIndividualPassportPage(index), passportOrIdCard).success.value

          val result = mapper.build(userAnswers).get

          result mustBe List(Settlor(
            name = name,
            dateOfBirth = None,
            identification = Some(IdentificationType(
              nino = None,
              passport = Some(passportOrIdCardType),
              address = Some(ukAddressType)
            ))
          ))
        }

        "non-UK address and ID card" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorIndividualNamePage(index), name).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorAddressYesNoPage(index), true).success.value
            .set(SettlorAddressUKYesNoPage(index), false).success.value
            .set(SettlorAddressInternationalPage(index), nonUkAddress).success.value
            .set(SettlorIndividualPassportYesNoPage(index), false).success.value
            .set(SettlorIndividualIDCardYesNoPage(index), true).success.value
            .set(SettlorIndividualIDCardPage(index), passportOrIdCard).success.value

          val result = mapper.build(userAnswers).get

          result mustBe List(Settlor(
            name = name,
            dateOfBirth = None,
            identification = Some(IdentificationType(
              nino = None,
              passport = Some(passportOrIdCardType),
              address = Some(nonUkAddressType)
            ))
          ))
        }

        "UK address and no passport/ID card" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorIndividualNamePage(index), name).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorAddressYesNoPage(index), true).success.value
            .set(SettlorAddressUKYesNoPage(index), false).success.value
            .set(SettlorAddressInternationalPage(index), nonUkAddress).success.value
            .set(SettlorIndividualPassportYesNoPage(index), false).success.value
            .set(SettlorIndividualIDCardYesNoPage(index), false).success.value

          val result = mapper.build(userAnswers).get

          result mustBe List(Settlor(
            name = name,
            dateOfBirth = None,
            identification = Some(IdentificationType(
              nino = None,
              passport = None,
              address = Some(nonUkAddressType)
            ))
          ))
        }

        "no identification" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorIndividualNamePage(index), name).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), true).success.value
            .set(SettlorIndividualDateOfBirthPage(index), date).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorAddressYesNoPage(index), false).success.value

          val result = mapper.build(userAnswers).get

          result mustBe List(Settlor(
            name = name,
            dateOfBirth = Some(date),
            identification = None
          ))
        }
      }

      "more than one settlor" in {

        def name(index: Int): FullName = FullName("Name", None, s"$index")

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(SettlorIndividualNamePage(0), name(0)).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(0), false).success.value
          .set(SettlorIndividualNINOYesNoPage(0), false).success.value
          .set(SettlorAddressYesNoPage(0), false).success.value

          .set(SettlorIndividualNamePage(1), name(1)).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(1), false).success.value
          .set(SettlorIndividualNINOYesNoPage(1), false).success.value
          .set(SettlorAddressYesNoPage(1), false).success.value

        val result = mapper.build(userAnswers).get

        result mustBe List(
          Settlor(
            name = name(0),
            dateOfBirth = None,
            identification = None
          ),
          Settlor(
            name = name(1),
            dateOfBirth = None,
            identification = None
          )
        )
      }
    }
  }
}
