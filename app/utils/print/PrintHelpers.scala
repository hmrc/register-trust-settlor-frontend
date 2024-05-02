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

package utils.print

import models.UserAnswers
import play.api.i18n.Messages
import viewmodels.AnswerSection

import javax.inject.Inject

class PrintHelpers @Inject() (
  deceasedSettlorPrintHelper: DeceasedSettlorPrintHelper,
  livingSettlorPrintHelper: LivingSettlorPrintHelper,
  businessSettlorPrintHelper: BusinessSettlorPrintHelper
) {

  def deceasedSettlorSection(userAnswers: UserAnswers, name: String, draftId: String)(implicit
    messages: Messages
  ): AnswerSection =
    deceasedSettlorPrintHelper.printSection(userAnswers, name, draftId)

  def livingSettlorSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)(implicit
    messages: Messages
  ): AnswerSection =
    livingSettlorPrintHelper.printSection(userAnswers, name, draftId, index)

  def businessSettlorSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)(implicit
    messages: Messages
  ): AnswerSection =
    businessSettlorPrintHelper.printSection(userAnswers, name, draftId, index)

}
