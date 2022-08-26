/*
 * Copyright 2022 HM Revenue & Customs
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

package controllers.living_settlor

import base.SpecBase
import controllers.routes._
import forms.deceased_settlor.SettlorIndividualOrBusinessFormProvider
import models.pages.IndividualOrBusiness
import pages.living_settlor.SettlorIndividualOrBusinessPage
import play.api.test.FakeRequest
import play.api.test.Helpers.{route, _}
import views.html.living_settlor.SettlorIndividualOrBusinessView

class SettlorIndividualOrBusinessControllerSpec extends SpecBase {

  lazy val settlorIndividualOrBusinessRoute = routes.SettlorIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url

  val formProvider = new SettlorIndividualOrBusinessFormProvider()
  val form = formProvider()
  val index = 0

  "SettlorIndividualOrBusiness Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualOrBusinessRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorIndividualOrBusinessView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, index)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualOrBusinessRoute)

      val view = application.injector.instanceOf[SettlorIndividualOrBusinessView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(IndividualOrBusiness.values.head), fakeDraftId, index)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, settlorIndividualOrBusinessRoute)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a 500 when there's an issue parsing the user answers" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, routes.SettlorIndividualOrBusinessController.onPageLoad(2, fakeDraftId).url)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual INTERNAL_SERVER_ERROR

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request =
        FakeRequest(POST, settlorIndividualOrBusinessRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[SettlorIndividualOrBusinessView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorIndividualOrBusinessRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorIndividualOrBusinessRoute)
          .withFormUrlEncodedBody(("value", IndividualOrBusiness.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
