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

import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.TryValues
import pages._
import pages.deceased_settlor._
import pages.living_settlor._
import pages.living_settlor.business.SettlorBusinessNamePage
import pages.living_settlor.individual._
import pages.trust_type._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersGenerator extends TryValues {
  self: Generators =>

  import models._

  val generators: Seq[Gen[(QuestionPage[_], JsValue)]] =
    arbitrary[(KindOfTrustPage.type, JsValue)] ::
      arbitrary[(HoldoverReliefYesNoPage.type, JsValue)] ::
      arbitrary[(SettlorBusinessNamePage, JsValue)] ::
      arbitrary[(SettlorIndividualPassportYesNoPage, JsValue)] ::
      arbitrary[(SettlorIndividualPassportPage, JsValue)] ::
      arbitrary[(SettlorIndividualIDCardYesNoPage, JsValue)] ::
      arbitrary[(SettlorIndividualIDCardPage, JsValue)] ::
      arbitrary[(SettlorAddressUKYesNoPage, JsValue)] ::
      arbitrary[(SettlorAddressUKPage, JsValue)] ::
      arbitrary[(SettlorAddressInternationalPage, JsValue)] ::
      arbitrary[(SettlorIndividualNINOYesNoPage, JsValue)] ::
      arbitrary[(SettlorIndividualNINOPage, JsValue)] ::
      arbitrary[(SettlorAddressYesNoPage, JsValue)] ::
      arbitrary[(SettlorIndividualDateOfBirthPage, JsValue)] ::
      arbitrary[(SettlorIndividualDateOfBirthYesNoPage, JsValue)] ::
      arbitrary[(SettlorIndividualNamePage, JsValue)] ::
      arbitrary[(SettlorIndividualOrBusinessPage, JsValue)] ::
      arbitrary[(WasSettlorsAddressUKYesNoPage.type, JsValue)] ::
      arbitrary[(SetUpByLivingSettlorYesNoPage.type, JsValue)] ::
      arbitrary[(SettlorsUKAddressPage.type, JsValue)] ::
      arbitrary[(SettlorsNationalInsuranceYesNoPage.type, JsValue)] ::
      arbitrary[(SettlorsNamePage.type, JsValue)] ::
      arbitrary[(SettlorsLastKnownAddressYesNoPage.type, JsValue)] ::
      arbitrary[(SettlorsInternationalAddressPage.type, JsValue)] ::
      arbitrary[(SettlorsDateOfBirthPage.type, JsValue)] ::
      arbitrary[(SettlorNationalInsuranceNumberPage.type, JsValue)] ::
      arbitrary[(SettlorDateOfDeathYesNoPage.type, JsValue)] ::
      arbitrary[(SettlorDateOfDeathPage.type, JsValue)] ::
      arbitrary[(SettlorDateOfBirthYesNoPage.type, JsValue)] ::
      arbitrary[(AddASettlorPage.type, JsValue)] ::
      Nil

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] =
    Arbitrary {
      for {
        id         <- nonEmptyString
        data       <- generators match {
                        case Nil => Gen.const(Map[QuestionPage[_], JsValue]())
                        case _   => Gen.mapOf(oneOf(generators))
                      }
        internalId <- nonEmptyString
      } yield UserAnswers(
        draftId = id,
        data = data.foldLeft(Json.obj()) { case (obj, (path, value)) =>
          obj.setObject(path.path, value).get
        },
        internalAuthId = internalId
      )
    }
}
