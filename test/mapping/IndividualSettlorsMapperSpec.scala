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
import models.pages.{FullName, InternationalAddress, PassportOrIdCardDetails, UKAddress}
import models.{AddressType, IdentificationType, PassportType, Settlor, UserAnswers, YesNoDontKnow}
import pages.living_settlor.individual._
import pages.living_settlor.individual.mld5._

import java.time.LocalDate

class IndividualSettlorsMapperSpec extends SpecBase {

  private val aliveAtRegistration: Boolean = true
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

  private val index: Int = 0

  "IndividualSettlors mapper" must {

    val mapper: IndividualSettlorsMapper = injector.instanceOf[IndividualSettlorsMapper]

    "map user answers to individual settlor model" when {

      "no settlors" in {

        val result = mapper.build(emptyUserAnswers)

        result mustBe None
      }

      "one settlor" when {

        "NINO" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorAliveYesNoPage(index), true).success.value
            .set(SettlorIndividualNamePage(index), name).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
            .set(SettlorIndividualNINOYesNoPage(index), true).success.value
            .set(SettlorIndividualNINOPage(index), nino).success.value

          val result = mapper.build(userAnswers).get

          result mustBe List(Settlor(
            aliveAtRegistration = aliveAtRegistration,
            name = name,
            dateOfBirth = None,
            identification = Some(IdentificationType(
              nino = Some(nino),
              passport = None,
              address = None
            )),
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          ))
        }

        "UK address and passport" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorAliveYesNoPage(index), true).success.value
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
            aliveAtRegistration = aliveAtRegistration,
            name = name,
            dateOfBirth = None,
            identification = Some(IdentificationType(
              nino = None,
              passport = Some(passportOrIdCardType),
              address = Some(ukAddressType)
            )),
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          ))
        }

        "non-UK address and ID card" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorAliveYesNoPage(index), true).success.value
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
            aliveAtRegistration = aliveAtRegistration,
            name = name,
            dateOfBirth = None,
            identification = Some(IdentificationType(
              nino = None,
              passport = Some(passportOrIdCardType),
              address = Some(nonUkAddressType)
            )),
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          ))
        }

        "UK address and no passport/ID card" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorAliveYesNoPage(index), true).success.value
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
            aliveAtRegistration = aliveAtRegistration,
            name = name,
            dateOfBirth = None,
            identification = Some(IdentificationType(
              nino = None,
              passport = None,
              address = Some(nonUkAddressType)
            )),
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          ))
        }

        "no identification" in {

          val userAnswers: UserAnswers = emptyUserAnswers
            .set(SettlorAliveYesNoPage(index), true).success.value
            .set(SettlorIndividualNamePage(index), name).success.value
            .set(SettlorIndividualDateOfBirthYesNoPage(index), true).success.value
            .set(SettlorIndividualDateOfBirthPage(index), date).success.value
            .set(SettlorIndividualNINOYesNoPage(index), false).success.value
            .set(SettlorAddressYesNoPage(index), false).success.value

          val result = mapper.build(userAnswers).get

          result mustBe List(Settlor(
            aliveAtRegistration = aliveAtRegistration,
            name = name,
            dateOfBirth = Some(date),
            identification = None,
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          ))
        }
      }

      "more than one settlor" in {

        def name(index: Int): FullName = FullName("Name", None, s"$index")

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(SettlorAliveYesNoPage(index), true).success.value
          .set(SettlorIndividualNamePage(0), name(0)).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(0), false).success.value
          .set(SettlorIndividualNINOYesNoPage(0), false).success.value
          .set(SettlorAddressYesNoPage(0), false).success.value

          .set(SettlorAliveYesNoPage(1), true).success.value
          .set(SettlorIndividualNamePage(1), name(1)).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(1), false).success.value
          .set(SettlorIndividualNINOYesNoPage(1), false).success.value
          .set(SettlorAddressYesNoPage(1), false).success.value

        val result = mapper.build(userAnswers).get

        result mustBe List(
          Settlor(
            aliveAtRegistration = aliveAtRegistration,
            name = name(0),
            dateOfBirth = None,
            identification = None,
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          ),
          Settlor(
            aliveAtRegistration = aliveAtRegistration,
            name = name(1),
            dateOfBirth = None,
            identification = None,
            countryOfResidence = None,
            nationality = None,
            legallyIncapable = None
          )
        )
      }

      "country of nationality and residency unknown" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(SettlorAliveYesNoPage(index), true).success.value
          .set(SettlorIndividualNamePage(index), name).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
          .set(CountryOfNationalityYesNoPage(index), false).success.value
          .set(SettlorIndividualNINOYesNoPage(index), false).success.value
          .set(CountryOfResidencyYesNoPage(index), false).success.value
          .set(SettlorAddressYesNoPage(index), false).success.value
          .set(MentalCapacityYesNoPage(index), YesNoDontKnow.Yes).success.value

        val result = mapper.build(userAnswers).get

        result mustBe List(Settlor(
          aliveAtRegistration = aliveAtRegistration,
          name = name,
          dateOfBirth = None,
          identification = None,
          countryOfResidence = None,
          nationality = None,
          legallyIncapable = Some(false)
        ))
      }

      "UK country of nationality and residency" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(SettlorAliveYesNoPage(index), true).success.value
          .set(SettlorIndividualNamePage(index), name).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
          .set(CountryOfNationalityYesNoPage(index), true).success.value
          .set(UkCountryOfNationalityYesNoPage(index), true).success.value
          .set(SettlorIndividualNINOYesNoPage(index), false).success.value
          .set(CountryOfResidencyYesNoPage(index), true).success.value
          .set(UkCountryOfResidencyYesNoPage(index), true).success.value
          .set(SettlorAddressYesNoPage(index), false).success.value
          .set(MentalCapacityYesNoPage(index), YesNoDontKnow.No).success.value

        val result = mapper.build(userAnswers).get

        result mustBe List(Settlor(
          aliveAtRegistration = aliveAtRegistration,
          name = name,
          dateOfBirth = None,
          identification = None,
          countryOfResidence = Some("GB"),
          nationality = Some("GB"),
          legallyIncapable = Some(true)
        ))
      }

      "non-UK country of nationality and residency" in {

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(SettlorAliveYesNoPage(index), true).success.value
          .set(SettlorIndividualNamePage(index), name).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
          .set(CountryOfNationalityYesNoPage(index), true).success.value
          .set(UkCountryOfNationalityYesNoPage(index), false).success.value
          .set(CountryOfNationalityPage(index), "FR").success.value
          .set(SettlorIndividualNINOYesNoPage(index), false).success.value
          .set(CountryOfResidencyYesNoPage(index), true).success.value
          .set(UkCountryOfResidencyYesNoPage(index), false).success.value
          .set(CountryOfResidencyPage(index), "ES").success.value
          .set(SettlorAddressYesNoPage(index), false).success.value
          .set(MentalCapacityYesNoPage(index), YesNoDontKnow.DontKnow).success.value

        val result = mapper.build(userAnswers).get

        result mustBe List(Settlor(
          aliveAtRegistration = aliveAtRegistration,
          name = name,
          dateOfBirth = None,
          identification = None,
          countryOfResidence = Some("ES"),
          nationality = Some("FR"),
          legallyIncapable = None
        ))
      }

      "settlor is not alive at the time of registration" in {
        val userAnswers: UserAnswers = emptyUserAnswers
          .set(SettlorAliveYesNoPage(index), false).success.value
          .set(SettlorIndividualNamePage(index), name).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
          .set(SettlorIndividualNINOYesNoPage(index), true).success.value
          .set(SettlorIndividualNINOPage(index), nino).success.value
          .set(MentalCapacityYesNoPage(index), YesNoDontKnow.Yes).success.value

        val result = mapper.build(userAnswers).get

        result mustBe List(Settlor(
          aliveAtRegistration = false,
          name = name,
          dateOfBirth = None,
          identification = Some(IdentificationType(
            nino = Some(nino),
            passport = None,
            address = None
          )),
          countryOfResidence = None,
          nationality = None,
          legallyIncapable = None
        ))
      }
    }
  }
}
