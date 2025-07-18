/*
 * Copyright 2024 HM Revenue & Customs
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
import connectors.TrustsStoreConnector
import models.TaskStatus.Completed
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{verify, when}
import play.api.http.Status.OK
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}

import scala.concurrent.Future

class TrustsStoreServiceSpec extends SpecBase {

  val mockConnector: TrustsStoreConnector = mock[TrustsStoreConnector]

  val trustsStoreService = new TrustsStoreService(mockConnector)

  implicit val hc: HeaderCarrier = HeaderCarrier()

  ".updateTaskStatus" must {
    "call trusts store connector" in {

      when(mockConnector.updateTaskStatus(any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val result = trustsStoreService.updateTaskStatus(draftId, Completed)

      whenReady(result) { res =>
        res.status mustBe OK
        verify(mockConnector).updateTaskStatus(eqTo(draftId), eqTo(Completed))(any(), any())
      }
    }
  }

  ".updateBeneficiaryTaskStatus" must {
    "call trusts store connector" in {

      when(mockConnector.updateBeneficiaryTaskStatus(any(), any())(any(), any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val result = trustsStoreService.updateBeneficiaryTaskStatus(draftId, Completed)

      whenReady(result) { res =>
        res.status mustBe OK
        verify(mockConnector).updateBeneficiaryTaskStatus(eqTo(draftId), eqTo(Completed))(any(), any())
      }
    }
  }
}
