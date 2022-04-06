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

package utils.print

import models.UserAnswers
import play.api.i18n.Messages
import viewmodels.{AnswerRow, AnswerSection}

abstract class SettlorPrintHelper(trustTypePrintHelper: TrustTypePrintHelper,
                                  answerRowConverter: AnswerRowConverter) {

  def headingKey: Option[String] = Some("answerPage.section.settlor.subheading")

  def sectionKey(index: Int): Option[String] = if (index == 0) Some("answerPage.section.settlors.heading") else None

  def printSection(userAnswers: UserAnswers, name: String, draftId: String, index: Int = 0)
                  (implicit messages: Messages): AnswerSection = {
    answerSection(userAnswers, name, index, draftId)(headingKey, sectionKey(index))
  }

  def checkDetailsSection(userAnswers: UserAnswers, name: String, draftId: String, index: Int = 0)
                         (implicit messages: Messages): AnswerSection = {
    answerSection(userAnswers, name, index, draftId)(None, None)
  }

  private def answerSection(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                           (heading: Option[String], section: Option[String])
                           (implicit messages: Messages): AnswerSection = {
    AnswerSection(
      headingKey = heading,
      rows = answerRows(userAnswers, name, index, draftId),
      sectionKey = section,
      headingArgs = Seq(index + 1)
    )
  }

  private def answerRows(userAnswers: UserAnswers, name: String, index: Int, draftId: String)
                        (implicit messages: Messages): Seq[AnswerRow] = {

    val bound = answerRowConverter.bind(userAnswers, name)

    trustTypePrintHelper.answerRows(userAnswers, draftId) ++
      answerRows(index, draftId)(bound).flatten
  }

  def answerRows(index: Int, draftId: String)
                (bound: AnswerRowConverter#Bound)
                (implicit messages: Messages): Seq[Option[AnswerRow]]

}
