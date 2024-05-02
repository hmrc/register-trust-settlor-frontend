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

package controllers.living_settlor.business.mld5

import base.SpecBase
import forms.YesNoFormProvider
import org.mockito.MockitoSugar
import pages.living_settlor.business.SettlorBusinessNamePage
import pages.living_settlor.business.mld5.CountryOfResidenceYesNoPage
import play.api.data.Form
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.living_settlor.business.mld5.CountryOfResidenceYesNoView

class CountryOfResidenceYesNoControllerSpec extends SpecBase with MockitoSugar {

  val formProvider        = new YesNoFormProvider()
  val form: Form[Boolean] = formProvider.withPrefix("settlorBusiness.5mld.countryOfResidenceYesNo")
  val index: Int          = 0
  val businessName        = "Test"

  lazy val countryOfResidenceYesNo: String = routes.CountryOfResidenceYesNoController.onPageLoad(index, draftId).url

  "CountryOfResidenceYesNo Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorBusinessNamePage(index), businessName)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, countryOfResidenceYesNo)

      val result = route(application, request).value

      val view = application.injector.instanceOf[CountryOfResidenceYesNoView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, draftId, index, businessName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorBusinessNamePage(index), businessName)
        .success
        .value
        .set(CountryOfResidenceYesNoPage(index), true)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, countryOfResidenceYesNo)

      val view = application.injector.instanceOf[CountryOfResidenceYesNoView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), draftId, index, businessName)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorBusinessNamePage(index), businessName)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, countryOfResidenceYesNo)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorBusinessNamePage(index), businessName)
        .success
        .value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, countryOfResidenceYesNo)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[CountryOfResidenceYesNoView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, draftId, index, businessName)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, countryOfResidenceYesNo)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, countryOfResidenceYesNo)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
