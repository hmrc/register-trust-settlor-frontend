/*
 * Copyright 2021 HM Revenue & Customs
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
import forms.PassportOrIdCardFormProvider
import models.pages.{FullName, PassportOrIdCardDetails}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.living_settlor.individual.{SettlorIndividualDateOfBirthYesNoPage, SettlorIndividualIDCardPage, SettlorIndividualNamePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.TrustsStoreService
import utils._
import utils.countryOptions.CountryOptions
import views.html.living_settlor.individual.SettlorIndividualIDCardView

import java.time.{LocalDate, ZoneOffset}
import scala.concurrent.Future

class SettlorIndividualIDCardControllerSpec extends SpecBase {

  private val formProvider: PassportOrIdCardFormProvider = new PassportOrIdCardFormProvider(frontendAppConfig)
  private val form: Form[PassportOrIdCardDetails] = formProvider("settlorIndividualIDCard")
  private val index: Int = 0
  private val name: FullName = FullName("First", Some("Middle"), "Last")
  private val validAnswer: LocalDate = LocalDate.now(ZoneOffset.UTC)

  private lazy val settlorIndividualIDCardRoute: String = routes.SettlorIndividualIDCardController.onPageLoad(index, fakeDraftId).url


  "SettlorIndividualIDCard Controller" must {

    "return OK and the correct view for a GET" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorIndividualNamePage(index),name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualIDCardRoute)

      val view = application.injector.instanceOf[SettlorIndividualIDCardView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, countryOptions, fakeDraftId, index, name)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorIndividualNamePage(index),name).success.value
        .set(SettlorIndividualIDCardPage(index),
          PassportOrIdCardDetails("Field 1", "Field 2", validAnswer )).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualIDCardRoute)

      val view = application.injector.instanceOf[SettlorIndividualIDCardView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(PassportOrIdCardDetails("Field 1", "Field 2", validAnswer)),
          countryOptions, fakeDraftId, index, name)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val mockFeatureFlagService: TrustsStoreService = mock[TrustsStoreService]
      when(mockFeatureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))

      val userAnswers = emptyUserAnswers
        .set(SettlorIndividualNamePage(index),name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[TrustsStoreService].toInstance(mockFeatureFlagService))
        .build()

      val request =
        FakeRequest(POST, settlorIndividualIDCardRoute)

          .withFormUrlEncodedBody(
            "country" -> "country",
            "number" -> "123456",
            "expiryDate.day"   -> validAnswer.getDayOfMonth.toString,
            "expiryDate.month" -> validAnswer.getMonthValue.toString,
            "expiryDate.year"  -> validAnswer.getYear.toString)
      val result = route(application, request).value


      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "redirect to Settlors Name page when Settlors name is not answered" in {

      val userAnswers = emptyUserAnswers.set(SettlorIndividualDateOfBirthYesNoPage(index), true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorIndividualIDCardRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorIndividualNameController.onPageLoad(index, fakeDraftId).url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorIndividualNamePage(index),name).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorIndividualIDCardRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[SettlorIndividualIDCardView]

      val result = route(application, request).value

      val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptions].options

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, countryOptions , fakeDraftId, index, name)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorIndividualIDCardRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorIndividualIDCardRoute)
          .withFormUrlEncodedBody(("field1", "value 1"), ("field2", "value 2"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }
  }
}
