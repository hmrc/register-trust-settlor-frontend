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

package pages

import javax.inject.Inject
import models.pages.Status.{Completed, InProgress}
import models.UserAnswers
import models.pages.{AddASettlor, Status}
import pages.living_settlor.trust_type.SetUpInAdditionToWillTrustYesNoPage
import sections.LivingSettlors

class RegistrationProgress @Inject()() {

  private def determineStatus(complete: Boolean): Option[Status] = {
    if (complete) {
      Some(Completed)
    } else {
      Some(InProgress)
    }
  }

  def settlorsStatus(userAnswers: UserAnswers): Option[Status] = {
    val setUpAfterSettlorDied = userAnswers.get(SetUpAfterSettlorDiedYesNoPage)
    val inAdditionToWillTrust = userAnswers.get(SetUpInAdditionToWillTrustYesNoPage).getOrElse(false)

    def isDeceasedSettlorComplete: Option[Status] = {
      val deceasedCompleted = userAnswers.get(DeceasedSettlorStatus)
      val isComplete = deceasedCompleted.contains(Completed)
      determineStatus(isComplete)
    }

    setUpAfterSettlorDied match {
      case None => None
      case Some(setupAfterDeceased) =>
        if (setupAfterDeceased) {isDeceasedSettlorComplete}
        else {
          userAnswers.get(LivingSettlors).getOrElse(Nil) match {
            case Nil =>
              if (!setupAfterDeceased && !inAdditionToWillTrust) {Some(Status.InProgress)}
              else { determineStatus(true) }
            case living =>
              val noMoreToAdd = userAnswers.get(AddASettlorPage).contains(AddASettlor.NoComplete)
              val isComplete = !living.exists(_.status == InProgress)
              determineStatus(isComplete && noMoreToAdd)
          }
        }
    }
  }

  def isSettlorsComplete(userAnswers: UserAnswers): Boolean = {
      settlorsStatus(userAnswers).contains(Completed)
  }

}
