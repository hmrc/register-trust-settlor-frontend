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

package pages.living_settlor

import models.UserAnswers
import models.pages.IndividualOrBusiness
import models.pages.IndividualOrBusiness.{Business, Individual}
import pages.QuestionPage
import pages.LivingSettlorStatus
import pages.living_settlor.business._
import pages.living_settlor.individual.{SettlorAddressInternationalPage, SettlorAddressUKPage, SettlorAddressUKYesNoPage, SettlorAddressYesNoPage, SettlorIndividualDateOfBirthPage, SettlorIndividualDateOfBirthYesNoPage, SettlorIndividualIDCardPage, SettlorIndividualIDCardYesNoPage, SettlorIndividualNINOPage, SettlorIndividualNINOYesNoPage, SettlorIndividualNamePage, SettlorIndividualPassportPage, SettlorIndividualPassportYesNoPage}
import play.api.libs.json.JsPath
import sections.LivingSettlors

import scala.util.Try

final case class SettlorIndividualOrBusinessPage(index : Int) extends QuestionPage[IndividualOrBusiness] {

  override def path: JsPath = LivingSettlors.path \ index \ toString

  override def toString: String = "individualOrBusiness"

  override def cleanup(value: Option[IndividualOrBusiness], userAnswers: UserAnswers): Try[UserAnswers] = {
    value match {
      case Some(Individual) =>
        userAnswers.remove(SettlorBusinessNamePage(index))
          .flatMap(_.remove(SettlorBusinessUtrYesNoPage(index)))
          .flatMap(_.remove(SettlorBusinessUtrPage(index)))
          .flatMap(_.remove(SettlorBusinessAddressYesNoPage(index)))
          .flatMap(_.remove(SettlorBusinessAddressUKYesNoPage(index)))
          .flatMap(_.remove(SettlorBusinessAddressUKPage(index)))
          .flatMap(_.remove(SettlorBusinessAddressInternationalPage(index)))
          .flatMap(_.remove(SettlorBusinessTypePage(index)))
          .flatMap(_.remove(SettlorBusinessTimeYesNoPage(index)))
          .flatMap(_.remove(LivingSettlorStatus(index)))
      case Some(Business) =>
        userAnswers.remove(SettlorIndividualNamePage(index))
          .flatMap(_.remove(SettlorIndividualDateOfBirthYesNoPage(index)))
          .flatMap(_.remove(SettlorIndividualDateOfBirthPage(index)))
          .flatMap(_.remove(SettlorIndividualNINOYesNoPage(index)))
          .flatMap(_.remove(SettlorIndividualNINOPage(index)))
          .flatMap(_.remove(SettlorAddressYesNoPage(index)))
          .flatMap(_.remove(SettlorAddressUKYesNoPage(index)))
          .flatMap(_.remove(SettlorAddressUKPage(index)))
          .flatMap(_.remove(SettlorAddressInternationalPage(index)))
          .flatMap(_.remove(SettlorIndividualPassportYesNoPage(index)))
          .flatMap(_.remove(SettlorIndividualPassportPage(index)))
          .flatMap(_.remove(SettlorIndividualIDCardYesNoPage(index)))
          .flatMap(_.remove(SettlorIndividualIDCardPage(index)))
          .flatMap(_.remove(LivingSettlorStatus(index)))
      case _ =>
        super.cleanup(value, userAnswers)
    }
  }
}
