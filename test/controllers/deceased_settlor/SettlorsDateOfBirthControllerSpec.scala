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

package controllers.deceased_settlor

import java.time.LocalDate

import base.SpecBase
import controllers.routes._
import forms.deceased_settlor.SettlorsDateOfBirthFormProvider
import models.{NormalMode, UserAnswers}
import models.pages.FullName
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import pages.deceased_settlor.{SettlorDateOfDeathPage, SettlorsDateOfBirthPage, SettlorsNamePage}
import play.api.data.Form
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.FeatureFlagService
import views.html.deceased_settlor.SettlorsDateOfBirthView

import scala.concurrent.Future

class SettlorsDateOfBirthControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new SettlorsDateOfBirthFormProvider(appConfig)
  val dateOfDeath: LocalDate = LocalDate.parse("2019-02-03")
  val form: Form[LocalDate] = formProvider.withConfig()

  val validAnswer: LocalDate = LocalDate.parse("2000-02-03")
  val invalidAnswer: LocalDate = LocalDate.parse("2020-02-03")

  lazy val settlorsDateOfBirthRoute: String = routes.SettlorsDateOfBirthController.onPageLoad(NormalMode, fakeDraftId).url

  val name: FullName = FullName("first name", None, "Last name")

  val baseAnswers: UserAnswers = emptyUserAnswers
    .set(SettlorsNamePage, name).success.value
    .set(SettlorDateOfDeathPage, dateOfDeath).success.value

  "SettlorsDateOfBirth Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request = FakeRequest(GET, settlorsDateOfBirthRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorsDateOfBirthView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode,fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = baseAnswers
        .set(SettlorsDateOfBirthPage, validAnswer).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorsDateOfBirthRoute)

      val view = application.injector.instanceOf[SettlorsDateOfBirthView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), NormalMode,fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val featureFlagService = mock[FeatureFlagService]

      val application =
        applicationBuilder(userAnswers = Some(baseAnswers))
          .overrides(
            bind[FeatureFlagService].toInstance(featureFlagService)
          ).build()

      when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))

      val request =
        FakeRequest(POST, settlorsDateOfBirthRoute)
          .withFormUrlEncodedBody(
            "value.day"   -> validAnswer.getDayOfMonth.toString,
            "value.month" -> validAnswer.getMonthValue.toString,
            "value.year"  -> validAnswer.getYear.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual fakeNavigator.desiredRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, settlorsDateOfBirthRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[SettlorsDateOfBirthView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "return a Bad Request and errors when submitted date is after date of death" in {

      val form: Form[LocalDate] = formProvider.withConfig((dateOfDeath, "afterDateOfDeath"))

      val application = applicationBuilder(userAnswers = Some(baseAnswers)).build()

      val request =
        FakeRequest(POST, settlorsDateOfBirthRoute)
          .withFormUrlEncodedBody(
            "value.day"   -> invalidAnswer.getDayOfMonth.toString,
            "value.month" -> invalidAnswer.getMonthValue.toString,
            "value.year"  -> invalidAnswer.getYear.toString
          )

      val boundForm = form.bind(Map(
        "value.day"   -> invalidAnswer.getDayOfMonth.toString,
        "value.month" -> invalidAnswer.getMonthValue.toString,
        "value.year"  -> invalidAnswer.getYear.toString
      ))

      val view = application.injector.instanceOf[SettlorsDateOfBirthView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode, fakeDraftId, name)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorsDateOfBirthRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorsDateOfBirthRoute)
          .withFormUrlEncodedBody(
            "value.day"   -> validAnswer.getDayOfMonth.toString,
            "value.month" -> validAnswer.getMonthValue.toString,
            "value.year"  -> validAnswer.getYear.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to SettlorNamePage when settlor name is not answered" in {


      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, settlorsDateOfBirthRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorsNameController.onPageLoad(NormalMode, fakeDraftId).url

      application.stop()
    }
  }
}
