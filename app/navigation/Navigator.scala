/*
 * Copyright 2022 HM Revenue & Customs
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

package navigation

import pages._
import models._
import play.api.mvc.Call

trait Navigator {

  protected def route(draftId: String): PartialFunction[Page, UserAnswers => Call]

  def nextPage(page: Page, draftId: String): UserAnswers => Call

  def yesNoNav(fromPage: QuestionPage[Boolean], yesCall: => Call, noCall: => Call)(answers: UserAnswers): Call = {
    answers.get(fromPage)
      .map(if (_) yesCall else noCall)
      .getOrElse(controllers.routes.SessionExpiredController.onPageLoad())
  }
}
