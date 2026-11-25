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
import pages.deceased_settlor.*
import pages.living_settlor.*
import pages.living_settlor.business.SettlorBusinessNamePage
import pages.living_settlor.individual.*
import pages.trust_type.{HoldoverReliefYesNoPage, KindOfTrustPage, SetUpByLivingSettlorYesNoPage}

trait PageGenerators {

  given arbitrarySettlorBusinessDetailsPage: Arbitrary[SettlorBusinessNamePage] =
    Arbitrary(SettlorBusinessNamePage(0))

  given arbitraryKindOfTrustPage: Arbitrary[KindOfTrustPage.type] =
    Arbitrary(KindOfTrustPage)

  given arbitraryHoldoverReliefYesNoPage: Arbitrary[HoldoverReliefYesNoPage.type] =
    Arbitrary(HoldoverReliefYesNoPage)

  given arbitraryAddASettlorPage: Arbitrary[AddASettlorPage.type] =
    Arbitrary(AddASettlorPage)

  given arbitrarySettlorIndividualPassportYesNoPage: Arbitrary[SettlorIndividualPassportYesNoPage] =
    Arbitrary(SettlorIndividualPassportYesNoPage(0))

  given arbitrarySettlorIndividualPassportPage: Arbitrary[SettlorIndividualPassportPage] =
    Arbitrary(SettlorIndividualPassportPage(0))

  given arbitrarySettlorIndividualIDCardYesNoPage: Arbitrary[SettlorIndividualIDCardYesNoPage] =
    Arbitrary(SettlorIndividualIDCardYesNoPage(0))

  given arbitrarySettlorIndividualIDCardPage: Arbitrary[SettlorIndividualIDCardPage] =
    Arbitrary(SettlorIndividualIDCardPage(0))

  given arbitrarySettlorIndividualAddressUKYesNoPage: Arbitrary[SettlorAddressUKYesNoPage] =
    Arbitrary(SettlorAddressUKYesNoPage(0))

  given arbitrarySettlorIndividualAddressUKPage: Arbitrary[SettlorAddressUKPage] =
    Arbitrary(SettlorAddressUKPage(0))

  given arbitrarySettlorIndividualAddressInternationalPage: Arbitrary[SettlorAddressInternationalPage] =
    Arbitrary(SettlorAddressInternationalPage(0))

  given arbitrarySettlorIndividualNINOYesNoPage: Arbitrary[SettlorIndividualNINOYesNoPage] =
    Arbitrary(SettlorIndividualNINOYesNoPage(0))

  given arbitrarySettlorIndividualNINOPage: Arbitrary[SettlorIndividualNINOPage] =
    Arbitrary(SettlorIndividualNINOPage(0))

  given arbitrarySettlorIndividualAddressYesNoPage: Arbitrary[SettlorAddressYesNoPage] =
    Arbitrary(SettlorAddressYesNoPage(0))

  given arbitrarySettlorIndividualDateOfBirthPage: Arbitrary[SettlorIndividualDateOfBirthPage] =
    Arbitrary(SettlorIndividualDateOfBirthPage(0))

  given arbitrarySettlorIndividualDateOfBirthYesNoPage: Arbitrary[SettlorIndividualDateOfBirthYesNoPage] =
    Arbitrary(SettlorIndividualDateOfBirthYesNoPage(0))

  given arbitrarySettlorIndividualNamePage: Arbitrary[SettlorIndividualNamePage] =
    Arbitrary(SettlorIndividualNamePage(0))

  given arbitrarySettlorIndividualOrBusinessPage: Arbitrary[SettlorIndividualOrBusinessPage] =
    Arbitrary(SettlorIndividualOrBusinessPage(0))

  given arbitraryWasSettlorsAddressUKYesNoPage: Arbitrary[WasSettlorsAddressUKYesNoPage.type] =
    Arbitrary(WasSettlorsAddressUKYesNoPage)

  given arbitrarySetUpByLivingSettlorYesNoPage: Arbitrary[SetUpByLivingSettlorYesNoPage.type] =
    Arbitrary(SetUpByLivingSettlorYesNoPage)

  given arbitrarySettlorsUKAddressPage: Arbitrary[SettlorsUKAddressPage.type] =
    Arbitrary(SettlorsUKAddressPage)

  given arbitrarySettlorsNINoYesNoPage: Arbitrary[SettlorsNationalInsuranceYesNoPage.type] =
    Arbitrary(SettlorsNationalInsuranceYesNoPage)

  given arbitrarySettlorsNamePage: Arbitrary[SettlorsNamePage.type] =
    Arbitrary(SettlorsNamePage)

  given arbitrarySettlorsLastKnownAddressYesNoPage: Arbitrary[SettlorsLastKnownAddressYesNoPage.type] =
    Arbitrary(SettlorsLastKnownAddressYesNoPage)

  given arbitrarySettlorsInternationalAddressPage: Arbitrary[SettlorsInternationalAddressPage.type] =
    Arbitrary(SettlorsInternationalAddressPage)

  given arbitrarySettlorsDateOfBirthPage: Arbitrary[SettlorsDateOfBirthPage.type] =
    Arbitrary(SettlorsDateOfBirthPage)

  given arbitrarySettlorNationalInsuranceNumberPage: Arbitrary[SettlorNationalInsuranceNumberPage.type] =
    Arbitrary(SettlorNationalInsuranceNumberPage)

  given arbitrarySettlorDateOfDeathYesNoPage: Arbitrary[SettlorDateOfDeathYesNoPage.type] =
    Arbitrary(SettlorDateOfDeathYesNoPage)

  given arbitrarySettlorDateOfDeathPage: Arbitrary[SettlorDateOfDeathPage.type] =
    Arbitrary(SettlorDateOfDeathPage)

  given arbitrarySettlorDateOfBirthYesNoPage: Arbitrary[SettlorDateOfBirthYesNoPage.type] =
    Arbitrary(SettlorDateOfBirthYesNoPage)

}
