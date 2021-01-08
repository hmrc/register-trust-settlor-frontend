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

package services

import connectors.SubmissionDraftConnector
import models.ReadOnlyUserAnswers
import models.pages.Status.InProgress
import pages.beneficiaries.RoleInCompanyPage
import repositories.RegistrationsRepository
import sections.beneficiaries.IndividualBeneficiaries
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DraftRegistrationService @Inject()(registrationsRepository: RegistrationsRepository,
                                         submissionDraftConnector: SubmissionDraftConnector)
                                        (implicit ec: ExecutionContext) {

  def setBeneficiaryStatus(draftId: String)(implicit hc: HeaderCarrier): Future[Boolean] =
    submissionDraftConnector.getDraftBeneficiaries(draftId: String) flatMap { response =>
      val answers = response.data.asOpt[ReadOnlyUserAnswers]

      val roleInCompanyQuestionRequiresAnswering: Boolean =
        answers match {
          case Some(userAnswers) =>
            userAnswers.get(IndividualBeneficiaries) match {
              case Some(individualBeneficiaries) =>
                individualBeneficiaries.zipWithIndex.exists { x =>
                  userAnswers.get(RoleInCompanyPage(x._2)).isEmpty
                }
              case _ =>
                false
            }
          case _ =>
            false
        }

      if (roleInCompanyQuestionRequiresAnswering) {
        registrationsRepository.getAllStatus(draftId) flatMap {
          allStatus =>
            registrationsRepository.setAllStatus(draftId, allStatus.copy(beneficiaries = Some(InProgress)))
        }
      } else {
        Future.successful(true)
      }
    }

  def removeRoleInCompanyAnswers(draftId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    submissionDraftConnector.removeRoleInCompanyAnswers(draftId)

  def removeDeceasedSettlorMappedPiece(draftId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    submissionDraftConnector.removeDeceasedSettlorMappedPiece(draftId)

  def removeLivingSettlorsMappedPiece(draftId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    submissionDraftConnector.removeLivingSettlorsMappedPiece(draftId)
    
}
