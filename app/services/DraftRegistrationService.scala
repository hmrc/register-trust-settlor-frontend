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

package services

import connectors.SubmissionDraftConnector
import models.pages.KindOfTrust.Employees
import models.{RolesInCompanies, TaskStatus, UserAnswers}
import pages.trust_type.KindOfTrustPage
import play.api.Logging
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DraftRegistrationService @Inject()(submissionDraftConnector: SubmissionDraftConnector,
                                         trustsStoreService: TrustsStoreService)
                                        (implicit ec: ExecutionContext) extends Logging {

  def amendBeneficiariesState(draftId: String, userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Unit] = {
    if (userAnswers.get(KindOfTrustPage).contains(Employees)) {

      logger.info(s"[DraftRegistrationService] trust is Employee related")

      submissionDraftConnector.allIndividualBeneficiariesHaveRoleInCompany(draftId) flatMap {
        case RolesInCompanies.AllRolesAnswered | RolesInCompanies.NoIndividualBeneficiaries =>
          logger.info(s"[DraftRegistrationService] trust has all director/employee roles answered or no individuals")
          Future.successful(())
        case RolesInCompanies.NotAllRolesAnswered =>
          logger.info(s"[DraftRegistrationService] trust has beneficiaries with not all director/employee roles answered")
          trustsStoreService.updateBeneficiaryTaskStatus(draftId, TaskStatus.InProgress).map(_ => ())
        case RolesInCompanies.CouldNotDetermine =>
          logger.info(s"[DraftRegistrationService] could not determine the roles for individual beneficiaries")
          Future.successful(())
      }
    } else {
      removeBeneficiaryRoleInCompanyAnswers(draftId).map(_ => ())
    }
  }

  def removeBeneficiaryRoleInCompanyAnswers(draftId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    submissionDraftConnector.removeRoleInCompanyAnswers(draftId)

  def removeDeceasedSettlorMappedPiece(draftId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    submissionDraftConnector.removeDeceasedSettlorMappedPiece(draftId)

  def removeLivingSettlorsMappedPiece(draftId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    submissionDraftConnector.removeLivingSettlorsMappedPiece(draftId)
    
}
