/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.LocalDate

import config.FrontendAppConfig
import javax.inject.Inject
import models.{AllStatus, RegistrationSubmission, SubmissionDraftData, SubmissionDraftResponse}
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

class SubmissionDraftConnector @Inject()(http: HttpClient, config : FrontendAppConfig) {

  val submissionsBaseUrl = s"${config.trustsUrl}/trusts/register/submission-drafts"
  private val beneficiariesSection = "beneficiaries"
  private val statusSection = "status"

  def setDraftMain(draftId : String, draftData: JsValue, inProgress: Boolean, reference: Option[String])
                     (implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] = {
    val submissionDraftData = SubmissionDraftData(draftData, reference, Some(inProgress))
    http.POST[JsValue, HttpResponse](s"$submissionsBaseUrl/$draftId/MAIN", Json.toJson(submissionDraftData))
  }

  def setDraftSectionSet(draftId: String, section: String, data: RegistrationSubmission.DataSet)
                        (implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] = {
    http.POST[JsValue, HttpResponse](s"$submissionsBaseUrl/$draftId/set/$section", Json.toJson(data))
  }

  def setDraftSection(draftId : String, section: String, draftData: JsValue)
                     (implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] = {
    val submissionDraftData = SubmissionDraftData(draftData, None, None)
    http.POST[JsValue, HttpResponse](s"$submissionsBaseUrl/$draftId/$section", Json.toJson(submissionDraftData))
  }

  def getDraftMain(draftId: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[SubmissionDraftResponse] = {
    http.GET[SubmissionDraftResponse](s"$submissionsBaseUrl/$draftId/MAIN")
  }

  def getDraftSection(draftId: String, section: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[SubmissionDraftResponse] = {
    http.GET[SubmissionDraftResponse](s"$submissionsBaseUrl/$draftId/$section")
  }

  def getTrustSetupDate(draftId: String)(implicit hc:HeaderCarrier, ec : ExecutionContext) : Future[Option[LocalDate]] = {
    http.GET[HttpResponse](s"$submissionsBaseUrl/$draftId/when-trust-setup").map {
      response =>
        (response.json \ "startDate").asOpt[LocalDate]
    }.recover {
      case _ => None
    }
  }

  def getDraftBeneficiaries(draftId: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[SubmissionDraftResponse] =
    getDraftSection(draftId, beneficiariesSection)

  def getStatus(draftId: String)(implicit hc: HeaderCarrier, ec : ExecutionContext): Future[AllStatus] =
    getDraftSection(draftId, statusSection).map {
      section => section.data.as[AllStatus]
    }

  def setStatus(draftId : String, status: AllStatus)
               (implicit hc: HeaderCarrier, ec : ExecutionContext): Future[HttpResponse] = {
    val submissionDraftData = SubmissionDraftData(Json.toJson(status), None, None)
    http.POST[JsValue, HttpResponse](s"$submissionsBaseUrl/$draftId/status", Json.toJson(submissionDraftData))
  }
}
