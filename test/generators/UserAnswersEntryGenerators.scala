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

import models.pages.*
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.AddASettlorPage
import pages.deceased_settlor.*
import pages.living_settlor.*
import pages.living_settlor.business.SettlorBusinessNamePage
import pages.living_settlor.individual.*
import pages.trust_type.{HoldoverReliefYesNoPage, KindOfTrustPage, SetUpByLivingSettlorYesNoPage}
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  given arbitrarySettlorBusinessDetailsUserAnswersEntry: Arbitrary[(SettlorBusinessNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorBusinessNamePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryHoldoverReliefYesNoUserAnswersEntry: Arbitrary[(HoldoverReliefYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HoldoverReliefYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryKindOfTrustUserAnswersEntry: Arbitrary[(KindOfTrustPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[KindOfTrustPage.type]
        value <- arbitrary[KindOfTrust].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualPassportYesNoUserAnswersEntry
    : Arbitrary[(SettlorIndividualPassportYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualPassportYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualPassportUserAnswersEntry: Arbitrary[(SettlorIndividualPassportPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualPassportPage]
        value <- arbitrary[PassportOrIdCardDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualIDCardYesNoUserAnswersEntry: Arbitrary[(SettlorIndividualIDCardYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualIDCardYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualIDCardUserAnswersEntry: Arbitrary[(SettlorIndividualIDCardPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualIDCardPage]
        value <- arbitrary[PassportOrIdCardDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualAddressUKYesNoUserAnswersEntry: Arbitrary[(SettlorAddressUKYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorAddressUKYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualAddressUKUserAnswersEntry: Arbitrary[(SettlorAddressUKPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorAddressUKPage]
        value <- arbitrary[UKAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualAddressInternationalUserAnswersEntry
    : Arbitrary[(SettlorAddressInternationalPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorAddressInternationalPage]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualNINOYesNoUserAnswersEntry: Arbitrary[(SettlorIndividualNINOYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualNINOYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualNINOUserAnswersEntry: Arbitrary[(SettlorIndividualNINOPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualNINOPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualAddressYesNoUserAnswersEntry: Arbitrary[(SettlorAddressYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorAddressYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualDateOfBirthUserAnswersEntry: Arbitrary[(SettlorIndividualDateOfBirthPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualDateOfBirthPage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualDateOfBirthYesNoUserAnswersEntry
    : Arbitrary[(SettlorIndividualDateOfBirthYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualDateOfBirthYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualNameUserAnswersEntry: Arbitrary[(SettlorIndividualNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualNamePage]
        value <- arbitrary[FullName].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorIndividualOrBusinessUserAnswersEntry: Arbitrary[(SettlorIndividualOrBusinessPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualOrBusinessPage]
        value <- arbitrary[IndividualOrBusiness].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryWasSettlorsAddressUKYesNoUserAnswersEntry: Arbitrary[(WasSettlorsAddressUKYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WasSettlorsAddressUKYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySetUpByLivingSettlorRouteUserAnswersEntry: Arbitrary[(SetUpByLivingSettlorYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SetUpByLivingSettlorYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorsUKAddressUserAnswersEntry: Arbitrary[(SettlorsUKAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsUKAddressPage.type]
        value <- arbitrary[UKAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorsNINoYesNoUserAnswersEntry: Arbitrary[(SettlorsNationalInsuranceYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsNationalInsuranceYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorsNameUserAnswersEntry: Arbitrary[(SettlorsNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsNamePage.type]
        value <- arbitrary[FullName].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorsLastKnownAddressYesNoUserAnswersEntry
    : Arbitrary[(SettlorsLastKnownAddressYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsLastKnownAddressYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorsInternationalAddressUserAnswersEntry
    : Arbitrary[(SettlorsInternationalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsInternationalAddressPage.type]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorsDateOfBirthUserAnswersEntry: Arbitrary[(SettlorsDateOfBirthPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsDateOfBirthPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorNationalInsuranceNumberUserAnswersEntry
    : Arbitrary[(SettlorNationalInsuranceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorNationalInsuranceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorDateOfDeathYesNoUserAnswersEntry: Arbitrary[(SettlorDateOfDeathYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorDateOfDeathYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorDateOfDeathUserAnswersEntry: Arbitrary[(SettlorDateOfDeathPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorDateOfDeathPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitrarySettlorDateOfBirthYesNoUserAnswersEntry: Arbitrary[(SettlorDateOfBirthYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorDateOfBirthYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  given arbitraryAddASettlorUserAnswersEntry: Arbitrary[(AddASettlorPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddASettlorPage.type]
        value <- arbitrary[AddASettlor].map(Json.toJson(_))
      } yield (page, value)
    }

}
