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
import forms.UKAddressFormProvider
import models.pages.UKAddress
import pages.living_settlor.business.{SettlorBusinessAddressUKPage, SettlorBusinessNamePage}
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.living_settlor.business.SettlorBusinessAddressUKView

class SettlorBusinessAddressUKControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val formProvider = new UKAddressFormProvider()
  val form = formProvider()
  val index = 0
  val name = "Business name"

  lazy val settlorBusinessAddressUKRoute: String = routes.SettlorBusinessAddressUKController.onPageLoad(index, fakeDraftId).url

  "SettlorBusinessAddressUK Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(SettlorBusinessNamePage(index), name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessAddressUKRoute)

      val view = application.injector.instanceOf[SettlorBusinessAddressUKView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index, name)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(SettlorBusinessNamePage(index), name).success.value
        .set(SettlorBusinessAddressUKPage(index), UKAddress("line 1", "line 2", Some("line 3"), Some("line 4"), "line 5")).success.value

      val application = applicationBuilder(Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessAddressUKRoute)

      val view = application.injector.instanceOf[SettlorBusinessAddressUKView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(UKAddress("line 1", "line 2", Some("line 3"), Some("line 4"), "line 5")), fakeDraftId, index, name)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorBusinessNamePage(index), name).success.value
        .set(SettlorBusinessAddressUKPage(index), UKAddress("line 1", "line 2", Some("line 3"), Some("line 4"), "line 5")).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorBusinessAddressUKRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("line3", "value 3"), ("line4", "value 4"), ("postcode", "NE1 1ZZ"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to Settlors Name page when Settlors name is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, settlorBusinessAddressUKRoute)

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
        FakeRequest(POST, settlorBusinessAddressUKRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[SettlorBusinessAddressUKView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, name)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorBusinessAddressUKRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorBusinessAddressUKRoute)
          .withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
