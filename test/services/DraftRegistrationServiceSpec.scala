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

import base.SpecBase
import connectors.SubmissionDraftConnector
import models.RolesInCompanies.{AllRolesAnswered, CouldNotDetermine, NoIndividualBeneficiaries, NotAllRolesAnswered}
import models.pages.KindOfTrust
import org.mockito.ArgumentMatchers.any
import pages.trust_type.KindOfTrustPage
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

class DraftRegistrationServiceSpec extends SpecBase {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  private val mockConnector: SubmissionDraftConnector = mock[SubmissionDraftConnector]
  private val mockTrustStore: TrustsStoreService      = mock[TrustsStoreService]

  private val service = new DraftRegistrationService(mockConnector, mockTrustStore)

  "Draft registration service" when {

    ".amendBeneficiariesState" when {

      "kind of trust is not Employee related" must {

        "clean up role in companies answers" in {

          reset(mockTrustStore)
          reset(mockConnector)

          val userAnswers = emptyUserAnswers
            .set(KindOfTrustPage, KindOfTrust.Deed)
            .success
            .value

          when(mockConnector.removeRoleInCompanyAnswers(any())(any(), any()))
            .thenReturn(Future.successful(HttpResponse.apply(OK, "")))

          Await.result(service.amendBeneficiariesState(fakeDraftId, userAnswers), Duration.Inf)

          verify(mockConnector, never).allIndividualBeneficiariesHaveRoleInCompany(any())(any(), any())
          verify(mockTrustStore, never).updateBeneficiaryTaskStatus(any(), any())(any(), any())

          verify(mockConnector, times(1)).removeRoleInCompanyAnswers(any())(any(), any())
        }
      }

      "kind of trust is Employee related" when {

        "there are no individual beneficiaries" must {

          "do nothing" in {

            reset(mockTrustStore)
            reset(mockConnector)

            val userAnswers = emptyUserAnswers
              .set(KindOfTrustPage, KindOfTrust.Employees)
              .success
              .value

            when(mockConnector.allIndividualBeneficiariesHaveRoleInCompany(any())(any(), any()))
              .thenReturn(Future.successful(NoIndividualBeneficiaries))

            Await.result(service.amendBeneficiariesState(fakeDraftId, userAnswers), Duration.Inf)

            verify(mockConnector, times(1)).allIndividualBeneficiariesHaveRoleInCompany(any())(any(), any())
            verify(mockTrustStore, never).updateBeneficiaryTaskStatus(any(), any())(any(), any())
            verify(mockConnector, never).removeRoleInCompanyAnswers(any())(any(), any())
          }
        }

        "can not determine if all roles are answered" must {

          "do nothing" in {

            reset(mockTrustStore)
            reset(mockConnector)

            val userAnswers = emptyUserAnswers
              .set(KindOfTrustPage, KindOfTrust.Employees)
              .success
              .value

            when(mockConnector.allIndividualBeneficiariesHaveRoleInCompany(any())(any(), any()))
              .thenReturn(Future.successful(CouldNotDetermine))

            Await.result(service.amendBeneficiariesState(fakeDraftId, userAnswers), Duration.Inf)

            verify(mockConnector, times(1)).allIndividualBeneficiariesHaveRoleInCompany(any())(any(), any())
            verify(mockTrustStore, never).updateBeneficiaryTaskStatus(any(), any())(any(), any())
            verify(mockConnector, never).removeRoleInCompanyAnswers(any())(any(), any())
          }
        }

        "there are individual beneficiaries" when {

          "the individuals all have roles in company defined" must {
            "do nothing" in {

              reset(mockTrustStore)
              reset(mockConnector)

              val userAnswers = emptyUserAnswers
                .set(KindOfTrustPage, KindOfTrust.Employees)
                .success
                .value

              when(mockConnector.allIndividualBeneficiariesHaveRoleInCompany(any())(any(), any()))
                .thenReturn(Future.successful(AllRolesAnswered))

              Await.result(service.amendBeneficiariesState(fakeDraftId, userAnswers), Duration.Inf)

              verify(mockConnector, times(1)).allIndividualBeneficiariesHaveRoleInCompany(any())(any(), any())
              verify(mockTrustStore, never).updateBeneficiaryTaskStatus(any(), any())(any(), any())
              verify(mockConnector, never).removeRoleInCompanyAnswers(any())(any(), any())
            }
          }

          "any individual beneficiary does not have role in company defined" must {

            "set beneficiaries section to in progress" in {

              reset(mockTrustStore)
              reset(mockConnector)

              val userAnswers = emptyUserAnswers
                .set(KindOfTrustPage, KindOfTrust.Employees)
                .success
                .value

              when(mockConnector.allIndividualBeneficiariesHaveRoleInCompany(any())(any(), any()))
                .thenReturn(Future.successful(NotAllRolesAnswered))

              when(mockTrustStore.updateBeneficiaryTaskStatus(any(), any())(any(), any()))
                .thenReturn(Future.successful(HttpResponse.apply(OK, "")))

              Await.result(service.amendBeneficiariesState(fakeDraftId, userAnswers), Duration.Inf)

              verify(mockConnector, times(1)).allIndividualBeneficiariesHaveRoleInCompany(any())(any(), any())
              verify(mockTrustStore, times(1)).updateBeneficiaryTaskStatus(any(), any())(any(), any())
              verify(mockConnector, never).removeRoleInCompanyAnswers(any())(any(), any())
            }
          }
        }
      }
    }

    ".removeRoleInCompanyAnswers" must {
      "return Http response" in {

        val status: Int = 200

        when(mockConnector.removeRoleInCompanyAnswers(any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(status, "")))

        val result = Await.result(service.removeBeneficiaryRoleInCompanyAnswers(fakeDraftId), Duration.Inf)

        result.status mustBe status
      }
    }

    ".removeDeceasedSettlorMappedPiece" must {

      "return Http response" in {

        val status: Int = 200

        when(mockConnector.removeDeceasedSettlorMappedPiece(any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(status, "")))

        val result = Await.result(service.removeDeceasedSettlorMappedPiece(fakeDraftId), Duration.Inf)

        result.status mustBe status
      }
    }

    ".removeLivingSettlorsMappedPiece" must {
      "return Http response" in {

        val status: Int = 200

        when(mockConnector.removeLivingSettlorsMappedPiece(any())(any(), any()))
          .thenReturn(Future.successful(HttpResponse(status, "")))

        val result = Await.result(service.removeLivingSettlorsMappedPiece(fakeDraftId), Duration.Inf)

        result.status mustBe status
      }
    }
  }
}
