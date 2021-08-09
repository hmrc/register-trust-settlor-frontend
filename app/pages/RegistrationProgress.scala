/*
 * Copyright 2021 HM Revenue & Customs
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

import models.UserAnswers
import models.pages.Status.{Completed, InProgress}
import models.pages.{AddASettlor, Status}
import pages.trust_type.{SetUpAfterSettlorDiedYesNoPage, SetUpInAdditionToWillTrustYesNoPage}
import sections.LivingSettlors

import javax.inject.Inject

class RegistrationProgress @Inject()() {

  def settlorsStatus(userAnswers: UserAnswers): Option[Status] = {

    val isDeceasedSettlorComplete: Boolean = userAnswers.get(DeceasedSettlorStatus).contains(Completed)

    userAnswers.get(SetUpAfterSettlorDiedYesNoPage) match {
      case Some(setUpAfterSettlorDied) =>
        if (setUpAfterSettlorDied) {
          determineStatus(isDeceasedSettlorComplete)
        } else {
          userAnswers.get(LivingSettlors).getOrElse(Nil) match {
            case Nil =>
              val inAdditionToWillTrust = userAnswers.get(SetUpInAdditionToWillTrustYesNoPage).getOrElse(false)
              if (!inAdditionToWillTrust) {
                Some(Status.InProgress)
              } else {
                determineStatus(isDeceasedSettlorComplete)
              }
            case living =>
              val noMoreToAdd = userAnswers.get(AddASettlorPage).contains(AddASettlor.NoComplete)
              val isComplete = !living.exists(_.status == InProgress)
              determineStatus(isComplete && noMoreToAdd)
          }
        }
      case _ =>
        None
    }
  }

  private def determineStatus(complete: Boolean): Option[Status] = {
    if (complete) {
      Some(Completed)
    } else {
      Some(InProgress)
    }
  }

}
