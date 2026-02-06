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

package connectors

import base.SpecBase
import com.github.tomakehurst.wiremock.client.WireMock._
import models.TaskStatus.Completed
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HeaderCarrier
import utils.WireMockHelper

class TrustsStoreConnectorSpec extends SpecBase with Matchers with OptionValues with WireMockHelper {

  implicit lazy val hc: HeaderCarrier = HeaderCarrier()

  override lazy val app: Application = new GuiceApplicationBuilder()
    .configure(
      Seq(
        "microservice.services.trusts-store.port" -> server.port(),
        "auditing.enabled"                        -> false
      ): _*
    )
    .build()

  private lazy val connector = app.injector.instanceOf[TrustsStoreConnector]

  "TrustsStoreConnector" when {

    ".updateTaskStatus" must {

      val url = s"/trusts-store/register/tasks/update-settlors/$draftId"

      "return OK with the current task status" in {

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(ok())
        )

        whenReady(connector.updateTaskStatus(draftId, Completed)) {
          _.status mustBe 200
        }
      }

      "return default tasks when a failure occurs" in {

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(serverError())
        )

        whenReady(connector.updateTaskStatus(draftId, Completed)) {
          _.status mustBe 500
        }
      }
    }

    ".updateBeneficiaryTaskStatus" must {

      val url = s"/trusts-store/register/tasks/update-beneficiaries/$draftId"

      "return OK with the current task status" in {

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(ok())
        )

        whenReady(connector.updateBeneficiaryTaskStatus(draftId, Completed)) {
          _.status mustBe 200
        }
      }

      "return default tasks when a failure occurs" in {

        server.stubFor(
          post(urlEqualTo(url))
            .willReturn(serverError())
        )

        whenReady(connector.updateBeneficiaryTaskStatus(draftId, Completed)) {
          _.status mustBe 500
        }
      }
    }
  }

}
