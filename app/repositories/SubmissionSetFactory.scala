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

package repositories

import mapping.{DeceasedSettlorMapper, SettlorsMapper, TrustDetailsMapper}
import models.{RegistrationSubmission, UserAnswers}
import play.api.i18n.Messages
import play.api.libs.json.{JsValue, Json}
import utils.CheckYourAnswersHelper
import utils.print.PrintHelpers
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class SubmissionSetFactory @Inject()(settlorsMapper: SettlorsMapper,
                                     deceasedSettlorMapper: DeceasedSettlorMapper,
                                     trustDetailsMapper: TrustDetailsMapper,
                                     printHelpers: PrintHelpers) {

  private def mappedPiece(path: String, json: JsValue) =
    List(RegistrationSubmission.MappedPiece(path, json))

  def createFrom(userAnswers: UserAnswers)(implicit messages: Messages): RegistrationSubmission.DataSet = {
    RegistrationSubmission.DataSet(
      data = Json.toJson(userAnswers),
      registrationPieces = mappedDataIfCompleted(userAnswers),
      answerSections = answerSectionsIfCompleted(userAnswers)
    )
  }

  private def mappedDataIfCompleted(userAnswers: UserAnswers): List[RegistrationSubmission.MappedPiece] = {

    val tdMappedPiece = trustDetailsMapper.build(userAnswers)
      .map(td => mappedPiece("trust/details/", Json.toJson(td)))
      .getOrElse(List.empty)

    val sMappedPiece = (
        settlorsMapper.build(userAnswers),
        deceasedSettlorMapper.build(userAnswers)
      ) match {
      case (None, Some(ds)) =>
        mappedPiece("trust/entities/deceased", Json.toJson(ds))
      case (Some(s), None) =>
        mappedPiece("trust/entities/settlors", Json.toJson(s))
      case _ =>
        List.empty
    }

    tdMappedPiece ++ sMappedPiece
  }

  def answerSectionsIfCompleted(userAnswers: UserAnswers)
                               (implicit messages: Messages): Seq[RegistrationSubmission.AnswerSection] = {
    val cyaHelper = new CheckYourAnswersHelper(printHelpers)(userAnswers, userAnswers.draftId)

    val settlorRows = settlorsMapper.build(userAnswers).map { _ =>
      cyaHelper.livingSettlors
    }.getOrElse(Nil)

    val deceasedRows = deceasedSettlorMapper.build(userAnswers).map { _ =>
      cyaHelper.deceasedSettlor
    }.getOrElse(Nil)

    (deceasedRows ++ settlorRows).map(convertForSubmission)
  }

  private def convertForSubmission(section: AnswerSection): RegistrationSubmission.AnswerSection = {
    RegistrationSubmission.AnswerSection(
      headingKey = section.headingKey,
      rows = section.rows.map(convertForSubmission),
      sectionKey = section.sectionKey,
      headingArgs = section.headingArgs.map(_.toString)
    )
  }

  private def convertForSubmission(row: AnswerRow): RegistrationSubmission.AnswerRow = {
    RegistrationSubmission.AnswerRow(row.label, row.answer.toString, row.labelArg)
  }
}
