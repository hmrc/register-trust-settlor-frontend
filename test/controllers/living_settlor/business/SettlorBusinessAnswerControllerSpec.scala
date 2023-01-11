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

package controllers.living_settlor.business

import base.SpecBase
import controllers.routes._
import models.UserAnswers
import models.pages._
import org.mockito.ArgumentMatchers.any
import pages.living_settlor._
import pages.living_settlor.business._
import pages.trust_type._
import play.api.Application
import play.api.inject.bind
import play.api.mvc.{Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import utils.print.BusinessSettlorPrintHelper
import viewmodels.AnswerSection
import views.html.living_settlor.SettlorAnswersView

import scala.concurrent.Future

class SettlorBusinessAnswerControllerSpec extends SpecBase {

  private val settlorName: String = "Settlor Org"
  private val index: Int = 0

  private lazy val settlorBusinessAnswerRoute: String = routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId).url
  private lazy val onSubmit: Call = routes.SettlorBusinessAnswerController.onSubmit(index, fakeDraftId)

  val baseAnswers: UserAnswers = emptyUserAnswers
    .set(SettlorBusinessNamePage(index), settlorName).success.value

  "SettlorBusinessAnswer Controller" must {

    "return OK and the correct view for a GET" in {

      val mockPrintHelper = mock[BusinessSettlorPrintHelper]

      val fakeAnswerSection = AnswerSection()

      when(mockPrintHelper.checkDetailsSection(any(), any(), any(), any())(any()))
        .thenReturn(fakeAnswerSection)

      val application: Application = applicationBuilder(userAnswers = Some(baseAnswers))
        .overrides(bind[BusinessSettlorPrintHelper].toInstance(mockPrintHelper))
        .build()

      val request = FakeRequest(GET, settlorBusinessAnswerRoute)

      val result: Future[Result] = route(application, request).value

      val view: SettlorAnswersView = application.injector.instanceOf[SettlorAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(onSubmit, Seq(fakeAnswerSection))(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorBusinessAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "update beneficiary status when kindOfTrustPage is set to Employees" in {

      reset(mockCreateDraftRegistrationService)

      val userAnswers = baseAnswers
        .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
        .set(KindOfTrustPage, KindOfTrust.Employees).success.value

      when(mockCreateDraftRegistrationService.amendBeneficiariesState(any(),any ())(any()))
        .thenReturn(Future.successful(()))

      when(mockCreateDraftRegistrationService.removeDeceasedSettlorMappedPiece(any())(any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, settlorBusinessAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe fakeNavigator.desiredRoute.url

      verify(mockCreateDraftRegistrationService, times(1)).amendBeneficiariesState(any(),any())(any())
      verify(mockCreateDraftRegistrationService, times(1)).removeDeceasedSettlorMappedPiece(any())(any())

      application.stop()
    }

    "remove role in company answers when kindOfTrustPage is not set to Employees" in {

      reset(mockCreateDraftRegistrationService)

      val userAnswers = baseAnswers
        .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Business).success.value
        .set(KindOfTrustPage, KindOfTrust.Deed).success.value

      when(mockCreateDraftRegistrationService.amendBeneficiariesState(any(),any ())(any()))
        .thenReturn(Future.successful(()))

      when(mockCreateDraftRegistrationService.removeDeceasedSettlorMappedPiece(any())(any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, settlorBusinessAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe fakeNavigator.desiredRoute.url

      verify(mockCreateDraftRegistrationService).amendBeneficiariesState(any(), any())(any())
      verify(mockCreateDraftRegistrationService).removeDeceasedSettlorMappedPiece(any())(any())

      application.stop()
    }
  }
}
