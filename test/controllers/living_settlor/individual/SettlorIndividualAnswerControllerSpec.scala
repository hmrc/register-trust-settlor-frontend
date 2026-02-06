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

package controllers.living_settlor.individual

import base.SpecBase
import controllers.living_settlor.individual.routes.SettlorIndividualNameController
import controllers.routes._
import models.UserAnswers
import models.pages._
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import pages.living_settlor._
import pages.living_settlor.individual._
import pages.trust_type._
import play.api.Application
import play.api.inject.bind
import play.api.mvc.{Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.twirl.api.Html
import uk.gov.hmrc.http.HttpResponse
import utils.print.LivingSettlorPrintHelper
import viewmodels.{AnswerRow, AnswerSection}
import views.html.living_settlor.SettlorAnswersView

import scala.concurrent.Future

class SettlorIndividualAnswerControllerSpec extends SpecBase {

  private val settlorName: FullName = FullName("first name", Some("middle name"), "last name")
  private val index: Int            = 0

  private lazy val settlorIndividualAnswerRoute: String =
    routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId).url

  private lazy val onSubmit: Call = routes.SettlorIndividualAnswerController.onSubmit(index, fakeDraftId)

  private val baseAnswers: UserAnswers = emptyUserAnswers
    .set(SettlorIndividualNamePage(index), settlorName)
    .success
    .value

  private val answerRow = AnswerRow(
    label = "settlorIndividualName.checkYourAnswersLabel",
    answer = Html(settlorName.toString),
    changeUrl = Some(SettlorIndividualNameController.onPageLoad(index, fakeDraftId).url)
  )

  "SettlorIndividualAnswer Controller" must {

    "return OK and the correct view for a GET where messageKeyPrefix is None" in {

      val userAnswers     = baseAnswers.set(SettlorAliveYesNoPage(index), true).success.value
      val mockPrintHelper = mock[LivingSettlorPrintHelper]

      val fakeAnswerSection = AnswerSection(rows = Seq(answerRow))

      when(mockPrintHelper.checkDetailsSection(any(), any(), any(), any(), any())(any()))
        .thenReturn(fakeAnswerSection)

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[LivingSettlorPrintHelper].toInstance(mockPrintHelper))
        .build()

      val request = FakeRequest(GET, settlorIndividualAnswerRoute)

      val result: Future[Result] = route(application, request).value

      val view: SettlorAnswersView = application.injector.instanceOf[SettlorAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(onSubmit, Seq(fakeAnswerSection))(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET where messageKeyPrefix is defined" in {

      val userAnswers     = baseAnswers.set(SettlorAliveYesNoPage(index), false).success.value
      val prefix          = "PastTense"
      val mockPrintHelper = mock[LivingSettlorPrintHelper]

      val fakeAnswerSection =
        AnswerSection(rows = Seq(answerRow.copy(label = s"settlorIndividualName$prefix.checkYourAnswersLabel")))

      when(mockPrintHelper.checkDetailsSection(any(), any(), any(), any(), any())(any()))
        .thenReturn(fakeAnswerSection)

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[LivingSettlorPrintHelper].toInstance(mockPrintHelper))
        .build()

      val request = FakeRequest(GET, settlorIndividualAnswerRoute)

      val result: Future[Result] = route(application, request).value

      val view: SettlorAnswersView = application.injector.instanceOf[SettlorAnswersView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(onSubmit, Seq(fakeAnswerSection))(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorIndividualAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "update beneficiary status when kindOfTrustPage is set to Employees" in {

      reset(mockCreateDraftRegistrationService)

      val userAnswers = baseAnswers
        .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual)
        .success
        .value
        .set(KindOfTrustPage, KindOfTrust.Employees)
        .success
        .value

      when(mockCreateDraftRegistrationService.amendBeneficiariesState(any(), any())(any()))
        .thenReturn(Future.successful(()))

      when(mockCreateDraftRegistrationService.removeDeceasedSettlorMappedPiece(any())(any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, settlorIndividualAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe fakeNavigator.desiredRoute.url

      verify(mockCreateDraftRegistrationService, times(1)).amendBeneficiariesState(any(), any())(any())
      verify(mockCreateDraftRegistrationService, times(1)).removeDeceasedSettlorMappedPiece(any())(any())

      application.stop()
    }

    "remove role in company answers when kindOfTrustPage is not set to Employees" in {

      reset(mockCreateDraftRegistrationService)

      val userAnswers = baseAnswers
        .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual)
        .success
        .value
        .set(KindOfTrustPage, KindOfTrust.Deed)
        .success
        .value

      when(mockCreateDraftRegistrationService.amendBeneficiariesState(any(), any())(any()))
        .thenReturn(Future.successful(()))

      when(mockCreateDraftRegistrationService.removeDeceasedSettlorMappedPiece(any())(any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, settlorIndividualAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe fakeNavigator.desiredRoute.url

      verify(mockCreateDraftRegistrationService, times(1)).amendBeneficiariesState(any(), any())(any())
      verify(mockCreateDraftRegistrationService, times(1)).removeDeceasedSettlorMappedPiece(any())(any())

      application.stop()
    }

  }

}
