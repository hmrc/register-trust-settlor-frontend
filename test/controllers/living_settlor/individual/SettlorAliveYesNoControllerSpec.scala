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

package controllers.living_settlor.individual

import base.SpecBase
import controllers.routes.SessionExpiredController
import forms.YesNoFormProvider
import pages.living_settlor.individual.SettlorAliveYesNoPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.living_settlor.individual.SettlorAliveYesNoView

class SettlorAliveYesNoControllerSpec extends SpecBase {

  private lazy val settlorAliveYesNoGetRoute = controllers.living_settlor.individual.routes.SettlorAliveYesNoController.onPageLoad(index, draftId).url
  private lazy val settlorAliveYesNoPostRoute = controllers.living_settlor.individual.routes.SettlorAliveYesNoController.onSubmit(index, draftId).url
  private val index = 0
  private val formProvider = new YesNoFormProvider()
  private val form = formProvider.withPrefix("settlorAliveYesNo")

  "SettlorAliveYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, settlorAliveYesNoGetRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorAliveYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, draftId, index)(request, messages).toString()

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(SettlorAliveYesNoPage(index), false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorAliveYesNoGetRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorAliveYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(false), draftId, index)(request, messages).toString()

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, settlorAliveYesNoPostRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorAliveYesNoPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorAliveYesNoPostRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[SettlorAliveYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, draftId, index)(request, messages).toString

      application.stop()
    }

    "return an Internal Server Error when the try fails" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, routes.SettlorAliveYesNoController.onSubmit(index + 2, fakeDraftId).url)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorAliveYesNoGetRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorAliveYesNoPostRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

}
