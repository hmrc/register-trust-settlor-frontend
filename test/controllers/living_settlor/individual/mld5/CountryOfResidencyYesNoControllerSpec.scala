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

package controllers.living_settlor.individual.mld5

import base.SpecBase
import controllers.living_settlor.individual.routes._
import controllers.routes._
import forms.YesNoFormProvider
import models.UserAnswers
import models.pages.FullName
import pages.living_settlor.individual.{SettlorAliveYesNoPage, SettlorIndividualNamePage}
import pages.living_settlor.individual.mld5.CountryOfResidencyYesNoPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.living_settlor.individual.mld5.CountryOfResidencyYesNoView

class CountryOfResidencyYesNoControllerSpec extends SpecBase {

  private val formProvider: YesNoFormProvider = new YesNoFormProvider()
  private val form: Form[Boolean]             = formProvider.withPrefix("settlorIndividualCountryOfResidencyYesNo")
  private val index: Int                      = 0
  private val name: FullName                  = FullName("First", Some("Middle"), "Last")

  private lazy val onPageLoadRoute: String = routes.CountryOfResidencyYesNoController.onPageLoad(index, fakeDraftId).url

  private val validAnswer: Boolean = true

  private val baseAnswers: UserAnswers = emptyUserAnswers
    .set(SettlorAliveYesNoPage(index), true)
    .success
    .value
    .set(SettlorIndividualNamePage(index), name)
    .success
    .value

  "CountryOfResidencyYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val formContentInPastTense: Form[Boolean] =
        formProvider.withPrefix("settlorIndividualCountryOfResidencyYesNoPastTense")

      val userAnswers = emptyUserAnswers.set(SettlorIndividualNamePage(index), name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, onPageLoadRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CountryOfResidencyYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(formContentInPastTense, index, fakeDraftId, name, settlorAliveAtRegistration = false)(
          request,
          messages
        ).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseAnswers
        .set(CountryOfResidencyYesNoPage(index), validAnswer)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, onPageLoadRoute)

      val view = application.injector.instanceOf[CountryOfResidencyYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), index, fakeDraftId, name, settlorAliveAtRegistration = true)(
          request,
          messages
        ).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = baseAnswers

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, onPageLoadRoute)
        .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = baseAnswers

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, onPageLoadRoute)
        .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[CountryOfResidencyYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, index, fakeDraftId, name, settlorAliveAtRegistration = true)(request, messages).toString

      application.stop()
    }

    "redirect to Settlors Name page when Settlors name is not answered" in {

      val userAnswers = emptyUserAnswers

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, onPageLoadRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SettlorIndividualNameController.onPageLoad(index, fakeDraftId).url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, onPageLoadRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(POST, onPageLoadRoute)
        .withFormUrlEncodedBody(("value", validAnswer.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }

}
