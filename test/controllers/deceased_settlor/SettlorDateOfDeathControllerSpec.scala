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
import forms.deceased_settlor.SettlorDateOfDeathFormProvider
import models.pages.FullName
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import pages.deceased_settlor.{SettlorDateOfDeathPage, SettlorsDateOfBirthPage, SettlorsNamePage}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.deceased_settlor.SettlorDateOfDeathView

import java.time.{LocalDate, ZoneOffset}
import scala.concurrent.Future

class SettlorDateOfDeathControllerSpec extends SpecBase with MockitoSugar {

  val formProvider = new SettlorDateOfDeathFormProvider(frontendAppConfig)
  val form = formProvider.withConfig()

  val validAnswer = LocalDate.now(ZoneOffset.UTC)

  private val fullName = FullName("first name", None, "Last name")

  lazy val settlorDateOfDeathRoute = routes.SettlorDateOfDeathController.onPageLoad(fakeDraftId).url

  "SettlorDateOfDeath Controller" must {

    "return OK and the correct view for a GET" in {
      val userAnswers = emptyUserAnswers.set(SettlorsNamePage,
        fullName).success.value


      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorDateOfDeathRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[SettlorDateOfDeathView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, fakeDraftId, fullName)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = emptyUserAnswers.set(SettlorDateOfDeathPage, validAnswer)
        .success.value.set(SettlorsNamePage, fullName).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, settlorDateOfDeathRoute)

      val view = application.injector.instanceOf[SettlorDateOfDeathView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(validAnswer), fakeDraftId, fullName)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorDateOfDeathPage, validAnswer)
        .success.value.set(SettlorsNamePage,
        fullName).success.value

      when(registrationsRepository.getTrustSetupDate(any())(any())).thenReturn(Future.successful(Some(validAnswer)))

      val application =
        applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorDateOfDeathRoute)
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


    "redirect to SettlorNamePage when settlor name is not answered" in {


      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, settlorDateOfDeathRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorsNameController.onPageLoad(fakeDraftId).url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val userAnswers = emptyUserAnswers.set(SettlorDateOfDeathPage, validAnswer)
        .success.value.set(SettlorsNamePage,
        fullName).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request =
        FakeRequest(POST, settlorDateOfDeathRoute)
          .withFormUrlEncodedBody(("value", "invalid value"))

      val boundForm = form.bind(Map("value" -> "invalid value"))

      val view = application.injector.instanceOf[SettlorDateOfDeathView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, fakeDraftId, fullName)(request, messages).toString

      application.stop()
    }

    "return a Bad Request and errors when" when {

      "submitted date is after trust start date" in {

        val submittedDate = LocalDate.parse("2020-02-03")

        when(registrationsRepository.getTrustSetupDate(any())(any())).thenReturn(Future.successful(Some(mockedTrustStartDate)))

        val form = formProvider.withConfig((mockedTrustStartDate, "afterTrustStartDate"))

        val userAnswers = emptyUserAnswers
          .set(SettlorsNamePage, fullName).success.value
          .set(SettlorDateOfDeathPage, submittedDate).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request =
          FakeRequest(POST, settlorDateOfDeathRoute)
            .withFormUrlEncodedBody(
              "value.day"   -> submittedDate.getDayOfMonth.toString,
              "value.month" -> submittedDate.getMonthValue.toString,
              "value.year"  -> submittedDate.getYear.toString
            )

        val boundForm = form.bind(Map(
          "value.day"   -> submittedDate.getDayOfMonth.toString,
          "value.month" -> submittedDate.getMonthValue.toString,
          "value.year"  -> submittedDate.getYear.toString
        ))

        val view = application.injector.instanceOf[SettlorDateOfDeathView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId, fullName)(request, messages).toString

        application.stop()
      }

      "submitted date is before date of birth" in {

        val dateOfBirth = LocalDate.parse("2020-02-03")
        val submittedDate = LocalDate.parse("2019-02-03")

        val form = formProvider.withConfig(minimumDate = (dateOfBirth, "beforeDateOfBirth"))

        val userAnswers = emptyUserAnswers
          .set(SettlorsNamePage, fullName).success.value
          .set(SettlorDateOfDeathPage, submittedDate).success.value
          .set(SettlorsDateOfBirthPage, dateOfBirth).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request =
          FakeRequest(POST, settlorDateOfDeathRoute)
            .withFormUrlEncodedBody(
              "value.day"   -> submittedDate.getDayOfMonth.toString,
              "value.month" -> submittedDate.getMonthValue.toString,
              "value.year"  -> submittedDate.getYear.toString
            )

        val boundForm = form.bind(Map(
          "value.day"   -> submittedDate.getDayOfMonth.toString,
          "value.month" -> submittedDate.getMonthValue.toString,
          "value.year"  -> submittedDate.getYear.toString
        ))

        val view = application.injector.instanceOf[SettlorDateOfDeathView]

        val result = route(application, request).value

        status(result) mustEqual BAD_REQUEST

        contentAsString(result) mustEqual
          view(boundForm, fakeDraftId, fullName)(request, messages).toString

        application.stop()
      }
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorDateOfDeathRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, settlorDateOfDeathRoute)
          .withFormUrlEncodedBody(
            "value.day"   -> validAnswer.getDayOfMonth.toString,
            "value.month" -> validAnswer.getMonthValue.toString,
            "value.year"  -> validAnswer.getYear.toString
          )

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad.url

      application.stop()
    }
  }
}
