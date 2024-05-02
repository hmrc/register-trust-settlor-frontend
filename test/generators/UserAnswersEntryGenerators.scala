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

package generators

import models.pages._
import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary.arbitrary
import pages.AddASettlorPage
import pages.deceased_settlor._
import pages.living_settlor._
import pages.living_settlor.business.SettlorBusinessNamePage
import pages.living_settlor.individual._
import pages.trust_type.{HoldoverReliefYesNoPage, KindOfTrustPage, SetUpByLivingSettlorYesNoPage}
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators with ModelGenerators {

  implicit lazy val arbitrarySettlorBusinessDetailsUserAnswersEntry: Arbitrary[(SettlorBusinessNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorBusinessNamePage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryHoldoverReliefYesNoUserAnswersEntry: Arbitrary[(HoldoverReliefYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[HoldoverReliefYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryKindOfTrustUserAnswersEntry: Arbitrary[(KindOfTrustPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[KindOfTrustPage.type]
        value <- arbitrary[KindOfTrust].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualPassportYesNoUserAnswersEntry
    : Arbitrary[(SettlorIndividualPassportYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualPassportYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualPassportUserAnswersEntry
    : Arbitrary[(SettlorIndividualPassportPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualPassportPage]
        value <- arbitrary[PassportOrIdCardDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualIDCardYesNoUserAnswersEntry
    : Arbitrary[(SettlorIndividualIDCardYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualIDCardYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualIDCardUserAnswersEntry
    : Arbitrary[(SettlorIndividualIDCardPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualIDCardPage]
        value <- arbitrary[PassportOrIdCardDetails].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualAddressUKYesNoUserAnswersEntry
    : Arbitrary[(SettlorAddressUKYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorAddressUKYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualAddressUKUserAnswersEntry: Arbitrary[(SettlorAddressUKPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorAddressUKPage]
        value <- arbitrary[UKAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualAddressInternationalUserAnswersEntry
    : Arbitrary[(SettlorAddressInternationalPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorAddressInternationalPage]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualNINOYesNoUserAnswersEntry
    : Arbitrary[(SettlorIndividualNINOYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualNINOYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualNINOUserAnswersEntry: Arbitrary[(SettlorIndividualNINOPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualNINOPage]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualAddressYesNoUserAnswersEntry
    : Arbitrary[(SettlorAddressYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorAddressYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualDateOfBirthUserAnswersEntry
    : Arbitrary[(SettlorIndividualDateOfBirthPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualDateOfBirthPage]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualDateOfBirthYesNoUserAnswersEntry
    : Arbitrary[(SettlorIndividualDateOfBirthYesNoPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualDateOfBirthYesNoPage]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualNameUserAnswersEntry: Arbitrary[(SettlorIndividualNamePage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualNamePage]
        value <- arbitrary[FullName].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorIndividualOrBusinessUserAnswersEntry
    : Arbitrary[(SettlorIndividualOrBusinessPage, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorIndividualOrBusinessPage]
        value <- arbitrary[IndividualOrBusiness].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryWasSettlorsAddressUKYesNoUserAnswersEntry
    : Arbitrary[(WasSettlorsAddressUKYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[WasSettlorsAddressUKYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySetUpByLivingSettlorRouteUserAnswersEntry
    : Arbitrary[(SetUpByLivingSettlorYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SetUpByLivingSettlorYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorsUKAddressUserAnswersEntry: Arbitrary[(SettlorsUKAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsUKAddressPage.type]
        value <- arbitrary[UKAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorsNINoYesNoUserAnswersEntry
    : Arbitrary[(SettlorsNationalInsuranceYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsNationalInsuranceYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorsNameUserAnswersEntry: Arbitrary[(SettlorsNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsNamePage.type]
        value <- arbitrary[FullName].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorsLastKnownAddressYesNoUserAnswersEntry
    : Arbitrary[(SettlorsLastKnownAddressYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsLastKnownAddressYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorsInternationalAddressUserAnswersEntry
    : Arbitrary[(SettlorsInternationalAddressPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsInternationalAddressPage.type]
        value <- arbitrary[InternationalAddress].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorsDateOfBirthUserAnswersEntry: Arbitrary[(SettlorsDateOfBirthPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorsDateOfBirthPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorNationalInsuranceNumberUserAnswersEntry
    : Arbitrary[(SettlorNationalInsuranceNumberPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorNationalInsuranceNumberPage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorDateOfDeathYesNoUserAnswersEntry
    : Arbitrary[(SettlorDateOfDeathYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorDateOfDeathYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorDateOfDeathUserAnswersEntry: Arbitrary[(SettlorDateOfDeathPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorDateOfDeathPage.type]
        value <- arbitrary[Int].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitrarySettlorDateOfBirthYesNoUserAnswersEntry
    : Arbitrary[(SettlorDateOfBirthYesNoPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[SettlorDateOfBirthYesNoPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryAddASettlorUserAnswersEntry: Arbitrary[(AddASettlorPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[AddASettlorPage.type]
        value <- arbitrary[AddASettlor].map(Json.toJson(_))
      } yield (page, value)
    }

}
