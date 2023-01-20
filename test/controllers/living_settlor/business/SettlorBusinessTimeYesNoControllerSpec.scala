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
import forms.YesNoFormProvider
import pages.living_settlor.business.{SettlorBusinessNamePage, SettlorBusinessTimeYesNoPage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.living_settlor.business.SettlorBusinessTimeYesNoView

class SettlorBusinessTimeYesNoControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new YesNoFormProvider()
  val form = formProvider.withPrefix("settlorBusinessTimeYesNo")
  val index = 0
  val name = "Business name"

  lazy val settlorBusinessTimeYesNoRoute = routes.SettlorBusinessTimeYesNoController.onPageLoad(index, fakeDraftId).url

  "SettlorBusinessTimeYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(SettlorBusinessNamePage(index), name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessTimeYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorBusinessTimeYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, name)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(SettlorBusinessNamePage(index), name).success.value
        .set(SettlorBusinessTimeYesNoPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessTimeYesNoRoute)

      val view = application.injector.instanceOf[SettlorBusinessTimeYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), fakeDraftId, index, name)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorBusinessNamePage(index), name).success.value
        .set(SettlorBusinessTimeYesNoPage(index), true).success.value
      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorBusinessTimeYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to Settlors Name page when Settlors name is not answered" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorBusinessTimeYesNoPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessTimeYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorBusinessNameController.onPageLoad(index, fakeDraftId).url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorBusinessNamePage(index),
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorBusinessTimeYesNoRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[SettlorBusinessTimeYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, name)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorBusinessTimeYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorBusinessTimeYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
