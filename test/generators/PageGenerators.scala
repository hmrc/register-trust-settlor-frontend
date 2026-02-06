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

package generators

import org.scalacheck.Arbitrary
import pages.AddASettlorPage
import pages.deceased_settlor._
import pages.living_settlor._
import pages.living_settlor.business.SettlorBusinessNamePage
import pages.living_settlor.individual._
import pages.trust_type.{HoldoverReliefYesNoPage, KindOfTrustPage, SetUpByLivingSettlorYesNoPage}

trait PageGenerators {

  implicit lazy val arbitrarySettlorBusinessDetailsPage: Arbitrary[SettlorBusinessNamePage] =
    Arbitrary(SettlorBusinessNamePage(0))

  implicit lazy val arbitraryKindOfTrustPage: Arbitrary[KindOfTrustPage.type] =
    Arbitrary(KindOfTrustPage)

  implicit lazy val arbitraryHoldoverReliefYesNoPage: Arbitrary[HoldoverReliefYesNoPage.type] =
    Arbitrary(HoldoverReliefYesNoPage)

  implicit lazy val arbitraryAddASettlorPage: Arbitrary[AddASettlorPage.type] =
    Arbitrary(AddASettlorPage)

  implicit lazy val arbitrarySettlorIndividualPassportYesNoPage: Arbitrary[SettlorIndividualPassportYesNoPage] =
    Arbitrary(SettlorIndividualPassportYesNoPage(0))

  implicit lazy val arbitrarySettlorIndividualPassportPage: Arbitrary[SettlorIndividualPassportPage] =
    Arbitrary(SettlorIndividualPassportPage(0))

  implicit lazy val arbitrarySettlorIndividualIDCardYesNoPage: Arbitrary[SettlorIndividualIDCardYesNoPage] =
    Arbitrary(SettlorIndividualIDCardYesNoPage(0))

  implicit lazy val arbitrarySettlorIndividualIDCardPage: Arbitrary[SettlorIndividualIDCardPage] =
    Arbitrary(SettlorIndividualIDCardPage(0))

  implicit lazy val arbitrarySettlorIndividualAddressUKYesNoPage: Arbitrary[SettlorAddressUKYesNoPage] =
    Arbitrary(SettlorAddressUKYesNoPage(0))

  implicit lazy val arbitrarySettlorIndividualAddressUKPage: Arbitrary[SettlorAddressUKPage] =
    Arbitrary(SettlorAddressUKPage(0))

  implicit lazy val arbitrarySettlorIndividualAddressInternationalPage: Arbitrary[SettlorAddressInternationalPage] =
    Arbitrary(SettlorAddressInternationalPage(0))

  implicit lazy val arbitrarySettlorIndividualNINOYesNoPage: Arbitrary[SettlorIndividualNINOYesNoPage] =
    Arbitrary(SettlorIndividualNINOYesNoPage(0))

  implicit lazy val arbitrarySettlorIndividualNINOPage: Arbitrary[SettlorIndividualNINOPage] =
    Arbitrary(SettlorIndividualNINOPage(0))

  implicit lazy val arbitrarySettlorIndividualAddressYesNoPage: Arbitrary[SettlorAddressYesNoPage] =
    Arbitrary(SettlorAddressYesNoPage(0))

  implicit lazy val arbitrarySettlorIndividualDateOfBirthPage: Arbitrary[SettlorIndividualDateOfBirthPage] =
    Arbitrary(SettlorIndividualDateOfBirthPage(0))

  implicit lazy val arbitrarySettlorIndividualDateOfBirthYesNoPage: Arbitrary[SettlorIndividualDateOfBirthYesNoPage] =
    Arbitrary(SettlorIndividualDateOfBirthYesNoPage(0))

  implicit lazy val arbitrarySettlorIndividualNamePage: Arbitrary[SettlorIndividualNamePage] =
    Arbitrary(SettlorIndividualNamePage(0))

  implicit lazy val arbitrarySettlorIndividualOrBusinessPage: Arbitrary[SettlorIndividualOrBusinessPage] =
    Arbitrary(SettlorIndividualOrBusinessPage(0))

  implicit lazy val arbitraryWasSettlorsAddressUKYesNoPage: Arbitrary[WasSettlorsAddressUKYesNoPage.type] =
    Arbitrary(WasSettlorsAddressUKYesNoPage)

  implicit lazy val arbitrarySetUpByLivingSettlorYesNoPage: Arbitrary[SetUpByLivingSettlorYesNoPage.type] =
    Arbitrary(SetUpByLivingSettlorYesNoPage)

  implicit lazy val arbitrarySettlorsUKAddressPage: Arbitrary[SettlorsUKAddressPage.type] =
    Arbitrary(SettlorsUKAddressPage)

  implicit lazy val arbitrarySettlorsNINoYesNoPage: Arbitrary[SettlorsNationalInsuranceYesNoPage.type] =
    Arbitrary(SettlorsNationalInsuranceYesNoPage)

  implicit lazy val arbitrarySettlorsNamePage: Arbitrary[SettlorsNamePage.type] =
    Arbitrary(SettlorsNamePage)

  implicit lazy val arbitrarySettlorsLastKnownAddressYesNoPage: Arbitrary[SettlorsLastKnownAddressYesNoPage.type] =
    Arbitrary(SettlorsLastKnownAddressYesNoPage)

  implicit lazy val arbitrarySettlorsInternationalAddressPage: Arbitrary[SettlorsInternationalAddressPage.type] =
    Arbitrary(SettlorsInternationalAddressPage)

  implicit lazy val arbitrarySettlorsDateOfBirthPage: Arbitrary[SettlorsDateOfBirthPage.type] =
    Arbitrary(SettlorsDateOfBirthPage)

  implicit lazy val arbitrarySettlorNationalInsuranceNumberPage: Arbitrary[SettlorNationalInsuranceNumberPage.type] =
    Arbitrary(SettlorNationalInsuranceNumberPage)

  implicit lazy val arbitrarySettlorDateOfDeathYesNoPage: Arbitrary[SettlorDateOfDeathYesNoPage.type] =
    Arbitrary(SettlorDateOfDeathYesNoPage)

  implicit lazy val arbitrarySettlorDateOfDeathPage: Arbitrary[SettlorDateOfDeathPage.type] =
    Arbitrary(SettlorDateOfDeathPage)

  implicit lazy val arbitrarySettlorDateOfBirthYesNoPage: Arbitrary[SettlorDateOfBirthYesNoPage.type] =
    Arbitrary(SettlorDateOfBirthYesNoPage)

}
