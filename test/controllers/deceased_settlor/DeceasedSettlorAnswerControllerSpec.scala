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

import base.SpecBase
import controllers.routes._
import models.pages.{FullName, InternationalAddress, UKAddress}
import models.{NormalMode, UserAnswers}
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, verify, when}
import pages.deceased_settlor._
import pages.trust_type.SetUpAfterSettlorDiedYesNoPage
import play.api.Application
import play.api.mvc.Result
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import utils.{CheckAnswersFormatters, CheckYourAnswersHelper}
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.deceased_settlor.DeceasedSettlorAnswerView

import java.time.LocalDate
import scala.concurrent.Future

class DeceasedSettlorAnswerControllerSpec extends SpecBase {

  private val checkAnswersFormatters = injector.instanceOf[CheckAnswersFormatters]

  "DeceasedSettlorAnswer Controller" must {

    lazy val deceasedSettlorsAnswerRoute = routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId).url

    "return OK and the correct view for a GET (UK National)" in {

      val answers: UserAnswers =
        emptyUserAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
          .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
          .set(SettlorDateOfDeathYesNoPage, true).success.value
          .set(SettlorDateOfDeathPage, LocalDate.now).success.value
          .set(SettlorDateOfBirthYesNoPage, true).success.value
          .set(SettlorsDateOfBirthPage, LocalDate.now).success.value
          .set(SettlorsNationalInsuranceYesNoPage, true).success.value
          .set(SettlorNationalInsuranceNumberPage, "AB123456C").success.value
          .set(SettlorsLastKnownAddressYesNoPage, true).success.value
          .set(WasSettlorsAddressUKYesNoPage, true).success.value
          .set(SettlorsUKAddressPage, UKAddress("Line1", "Line2", None, Some("TownOrCity"), "NE62RT")).success.value

      val countryOptions: CountryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(answers, fakeDraftId, canEdit = true)

      val expectedSections = Seq(
        AnswerSection(
          None,
          checkYourAnswersHelper.deceasedSettlorQuestions
        )
      )

      val application: Application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId).url)

      val result: Future[Result] = route(application, request).value

      val view: DeceasedSettlorAnswerView = application.injector.instanceOf[DeceasedSettlorAnswerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId, expectedSections)(request, messages).toString

      application.stop()
    }

    "return OK and the correct view for a GET (Non-UK National)" in {

      val answers =
        emptyUserAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
          .set(SettlorsNamePage, FullName("First", None, "Last")).success.value
          .set(SettlorDateOfDeathYesNoPage, true).success.value
          .set(SettlorDateOfDeathPage, LocalDate.now).success.value
          .set(SettlorDateOfBirthYesNoPage, true).success.value
          .set(SettlorsDateOfBirthPage, LocalDate.now).success.value
          .set(SettlorsNationalInsuranceYesNoPage, true).success.value
          .set(SettlorNationalInsuranceNumberPage, "AB123456C").success.value
          .set(SettlorsLastKnownAddressYesNoPage, true).success.value
          .set(WasSettlorsAddressUKYesNoPage, false).success.value
          .set(SettlorsInternationalAddressPage, InternationalAddress("Line1", "Line2", None, "Country")).success.value

      val countryOptions = injector.instanceOf[CountryOptions]

      val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(answers, fakeDraftId, canEdit = true)

      val expectedSections = Seq(
        AnswerSection(
          None,
          checkYourAnswersHelper.deceasedSettlorQuestions
        )
      )

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId).url)

      val result = route(application, request).value

      val view = application.injector.instanceOf[DeceasedSettlorAnswerView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(fakeDraftId, expectedSections)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, deceasedSettlorsAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request =
        FakeRequest(POST, deceasedSettlorsAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to SettlorNamePage when settlor name is not answered" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers)).build()

      val request = FakeRequest(GET, deceasedSettlorsAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual routes.SettlorsNameController.onPageLoad(NormalMode, fakeDraftId).url

      application.stop()
    }

    "remove living settlors mapped piece for a POST" in {

      reset(mockCreateDraftRegistrationService)

      val userAnswers: UserAnswers = emptyUserAnswers
        .set(SettlorsNamePage, FullName("First", None, "Last")).success.value

      when(mockCreateDraftRegistrationService.removeLivingSettlorsMappedPiece(any())(any()))
        .thenReturn(Future.successful(HttpResponse(OK, "")))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, deceasedSettlorsAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe fakeNavigator.desiredRoute.url

      verify(mockCreateDraftRegistrationService).removeLivingSettlorsMappedPiece(any())(any())

      application.stop()
    }
  }
}
