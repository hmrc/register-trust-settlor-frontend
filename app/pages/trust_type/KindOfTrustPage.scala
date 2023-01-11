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

package pages.trust_type

import models.UserAnswers
import models.pages.KindOfTrust
import models.pages.KindOfTrust._
import pages.QuestionPage
import pages.living_settlor.business.{SettlorBusinessTimeYesNoPage, SettlorBusinessTypePage}
import play.api.libs.json.{JsArray, JsPath, JsSuccess, __}
import sections.{DeceasedSettlor, Settlors}

import scala.util.{Success, Try}

case object KindOfTrustPage extends QuestionPage[KindOfTrust] {

  override def path: JsPath = Settlors.path \ toString

  override def toString: String = "kindOfTrust"

  override def cleanup(value: Option[KindOfTrust], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(Deed) =>
        userAnswers.remove(HoldoverReliefYesNoPage)
          .flatMap(_.remove(EfrbsYesNoPage))
          .flatMap(_.remove(EfrbsStartDatePage))
          .flatMap(removeCompanyTypeAndTimeAnswers)
      case Some(Intervivos) =>
        userAnswers.remove(SetUpInAdditionToWillTrustYesNoPage)
          .flatMap(_.remove(HowDeedOfVariationCreatedPage))
          .flatMap(_.remove(EfrbsYesNoPage))
          .flatMap(_.remove(EfrbsStartDatePage))
          .flatMap(removeCompanyTypeAndTimeAnswers)
          .flatMap(_.remove(DeceasedSettlor))
      case Some(FlatManagement) | Some(HeritageMaintenanceFund) =>
        userAnswers.remove(SetUpInAdditionToWillTrustYesNoPage)
          .flatMap(_.remove(HowDeedOfVariationCreatedPage))
          .flatMap(_.remove(HoldoverReliefYesNoPage))
          .flatMap(_.remove(EfrbsYesNoPage))
          .flatMap(_.remove(EfrbsStartDatePage))
          .flatMap(removeCompanyTypeAndTimeAnswers)
          .flatMap(_.remove(DeceasedSettlor))
      case Some(Employees) =>
        userAnswers.remove(SetUpInAdditionToWillTrustYesNoPage)
          .flatMap(_.remove(HowDeedOfVariationCreatedPage))
          .flatMap(_.remove(HoldoverReliefYesNoPage))
          .flatMap(_.remove(DeceasedSettlor))
      case _ =>
        super.cleanup(value, userAnswers)
    }
  }

  private def removeCompanyTypeAndTimeAnswers(userAnswers: UserAnswers): Try[UserAnswers] = {

    val numberOfLivingSettlorsCompleteOrInProgress = userAnswers.data.transform((__ \ 'settlors \ 'living).json.pick) match {
      case JsSuccess(value, _) => value match {
        case JsArray(value) => value.size
        case _ => 0
      }
      case _ => 0
    }

    (0 until numberOfLivingSettlorsCompleteOrInProgress).foldLeft[Try[UserAnswers]](Success(userAnswers))((ua, index) => {
      ua
        .flatMap(_.remove(SettlorBusinessTypePage(index)))
        .flatMap(_.remove(SettlorBusinessTimeYesNoPage(index)))
    })
  }
}
