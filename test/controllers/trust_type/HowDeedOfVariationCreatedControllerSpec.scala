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

package controllers.trust_type

import base.SpecBase
import controllers.routes._
import forms.DeedOfVariationFormProvider
import models.pages.{DeedOfVariation, KindOfTrust}
import pages.trust_type.{HowDeedOfVariationCreatedPage, SetUpByLivingSettlorYesNoPage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.trust_type.HowDeedOfVariationCreatedView

class HowDeedOfVariationCreatedControllerSpec extends SpecBase {

  val index = 0

  lazy val deedOfVariationRoute = routes.HowDeedOfVariationCreatedController.onPageLoad(fakeDraftId).url

  val formProvider = new DeedOfVariationFormProvider()
  val form = formProvider()

  "HowDeedOfVariationCreated Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers.set(SetUpByLivingSettlorYesNoPage, false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, deedOfVariationRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[HowDeedOfVariationCreatedView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(SetUpByLivingSettlorYesNoPage, false).success.value
        .set(HowDeedOfVariationCreatedPage, DeedOfVariation.values.head).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, deedOfVariationRoute)

      val view = application.injector.instanceOf[HowDeedOfVariationCreatedView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(DeedOfVariation.values.head), fakeDraftId)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SetUpByLivingSettlorYesNoPage, false).success.value

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, deedOfVariationRoute)
          .withFormUrlEncodedBody(("value", DeedOfVariation.options.head.value))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SetUpByLivingSettlorYesNoPage, false).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, deedOfVariationRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[HowDeedOfVariationCreatedView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, deedOfVariationRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, deedOfVariationRoute)
          .withFormUrlEncodedBody(("value", KindOfTrust.values.head.toString))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}

