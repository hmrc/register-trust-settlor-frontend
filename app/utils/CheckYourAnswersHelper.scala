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

package utils

import models.UserAnswers
import play.api.i18n.Messages
import sections.{DeceasedSettlor, LivingSettlors}
import utils.print.PrintHelpers
import viewmodels._

import javax.inject.Inject

class CheckYourAnswersHelper @Inject()(printHelpers: PrintHelpers)
                                      (userAnswers: UserAnswers,
                                       draftId: String)
                                      (implicit messages: Messages) {

  def deceasedSettlor: Option[Seq[AnswerSection]] = {

    userAnswers.get(DeceasedSettlor) match {
      case Some(value) => value match {
        case x: SettlorDeceasedViewModel =>
          Some(Seq(printHelpers.deceasedSettlorSection(userAnswers, x.name, draftId)))
        case _ =>
          None
      }
      case _ =>
        None
    }
  }

  def livingSettlors: Option[Seq[AnswerSection]] = {

    for {
      livingSettlors <- userAnswers.get(LivingSettlors)
      indexed = livingSettlors.zipWithIndex
    } yield indexed.map {
      case (settlor, index) =>

        val questions: Seq[AnswerRow] = settlor match {
          case x: SettlorIndividualViewModel => printHelpers.livingSettlorRows(userAnswers, x.name.getOrElse(""), index, draftId)
          case x: SettlorBusinessViewModel => printHelpers.businessSettlorRows(userAnswers, x.name.getOrElse(""), index, draftId)
          case _ => Nil
        }

        val sectionKey = if (index == 0) Some(messages("answerPage.section.settlors.heading")) else None

        AnswerSection(
          headingKey = Some(messages("answerPage.section.settlor.subheading", index + 1)),
          questions,
          sectionKey = sectionKey
        )
    }
  }

}
