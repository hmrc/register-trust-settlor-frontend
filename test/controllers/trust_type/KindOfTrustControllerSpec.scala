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

package controllers.trust_type

import base.SpecBase
import controllers.routes._
import forms.KindOfTrustFormProvider
import models.pages.KindOfTrust
import pages.trust_type.{KindOfTrustPage, SetUpByLivingSettlorYesNoPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.trust_type.KindOfTrustView

class KindOfTrustControllerSpec extends SpecBase {

  val index = 0

  lazy val kindOfTrustRoute = routes.KindOfTrustController.onPageLoad(fakeDraftId).url

  val formProvider = new KindOfTrustFormProvider()
  val form         = formProvider()

  "KindOfTrust Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(SetUpByLivingSettlorYesNoPage, false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, kindOfTrustRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[KindOfTrustView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(SetUpByLivingSettlorYesNoPage, false)
        .success
        .value
        .set(KindOfTrustPage, KindOfTrust.values.head)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, kindOfTrustRoute)

      val view = application.injector.instanceOf[KindOfTrustView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(KindOfTrust.values.head), fakeDraftId)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SetUpByLivingSettlorYesNoPage, false).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, kindOfTrustRoute)
          .withFormUrlEncodedBody(("value", KindOfTrust.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SetUpByLivingSettlorYesNoPage, false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, kindOfTrustRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[KindOfTrustView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, kindOfTrustRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, kindOfTrustRoute)
          .withFormUrlEncodedBody(("value", KindOfTrust.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

}
