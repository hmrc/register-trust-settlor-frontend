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

package controllers.deceased_settlor.mld5

import base.SpecBase
import controllers.deceased_settlor.mld5.routes.CountryOfResidenceInTheUkYesNoController
import controllers.deceased_settlor.routes.SettlorsNameController
import controllers.routes.SessionExpiredController
import forms.YesNoFormProvider
import models.pages.FullName
import pages.deceased_settlor.SettlorsNamePage
import pages.deceased_settlor.mld5.CountryOfResidenceInTheUkYesNoPage
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.deceased_settlor.mld5.CountryOfResidenceInTheUkYesNoView

class CountryOfResidenceInTheUkYesNoControllerSpec extends SpecBase {

  val form = new YesNoFormProvider().withPrefix("5mld.countryOfResidenceInTheUkYesNo")

  lazy val countryOfResidenceInTheUkYesNoRoute = CountryOfResidenceInTheUkYesNoController.onPageLoad(fakeDraftId).url

  val name = FullName("first name", None, "Last name")

  "CountryOfResidenceInTheUkYesNoController Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(SettlorsNamePage,
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, countryOfResidenceInTheUkYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CountryOfResidenceInTheUkYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(CountryOfResidenceInTheUkYesNoPage, true).success.value.set(SettlorsNamePage,
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, countryOfResidenceInTheUkYesNoRoute)

      val view = application.injector.instanceOf[CountryOfResidenceInTheUkYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true),fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorsNamePage,
        name).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, countryOfResidenceInTheUkYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorsNamePage,
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, countryOfResidenceInTheUkYesNoRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[CountryOfResidenceInTheUkYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm,fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, countryOfResidenceInTheUkYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, countryOfResidenceInTheUkYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to SettlorNamePage when settlor name is not answered" in {


      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, countryOfResidenceInTheUkYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SettlorsNameController.onPageLoad(fakeDraftId).url

      application.stop()
    }
  }
}
