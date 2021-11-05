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

package connectors

import config.FrontendAppConfig
import models.RolesInCompanies.RolesInCompaniesAnswered
import models.{AllStatus, RegistrationSubmission, RolesInCompanies, SubmissionDraftData, SubmissionDraftResponse}
import play.api.http.Status.NOT_FOUND
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HttpReads.Implicits._
import uk.gov.hmrc.http.{HeaderCarrier, HttpClient, HttpResponse, UpstreamErrorResponse}

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SubmissionDraftConnector @Inject()(http: HttpClient, config: FrontendAppConfig) {

  private val submissionsBaseUrl = s"${config.trustsUrl}/trusts/register/submission-drafts"

  def setDraftSection(draftId: String, section: String, data: RegistrationSubmission.DataSet)
                     (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](s"$submissionsBaseUrl/$draftId/set/$section", Json.toJson(data))
  }

  def getDraftSection(draftId: String, section: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[SubmissionDraftResponse] = {
    http.GET[SubmissionDraftResponse](s"$submissionsBaseUrl/$draftId/$section")
  }

  def getTrustSetupDate(draftId: String)(implicit hc:HeaderCarrier, ec: ExecutionContext): Future[Option[LocalDate]] = {
    http.GET[HttpResponse](s"$submissionsBaseUrl/$draftId/when-trust-setup").map {
      response =>
        (response.json \ "startDate").asOpt[LocalDate]
    }.recover {
      case _ => None
    }
  }

  @deprecated("Status should be tracked in trusts-store", "05/11/2021")
  def getStatus(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[AllStatus] = {
    getDraftSection(draftId, "status").map {
      section => section.data.as[AllStatus]
    }
  }

  @deprecated("Status should be tracked in trusts-store", "05/11/2021")
  def setStatus(draftId: String, status: AllStatus)
               (implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val submissionDraftData = SubmissionDraftData(Json.toJson(status), None, None)
    http.POST[JsValue, HttpResponse](s"$submissionsBaseUrl/$draftId/status", Json.toJson(submissionDraftData))
  }


  def getIsTrustTaxable(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Boolean] = {
    http.GET[Boolean](s"$submissionsBaseUrl/$draftId/is-trust-taxable").recover {
      case _ => true
    }
  }

  def allIndividualBeneficiariesHaveRoleInCompany(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[RolesInCompaniesAnswered] = {
    http.GET[RolesInCompaniesAnswered](s"$submissionsBaseUrl/$draftId/beneficiaries")
  }

  def removeRoleInCompanyAnswers(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$submissionsBaseUrl/$draftId/set/beneficiaries/remove-role-in-company"
    http.POSTEmpty[HttpResponse](url)
  }

  def removeDeceasedSettlorMappedPiece(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$submissionsBaseUrl/$draftId/remove-mapped-piece/deceased-settlor"
    http.POSTEmpty[HttpResponse](url)
  }

  def removeLivingSettlorsMappedPiece(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[HttpResponse] = {
    val url: String = s"$submissionsBaseUrl/$draftId/remove-mapped-piece/living-settlors"
    http.POSTEmpty[HttpResponse](url)
  }

  def getTrustUtr(draftId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[String]] = {
    http.GET[String](s"$submissionsBaseUrl/$draftId/trust-utr")
      .map(Some(_))
      .recover {
        case e: UpstreamErrorResponse if e.statusCode == NOT_FOUND => None
      }
  }

}
