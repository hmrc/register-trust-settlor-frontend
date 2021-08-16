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

package controllers.deceased_settlor

import base.SpecBase
import controllers.routes._
import models.TaskStatus.Completed
import models.UserAnswers
import models.pages.FullName
import org.mockito.Matchers.{any, eq => eqTo}
import org.mockito.Mockito.{reset, verify, when}
import org.scalatest.BeforeAndAfterEach
import pages.deceased_settlor._
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustsStoreService
import uk.gov.hmrc.http.HttpResponse
import utils.print.DeceasedSettlorPrintHelper
import viewmodels.AnswerSection
import views.html.deceased_settlor.DeceasedSettlorAnswerView

import scala.concurrent.Future

class DeceasedSettlorAnswerControllerSpec extends SpecBase with BeforeAndAfterEach {

  private val name: FullName = FullName("First", None, "Last")

  private val trustsStoreService: TrustsStoreService = mock[TrustsStoreService]

  override def beforeEach(): Unit = {
    reset(mockCreateDraftRegistrationService, trustsStoreService)

    when(mockCreateDraftRegistrationService.removeLivingSettlorsMappedPiece(any())(any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))

    when(trustsStoreService.updateTaskStatus(any(), any())(any(), any()))
      .thenReturn(Future.successful(HttpResponse(OK, "")))
  }

  "DeceasedSettlorAnswer Controller" must {

    lazy val deceasedSettlorsAnswerRoute = routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId).url

    "return OK and the correct view for a GET" in {

      val mockPrintHelper = mock[DeceasedSettlorPrintHelper]

      val fakeAnswerSection = AnswerSection()

      when(mockPrintHelper.checkDetailsSection(any(), any(), any(), any())(any()))
        .thenReturn(fakeAnswerSection)

      val answers: UserAnswers = emptyUserAnswers
        .set(SettlorsNamePage, name).success.value

      val application: Application = applicationBuilder(userAnswers = Some(answers))
        .overrides(bind[DeceasedSettlorPrintHelper].toInstance(mockPrintHelper))
        .build()

      val request = FakeRequest(GET, routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId).url)

      val result: Future[Result] = route(application, request).value

      val view: DeceasedSettlorAnswerView = application.injector.instanceOf[DeceasedSettlorAnswerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId, Seq(fakeAnswerSection))(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, deceasedSettlorsAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, deceasedSettlorsAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to SettlorNamePage when settlor name is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, deceasedSettlorsAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorsNameController.onPageLoad(fakeDraftId).url

      application.stop()
    }

    "remove living settlors mapped piece for a POST" in {

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(SettlorsNamePage, FullName("First", None, "Last")).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[TrustsStoreService].toInstance(trustsStoreService))
        .build()

      val request = FakeRequest(POST, deceasedSettlorsAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe fakeNavigator.desiredRoute.url

      verify(mockCreateDraftRegistrationService).removeLivingSettlorsMappedPiece(any())(any())
      verify(trustsStoreService).updateTaskStatus(any(), eqTo(Completed))(any(), any())

      application.stop()
    }
  }
}
