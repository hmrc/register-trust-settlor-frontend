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

package pages.trust_type

import models.UserAnswers
import pages.QuestionPage
import play.api.libs.json.JsPath
import sections.{DeceasedSettlor, LivingSettlors, Settlors}

import scala.util.Try

case object SetUpInAdditionToWillTrustYesNoPage extends QuestionPage[Boolean] {

  override def path: JsPath = Settlors.path \ toString

  override def toString: String = "setUpInAdditionToWillTrustYesNo"

  override def cleanup(value: Option[Boolean], userAnswers: UserAnswers): Try[UserAnswers] =
    value match {
      case Some(false) =>
        userAnswers.remove(DeceasedSettlor)
      case Some(true)  =>
        userAnswers
          .remove(HowDeedOfVariationCreatedPage)
          .flatMap(_.remove(LivingSettlors))
      case _           => super.cleanup(value, userAnswers)
    }

}
