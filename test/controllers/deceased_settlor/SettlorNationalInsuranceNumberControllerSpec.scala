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

package controllers.deceased_settlor

import base.SpecBase
import controllers.routes._
import forms.deceased_settlor.SettlorNationalInsuranceNumberFormProvider
import models.pages.FullName
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import pages.deceased_settlor.{SettlorNationalInsuranceNumberPage, SettlorsNamePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.DraftRegistrationService
import views.html.deceased_settlor.SettlorNationalInsuranceNumberView

import scala.concurrent.Future

class SettlorNationalInsuranceNumberControllerSpec extends SpecBase {

  val existingTrusteeNinos     = Seq("")
  val existingBeneficiaryNinos = Seq("")
  val existingProtectorNinos   = Seq("")

  val form = new SettlorNationalInsuranceNumberFormProvider()(
    existingTrusteeNinos,
    existingBeneficiaryNinos,
    existingProtectorNinos
  )

  lazy val settlorNationalInsuranceNumberRoute =
    routes.SettlorNationalInsuranceNumberController.onPageLoad(fakeDraftId).url

  val name = FullName("first name", None, "Last name")

  "SettlorNationalInsuranceNumber Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(SettlorsNamePage, name).success.value

      val mockDraftRegistrationService = mock[DraftRegistrationService]

      val sequence: IndexedSeq[String] = IndexedSeq("")

      when(mockDraftRegistrationService.retrieveTrusteeNinos(any())(any())).thenReturn(Future.successful(sequence))

      when(mockDraftRegistrationService.retrieveBeneficiaryNinos(any())(any())).thenReturn(Future.successful(sequence))

      when(mockDraftRegistrationService.retrieveProtectorNinos(any())(any())).thenReturn(Future.successful(sequence))

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), draftRegistrationService = mockDraftRegistrationService)
          .build()

      val request = FakeRequest(GET, settlorNationalInsuranceNumberRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorNationalInsuranceNumberView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorNationalInsuranceNumberPage, "answer")
        .success
        .value
        .set(SettlorsNamePage, name)
        .success
        .value

      val mockDraftRegistrationService = mock[DraftRegistrationService]

      val sequence: IndexedSeq[String] = IndexedSeq("")

      when(mockDraftRegistrationService.retrieveTrusteeNinos(any())(any())).thenReturn(Future.successful(sequence))

      when(mockDraftRegistrationService.retrieveBeneficiaryNinos(any())(any())).thenReturn(Future.successful(sequence))

      when(mockDraftRegistrationService.retrieveProtectorNinos(any())(any())).thenReturn(Future.successful(sequence))

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), draftRegistrationService = mockDraftRegistrationService)
          .build()

      val request = FakeRequest(GET, settlorNationalInsuranceNumberRoute)

      val view = application.injector.instanceOf[SettlorNationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill("answer"), fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorsNamePage, name).success.value

      val mockDraftRegistrationService = mock[DraftRegistrationService]

      val sequence: IndexedSeq[String] = IndexedSeq("")

      when(mockDraftRegistrationService.retrieveTrusteeNinos(any())(any())).thenReturn(Future.successful(sequence))

      when(mockDraftRegistrationService.retrieveBeneficiaryNinos(any())(any())).thenReturn(Future.successful(sequence))

      when(mockDraftRegistrationService.retrieveProtectorNinos(any())(any())).thenReturn(Future.successful(sequence))

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), draftRegistrationService = mockDraftRegistrationService)
          .build()

      val request =
        FakeRequest(POST, settlorNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "JP123456A"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorsNamePage, name).success.value

      val mockDraftRegistrationService = mock[DraftRegistrationService]

      val sequence: IndexedSeq[String] = IndexedSeq("")

      when(mockDraftRegistrationService.retrieveTrusteeNinos(any())(any())).thenReturn(Future.successful(sequence))

      when(mockDraftRegistrationService.retrieveBeneficiaryNinos(any())(any())).thenReturn(Future.successful(sequence))

      when(mockDraftRegistrationService.retrieveProtectorNinos(any())(any())).thenReturn(Future.successful(sequence))

      val application =
        applicationBuilder(userAnswers = Some(userAnswers), draftRegistrationService = mockDraftRegistrationService)
          .build()

      val request =
        FakeRequest(POST, settlorNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[SettlorNationalInsuranceNumberView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorNationalInsuranceNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorNationalInsuranceNumberRoute)
          .withFormUrlEncodedBody(("value", "answer"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to SettlorNamePage when settlor name is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, settlorNationalInsuranceNumberRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorsNameController.onPageLoad(fakeDraftId).url

      application.stop()
    }
  }
}
