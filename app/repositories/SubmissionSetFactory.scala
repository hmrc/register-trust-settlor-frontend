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

package repositories

import mapping.{DeceasedSettlorMapper, SettlorsMapper, TrustDetailsMapper}
import models.pages.Status
import models.pages.Status._
import models.{RegistrationSubmission, UserAnswers}
import pages.RegistrationProgress
import play.api.i18n.Messages
import play.api.libs.json.Json
import utils.CheckYourAnswersHelper
import utils.print.PrintHelpers
import viewmodels.{AnswerRow, AnswerSection}

import javax.inject.Inject

class SubmissionSetFactory @Inject()(registrationProgress: RegistrationProgress,
                                     settlorsMapper: SettlorsMapper,
                                     deceasedSettlorMapper: DeceasedSettlorMapper,
                                     trustDetailsMapper: TrustDetailsMapper,
                                     printHelpers: PrintHelpers) {

  def createFrom(userAnswers: UserAnswers)(implicit messages: Messages): RegistrationSubmission.DataSet = {
    val status = registrationProgress.settlorsStatus(userAnswers)

    RegistrationSubmission.DataSet(
      data = Json.toJson(userAnswers),
      status = status,
      registrationPieces = mappedDataIfCompleted(userAnswers, status),
      answerSections = answerSectionsIfCompleted(userAnswers, status)
    )
  }

  private def mappedDataIfCompleted(userAnswers: UserAnswers, status: Option[Status]): List[RegistrationSubmission.MappedPiece] = {

    if (status.contains(Completed)) {

      val trustDetailsMappedPiece = trustDetailsMapper.build(userAnswers) match {
        case Some(trustDetails) => List(RegistrationSubmission.MappedPiece("trust/details/", Json.toJson(trustDetails)))
        case _ => List.empty
      }

      val settlorsMappedPiece = (settlorsMapper.build(userAnswers), deceasedSettlorMapper.build(userAnswers)) match {
        case (_, Some(deceasedSettlor)) => List(RegistrationSubmission.MappedPiece("trust/entities/deceased", Json.toJson(deceasedSettlor)))
        case (Some(settlors), _)        => List(RegistrationSubmission.MappedPiece("trust/entities/settlors", Json.toJson(settlors)))
        case _                          => List.empty
      }

      trustDetailsMappedPiece ++ settlorsMappedPiece

    } else {
      List.empty
    }

  }

  def answerSectionsIfCompleted(userAnswers: UserAnswers, status: Option[Status])
                               (implicit messages: Messages): List[RegistrationSubmission.AnswerSection] = {

    val checkYourAnswersHelper = new CheckYourAnswersHelper(printHelpers)(userAnswers, userAnswers.draftId)

    if (status.contains(Status.Completed)) {

      ((settlorsMapper.build(userAnswers), deceasedSettlorMapper.build(userAnswers)) match {
        case (_, Some(_)) =>
          List(
            checkYourAnswersHelper.deceasedSettlor
          ).flatten.flatten
        case (Some(_), _) =>
          List(
            checkYourAnswersHelper.livingSettlors
          ).flatten.flatten
        case _ =>
          List.empty
      }) map convertForSubmission

    } else {
      List.empty
    }
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
