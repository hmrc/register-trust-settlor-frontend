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

package pages.living_settlor.individual.mld5

import models.UserAnswers
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.LivingSettlors
import utils.Constants.GB

import scala.util.Try

case class UkCountryOfNationalityYesNoPage(index: Int) extends QuestionPage[Boolean] {

  override def path: JsPath = LivingSettlors.path \ index \ toString

  override def toString: String = "ukCountryOfNationalityYesNo"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] = {
    val hasGbCountryOfNationality: Boolean = userAnswers.get(CountryOfNationalityPage(index)).contains(GB)
    value match {
      case Some(true)                               =>
        userAnswers.set(CountryOfNationalityPage(index), GB)
      case Some(false) if hasGbCountryOfNationality =>
        userAnswers.remove(CountryOfNationalityPage(index))
      case _                                        =>
        super.cleanup(value, userAnswers)
    }
  }

}
