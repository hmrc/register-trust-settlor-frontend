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

package pages

import models.UserAnswers
import models.pages.Status.{Completed, InProgress}
import models.pages.{AddASettlor, Status}
import pages.trust_type.{SetUpByLivingSettlorYesNoPage, SetUpInAdditionToWillTrustYesNoPage}
import sections.LivingSettlors
import viewmodels.SettlorViewModel

import javax.inject.Inject

class RegistrationProgress @Inject()() {

  def settlorsStatus(userAnswers: UserAnswers): Option[Status] = {

    val deceasedStatus = determineStatus(
      userAnswers.get(DeceasedSettlorStatus).contains(Completed)
    )

    val setupByLivingSettlor = userAnswers.get(SetUpByLivingSettlorYesNoPage)

    setupByLivingSettlor flatMap { setupByLivingSettlor =>
      if (setupByLivingSettlor) {
        statusForNonWillTrust(userAnswers, deceasedStatus)
      } else {
        deceasedStatus
      }
    }
  }

  private def statusForNonWillTrust(userAnswers: UserAnswers,
                                    deceasedStatus: Option[Status]
                                   ): Option[Status] = {
    userAnswers
      .get(LivingSettlors)
      .getOrElse(Nil) match {
        case Nil =>
          statusForInAdditionToWill(userAnswers, deceasedStatus)
        case settlors =>
          statusForLivingSettlors(userAnswers, settlors)
    }
  }

  private def statusForLivingSettlors(userAnswers: UserAnswers,
                                       living: List[SettlorViewModel]
                                      ): Option[Status] = {
    val noMoreToAdd = userAnswers
      .get(AddASettlorPage)
      .contains(AddASettlor.NoComplete)

    val isComplete = !living.exists(_.status == InProgress)
    determineStatus(isComplete && noMoreToAdd)
  }

  private def statusForInAdditionToWill(userAnswers: UserAnswers,
                                       deceasedStatus: Option[Status]
                                      ): Option[Status] = {
    val inAdditionToWillTrust =
      userAnswers
        .get(SetUpInAdditionToWillTrustYesNoPage)
        .getOrElse(false)

    if (!inAdditionToWillTrust) {
      Some(Status.InProgress)
    } else {
      deceasedStatus
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
