/*
 * Copyright 2026 HM Revenue & Customs
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

import config.FrontendAppConfig
import connectors.SubmissionDraftConnector
import models.pages.KindOfTrust.Employees
import models.{RolesInCompanies, TaskStatus, UserAnswers}
import pages.trust_type.KindOfTrustPage
import play.api.Logging
import play.api.libs.json.JsArray
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class DraftRegistrationService @Inject() (
  config: FrontendAppConfig,
  submissionDraftConnector: SubmissionDraftConnector,
  trustsStoreService: TrustsStoreService
)(implicit ec: ExecutionContext)
    extends Logging {

  def amendBeneficiariesState(draftId: String, userAnswers: UserAnswers)(implicit hc: HeaderCarrier): Future[Unit] =
    if (userAnswers.get(KindOfTrustPage).contains(Employees)) {

      logger.info(s"[DraftRegistrationService] trust is Employee related")

      submissionDraftConnector.allIndividualBeneficiariesHaveRoleInCompany(draftId) flatMap {
        case RolesInCompanies.AllRolesAnswered | RolesInCompanies.NoIndividualBeneficiaries =>
          logger.info(s"[DraftRegistrationService] trust has all director/employee roles answered or no individuals")
          Future.successful(())
        case RolesInCompanies.NotAllRolesAnswered                                           =>
          logger.info(
            s"[DraftRegistrationService] trust has beneficiaries with not all director/employee roles answered"
          )
          trustsStoreService.updateBeneficiaryTaskStatus(draftId, TaskStatus.InProgress).map(_ => ())
        case RolesInCompanies.CouldNotDetermine                                             =>
          logger.info(s"[DraftRegistrationService] could not determine the roles for individual beneficiaries")
          Future.successful(())
      }
    } else {
      removeBeneficiaryRoleInCompanyAnswers(draftId).map(_ => ())
    }

  def removeBeneficiaryRoleInCompanyAnswers(draftId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    submissionDraftConnector.removeRoleInCompanyAnswers(draftId)

  def removeDeceasedSettlorMappedPiece(draftId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    submissionDraftConnector.removeDeceasedSettlorMappedPiece(draftId)

  def removeLivingSettlorsMappedPiece(draftId: String)(implicit hc: HeaderCarrier): Future[HttpResponse] =
    submissionDraftConnector.removeLivingSettlorsMappedPiece(draftId)

  def retrieveTrusteeNinos(draftId: String)(implicit hc: HeaderCarrier): Future[collection.IndexedSeq[String]] =
    submissionDraftConnector.getDraftSection(draftId, config.repositoryKeyTrustees).map { response =>
      response.data
        .\("data")
        .\("trustees")
        .asOpt[JsArray]
        .getOrElse(JsArray())
        .value
        .map(_.\("nino").asOpt[String])
        .filter(_.isDefined)
        .map(_.get)

    }

  def retrieveBeneficiaryNinos(draftId: String)(implicit hc: HeaderCarrier): Future[collection.IndexedSeq[String]] =
    submissionDraftConnector.getDraftSection(draftId, config.repositoryKeyBeneficiaries).map { response =>
      response.data
        .\("data")
        .\("beneficiaries")
        .\("individualBeneficiaries")
        .asOpt[JsArray]
        .getOrElse(JsArray())
        .value
        .map(_.\("nationalInsuranceNumber").asOpt[String])
        .filter(_.isDefined)
        .map(_.get)
    }

  def retrieveProtectorNinos(draftId: String)(implicit hc: HeaderCarrier): Future[collection.IndexedSeq[String]] =
    submissionDraftConnector.getDraftSection(draftId, config.repositoryKeyProtectors).map { response =>
      response.data
        .\("data")
        .\("protectors")
        .\("individualProtectors")
        .asOpt[JsArray]
        .getOrElse(JsArray())
        .value
        .map(_.\("nationalInsuranceNumber").asOpt[String])
        .filter(_.isDefined)
        .map(_.get)
    }

}
