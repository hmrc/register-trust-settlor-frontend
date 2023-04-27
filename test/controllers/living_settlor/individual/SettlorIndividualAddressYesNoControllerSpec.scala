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
import controllers.routes._
import forms.YesNoFormProvider
import models.pages.FullName
import pages.living_settlor.individual.{SettlorAddressYesNoPage, SettlorAliveYesNoPage, SettlorIndividualNamePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.living_settlor.individual.SettlorIndividualAddressYesNoView

class SettlorIndividualAddressYesNoControllerSpec extends SpecBase {

  private val formProvider   = new YesNoFormProvider()
  private val form           = formProvider.withPrefix("settlorIndividualAddressYesNo")
  private val index          = 0
  private val name: FullName = FullName("First", Some("Middle"), "Last")

  private lazy val settlorIndividualAddressYesNoRoute: String =
    routes.SettlorIndividualAddressYesNoController.onPageLoad(index, fakeDraftId).url

  "SettlorIndividualAddressYesNo Controller" must {

    "return OK and the correct view for a GET" in {
      val formContentInPastTense = formProvider.withPrefix("settlorIndividualAddressYesNoPastTense")

      val userAnswers = emptyUserAnswers.set(SettlorIndividualNamePage(index), name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualAddressYesNoRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorIndividualAddressYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(formContentInPastTense, fakeDraftId, index, name, settlorAliveAtRegistration = false)(
          request,
          messages
        ).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorAliveYesNoPage(index), true)
        .success
        .value
        .set(SettlorIndividualNamePage(index), name)
        .success
        .value
        .set(SettlorAddressYesNoPage(index), true)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualAddressYesNoRoute)

      val view = application.injector.instanceOf[SettlorIndividualAddressYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), fakeDraftId, index, name, settlorAliveAtRegistration = true)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorAliveYesNoPage(index), true)
        .success
        .value
        .set(SettlorIndividualNamePage(index), name)
        .success
        .value
        .set(SettlorAddressYesNoPage(index), true)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorIndividualAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "redirect to Settlors Name page when Settlors name is not answered" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorAliveYesNoPage(index), true)
        .success
        .value
        .set(SettlorAddressYesNoPage(index), true)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualAddressYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorIndividualNameController.onPageLoad(index, fakeDraftId).url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorAliveYesNoPage(index), true)
        .success
        .value
        .set(SettlorIndividualNamePage(index), name)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorIndividualAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[SettlorIndividualAddressYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, index, name, settlorAliveAtRegistration = true)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorIndividualAddressYesNoRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorIndividualAddressYesNoRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
