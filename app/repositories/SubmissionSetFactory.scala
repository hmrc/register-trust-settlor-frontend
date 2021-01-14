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

import javax.inject.Inject
import mapping.{DeceasedSettlorMapper, SettlorsMapper, TrustDetailsMapper}
import models.pages.Status._
import models.pages.Status
import models.{RegistrationSubmission, UserAnswers}
import pages.RegistrationProgress
import play.api.i18n.Messages
import play.api.libs.json.Json
import utils.{CheckAnswersFormatters, CheckYourAnswersHelper}
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class SubmissionSetFactory @Inject()(registrationProgress: RegistrationProgress,
                                     checkAnswersFormatters: CheckAnswersFormatters,
                                     settlorsMapper: SettlorsMapper,
                                     countryOptions: CountryOptions,
                                     deceasedSettlorMapper: DeceasedSettlorMapper,
                                     trustDetailsMapper: TrustDetailsMapper
                                    ) {

  def createFrom(userAnswers: UserAnswers)(implicit messages: Messages): RegistrationSubmission.DataSet = {
    val status = registrationProgress.settlorsStatus(userAnswers)
    val registrationPieces = mappedDataIfCompleted(userAnswers, status)

    RegistrationSubmission.DataSet(
      Json.toJson(userAnswers),
      status,
      registrationPieces,
      answerSectionsIfCompleted(userAnswers, status)
    )
  }

  private def mappedDataIfCompleted(userAnswers: UserAnswers, status: Option[Status]) = {

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

    val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, userAnswers.draftId, false)

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

  private def convertForSubmission(row: AnswerRow): RegistrationSubmission.AnswerRow = {
    RegistrationSubmission.AnswerRow(row.label, row.answer.toString, row.labelArg)
  }

  private def convertForSubmission(section: AnswerSection): RegistrationSubmission.AnswerSection = {
    RegistrationSubmission.AnswerSection(section.headingKey, section.rows.map(convertForSubmission), section.sectionKey)
  }
}
