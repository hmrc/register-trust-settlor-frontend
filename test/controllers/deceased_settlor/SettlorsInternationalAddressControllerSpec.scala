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

package controllers.deceased_settlor

import base.SpecBase
import controllers.routes._
import forms.InternationalAddressFormProvider
import models.UserAnswers
import models.pages.{FullName, InternationalAddress}
import pages.deceased_settlor.{SettlorsInternationalAddressPage, SettlorsNamePage}
import play.api.Application
import play.api.data.Form
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.html.deceased_settlor.SettlorsInternationalAddressView

import scala.concurrent.Future

class SettlorsInternationalAddressControllerSpec extends SpecBase {

  val formProvider = new InternationalAddressFormProvider()
  val form: Form[InternationalAddress] = formProvider()

  lazy val settlorsInternationalAddressRoute: String = routes.SettlorsInternationalAddressController.onPageLoad(fakeDraftId).url

  val name = FullName("first name", None, "Last name")

  "SettlorsInternationalAddress Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers: UserAnswers = emptyUserAnswers.set(SettlorsNamePage,
        name).success.value

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorsInternationalAddressRoute)

      val view: SettlorsInternationalAddressView = application.injector.instanceOf[SettlorsInternationalAddressView]

      val result: Future[Result] = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options()

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOptions, fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorsInternationalAddressPage, InternationalAddress("line 1", "line 2", None, "country")).success.value.set(SettlorsNamePage,
        name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorsInternationalAddressRoute)

      val view = application.injector.instanceOf[SettlorsInternationalAddressView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options()

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(InternationalAddress("line 1", "line 2", None, "country")), countryOptions, fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorsNamePage,
        name).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorsInternationalAddressRoute)
          .withFormUrlEncodedBody(("line1", "value 1"), ("line2", "value 2"), ("country", "IN"))

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
        FakeRequest(POST, settlorsInternationalAddressRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[SettlorsInternationalAddressView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options()

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOptions, fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorsInternationalAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorsInternationalAddressRoute)
          .withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to SettlorNamePage when settlor name is not answered" in {


      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, settlorsInternationalAddressRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorsNameController.onPageLoad(fakeDraftId).url

      application.stop()
    }
  }
}
