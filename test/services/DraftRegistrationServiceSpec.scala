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

package services

import java.time.LocalDateTime

import base.SpecBase
import connectors.SubmissionDraftConnector
import models.{AllStatus, SubmissionDraftResponse}
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import play.api.libs.json.Json
import repositories.RegistrationsRepository
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DraftRegistrationServiceSpec extends SpecBase {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private val mockRepository: RegistrationsRepository = mock[RegistrationsRepository]
  private val mockConnector: SubmissionDraftConnector = mock[SubmissionDraftConnector]

  private val service = new DraftRegistrationService(mockRepository, mockConnector)

  "Draft registration service" when {

    ".setBeneficiaryStatus" when {

      "response data not validated as ReadOnlyUserAnswers" must {
        "do nothing" in {

          reset(mockRepository)
          reset(mockConnector)

          val response = SubmissionDraftResponse(LocalDateTime.now(), Json.obj(), None)

          when(mockConnector.getDraftBeneficiaries(any())(any(), any()))
            .thenReturn(Future.successful(response))

          Await.result(service.setBeneficiaryStatus(fakeDraftId), Duration.Inf)

          verify(mockRepository, times(0)).getAllStatus(any())(any())
          verify(mockRepository, times(0)).setAllStatus(any(), any())(any())
        }
      }

      "there are no individual beneficiaries" must {
        "do nothing" in {

          reset(mockRepository)
          reset(mockConnector)

          val data = Json.parse(
            """
              |{
              |  "data": {
              |    "beneficiaries": {
              |      "classOfBeneficiaries": [
              |        {
              |          "description": "Future grandchildren"
              |        }
              |      ]
              |    }
              |  }
              |}
            """.stripMargin)

          val response = SubmissionDraftResponse(LocalDateTime.now(), data, None)

          when(mockConnector.getDraftBeneficiaries(any())(any(), any()))
            .thenReturn(Future.successful(response))

          Await.result(service.setBeneficiaryStatus(fakeDraftId), Duration.Inf)

          verify(mockRepository, times(0)).getAllStatus(any())(any())
          verify(mockRepository, times(0)).setAllStatus(any(), any())(any())
        }
      }

      "there are beneficiaries" when {
        "individual beneficiary has role in company defined" must {
          "do nothing" in {

            reset(mockRepository)
            reset(mockConnector)

            val data = Json.parse(
              """
                |{
                |  "data": {
                |    "beneficiaries": {
                |      "individualBeneficiaries": [
                |        {
                |          "name": {
                |            "firstName": "Joe",
                |            "lastName": "Bloggs"
                |          },
                |          "roleInCompany": "Director"
                |        }
                |      ]
                |    }
                |  }
                |}
            """.stripMargin)

            val response = SubmissionDraftResponse(LocalDateTime.now(), data, None)

            when(mockConnector.getDraftBeneficiaries(any())(any(), any()))
              .thenReturn(Future.successful(response))

            Await.result(service.setBeneficiaryStatus(fakeDraftId), Duration.Inf)

            verify(mockRepository, times(0)).getAllStatus(any())(any())
            verify(mockRepository, times(0)).setAllStatus(any(), any())(any())
          }
        }

        "individual beneficiary does not have role in company defined" must {
          "set beneficiaries section to In Progress as role in company needs answering" in {

            reset(mockRepository)
            reset(mockConnector)

            val data = Json.parse(
              """
                |{
                |  "data": {
                |    "beneficiaries": {
                |      "individualBeneficiaries": [
                |        {
                |          "name": {
                |            "firstName": "Joe",
                |            "lastName": "Bloggs"
                |          }
                |        }
                |      ]
                |    }
                |  }
                |}
            """.stripMargin)

            val response = SubmissionDraftResponse(LocalDateTime.now(), data, None)

            when(mockConnector.getDraftBeneficiaries(any())(any(), any()))
              .thenReturn(Future.successful(response))

            when(mockRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))
            when(mockRepository.setAllStatus(any(), any())(any())).thenReturn(Future.successful(true))

            Await.result(service.setBeneficiaryStatus(fakeDraftId), Duration.Inf)

            verify(mockRepository, times(1)).getAllStatus(any())(any())
            verify(mockRepository, times(1)).setAllStatus(any(), any())(any())
          }
        }

        "any individual beneficiary doesn't have role in company defined" must {
          "set beneficiaries section to In Progress as role in company needs answering" in {

            reset(mockRepository)
            reset(mockConnector)

            val data = Json.parse(
              """
                |{
                |  "data": {
                |    "beneficiaries": {
                |      "individualBeneficiaries": [
                |        {
                |          "name": {
                |            "firstName": "Joe",
                |            "lastName": "Bloggs"
                |          },
                |          "roleInCompany": "Director"
                |        },
                |        {
                |          "name": {
                |            "firstName": "John",
                |            "lastName": "Doe"
                |          }
                |        },
                |        {
                |          "name": {
                |            "firstName": "Jane",
                |            "lastName": "Doe"
                |          },
                |          "roleInCompany": "Employee"
                |        }
                |      ]
                |    }
                |  }
                |}
            """.stripMargin)

            val response = SubmissionDraftResponse(LocalDateTime.now(), data, None)

            when(mockConnector.getDraftBeneficiaries(any())(any(), any()))
              .thenReturn(Future.successful(response))

            when(mockRepository.getAllStatus(any())(any())).thenReturn(Future.successful(AllStatus()))
            when(mockRepository.setAllStatus(any(), any())(any())).thenReturn(Future.successful(true))

            Await.result(service.setBeneficiaryStatus(fakeDraftId), Duration.Inf)

            verify(mockRepository, times(1)).getAllStatus(any())(any())
            verify(mockRepository, times(1)).setAllStatus(any(), any())(any())
          }
        }
      }
    }

    ".removeRoleInCompanyAnswers" must {
      "return Http response" in {

        val status: Int = 200

        when(mockConnector.removeRoleInCompanyAnswers(any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(status)))

        val result = Await.result(service.removeRoleInCompanyAnswers(fakeDraftId), Duration.Inf)

        result.status mustBe status
      }
    }
  }
}
