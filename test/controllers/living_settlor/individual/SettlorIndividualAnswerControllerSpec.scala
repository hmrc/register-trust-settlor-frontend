/*
 * Copyright 2020 HM Revenue & Customs
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

import java.time.{LocalDate, ZoneOffset}

import base.SpecBase
import controllers.routes._
import models.pages._
import models.{NormalMode, UserAnswers}
import org.mockito.Matchers._
import org.mockito.Mockito._
import pages.living_settlor._
import pages.living_settlor.individual._
import pages.trust_type.{SetUpAfterSettlorDiedYesNoPage, _}
import play.api.Application
import play.api.mvc.{Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.HttpResponse
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.living_settlor.SettlorAnswersView

import scala.concurrent.Future

class SettlorIndividualAnswerControllerSpec extends SpecBase {

  private val settlorName: FullName = FullName("first name", Some("middle name"), "last name")
  private val validDate: LocalDate = LocalDate.now(ZoneOffset.UTC)
  private val nino: String = "CC123456A"
  private val AddressUK: UKAddress = UKAddress("line 1", "line 2", Some("line 3"), Some("line 4"), "line 5")
  private val AddressInternational: InternationalAddress = InternationalAddress("line 1", "line 2", Some("line 3"), "ES")
  private val passportOrIDCardDetails: PassportOrIdCardDetails = PassportOrIdCardDetails("Field 1", "Field 2", LocalDate.now(ZoneOffset.UTC))
  private val index: Int = 0

  private lazy val settlorIndividualAnswerRoute: String = routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId).url
  private lazy val onSubmit: Call = routes.SettlorIndividualAnswerController.onSubmit(index, fakeDraftId)

  "SettlorIndividualAnswer Controller" must {

    "settlor individual" when {

      "no date of birth, no nino, no address" must {

        "return OK and the correct view for a GET" in {

          val userAnswers: UserAnswers =
            emptyUserAnswers
              .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
              .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
              .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill).success.value
              .set(HoldoverReliefYesNoPage, false).success.value
              .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
              .set(SettlorIndividualNamePage(index), settlorName).success.value
              .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
              .set(SettlorIndividualNINOYesNoPage(index), false).success.value
              .set(SettlorAddressYesNoPage(index), false).success.value

          val countryOptions: CountryOptions = injector.instanceOf[CountryOptions]
          val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = true)

          val expectedSections = Seq(
            AnswerSection(
              None,
              checkYourAnswersHelper.settlorIndividualQuestions(index)
            )
          )

          val application: Application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, settlorIndividualAnswerRoute)

          val result: Future[Result] = route(application, request).value

          val view: SettlorAnswersView = application.injector.instanceOf[SettlorAnswersView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(onSubmit, expectedSections)(request, messages).toString

          application.stop()
        }

      }

      "with date of birth, with nino, and no address" must {

        "return OK and the correct view for a GET" in {

          val userAnswers: UserAnswers =
            emptyUserAnswers
              .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
              .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
              .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplaceAbsolute).success.value
              .set(HoldoverReliefYesNoPage, false).success.value
              .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
              .set(SettlorIndividualNamePage(index), settlorName).success.value
              .set(SettlorIndividualDateOfBirthYesNoPage(index), true).success.value
              .set(SettlorIndividualDateOfBirthPage(index), validDate).success.value
              .set(SettlorIndividualNINOYesNoPage(index), true).success.value
              .set(SettlorIndividualNINOPage(index), nino).success.value
              .set(SettlorAddressYesNoPage(index), false).success.value

          val countryOptions = injector.instanceOf[CountryOptions]
          val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = true)

          val expectedSections = Seq(
            AnswerSection(
              None,
              checkYourAnswersHelper.settlorIndividualQuestions(index)
            )
          )

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, settlorIndividualAnswerRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[SettlorAnswersView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(onSubmit, expectedSections)(request, messages).toString

          application.stop()
        }

      }

      "no date of birth, no nino, UK address, no passport, no ID card" must {

        "return OK and the correct view for a GET" in {

          val userAnswers =
            emptyUserAnswers
              .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
              .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
              .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill).success.value
              .set(HoldoverReliefYesNoPage, false).success.value
              .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
              .set(SettlorIndividualNamePage(index), settlorName).success.value
              .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
              .set(SettlorIndividualNINOYesNoPage(index), false).success.value
              .set(SettlorAddressYesNoPage(index), true).success.value
              .set(SettlorAddressUKYesNoPage(index), true).success.value
              .set(SettlorAddressUKPage(index), AddressUK).success.value
              .set(SettlorIndividualPassportYesNoPage(index), false).success.value
              .set(SettlorIndividualIDCardYesNoPage(index), false).success.value

          val countryOptions = injector.instanceOf[CountryOptions]
          val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = true)

          val expectedSections = Seq(
            AnswerSection(
              None,
              checkYourAnswersHelper.settlorIndividualQuestions(index)
            )
          )

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, settlorIndividualAnswerRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[SettlorAnswersView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(onSubmit, expectedSections)(request, messages).toString

          application.stop()
        }

      }

      "no date of birth, no nino, International address, no passport, no ID card" must {

        "return OK and the correct view for a GET" in {

          val userAnswers =
            emptyUserAnswers
              .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
              .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
              .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill).success.value
              .set(HoldoverReliefYesNoPage, false).success.value
              .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
              .set(SettlorIndividualNamePage(index), settlorName).success.value
              .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
              .set(SettlorIndividualNINOYesNoPage(index), false).success.value
              .set(SettlorAddressYesNoPage(index), true).success.value
              .set(SettlorAddressUKYesNoPage(index), false).success.value
              .set(SettlorAddressInternationalPage(index), AddressInternational).success.value
              .set(SettlorIndividualPassportYesNoPage(index), false).success.value
              .set(SettlorIndividualIDCardYesNoPage(index), false).success.value

          val countryOptions = injector.instanceOf[CountryOptions]
          val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = true)

          val expectedSections = Seq(
            AnswerSection(
              None,
              checkYourAnswersHelper.settlorIndividualQuestions(index)
            )
          )

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, settlorIndividualAnswerRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[SettlorAnswersView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(onSubmit, expectedSections)(request, messages).toString

          application.stop()
        }

      }

      "no date of birth, no nino, UK address, passport, ID card" must {

        "return OK and the correct view for a GET" in {

          val userAnswers =
            emptyUserAnswers
              .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
              .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
              .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill).success.value
              .set(HoldoverReliefYesNoPage, false).success.value
              .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
              .set(SettlorIndividualNamePage(index), settlorName).success.value
              .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
              .set(SettlorIndividualNINOYesNoPage(index), false).success.value
              .set(SettlorAddressYesNoPage(index), true).success.value
              .set(SettlorAddressUKYesNoPage(index), true).success.value
              .set(SettlorAddressUKPage(index), AddressUK).success.value
              .set(SettlorIndividualPassportYesNoPage(index), true).success.value
              .set(SettlorIndividualPassportPage(index), passportOrIDCardDetails).success.value
              .set(SettlorIndividualIDCardYesNoPage(index), true).success.value
              .set(SettlorIndividualIDCardPage(index), passportOrIDCardDetails).success.value

          val countryOptions = injector.instanceOf[CountryOptions]
          val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = true)

          val expectedSections = Seq(
            AnswerSection(
              None,
              checkYourAnswersHelper.settlorIndividualQuestions(index)
            )
          )

          val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

          val request = FakeRequest(GET, settlorIndividualAnswerRoute)

          val result = route(application, request).value

          val view = application.injector.instanceOf[SettlorAnswersView]

          status(result) mustEqual OK

          contentAsString(result) mustEqual
            view(onSubmit, expectedSections)(request, messages).toString

          application.stop()
        }

      }

    }

    "redirect to SettlorIndividualOrBusinessPage on a GET if no answer for 'Is the settlor an individual or business?' at index" in {

      val answers =
        emptyUserAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
          .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill).success.value
          .set(HoldoverReliefYesNoPage, false).success.value
          .set(SettlorIndividualNamePage(index), settlorName).success.value
          .set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
          .set(SettlorIndividualNINOYesNoPage(index), false).success.value
          .set(SettlorAddressYesNoPage(index), false).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, settlorIndividualAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual
        controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId).url

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None).build()

      val request = FakeRequest(GET, settlorIndividualAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustEqual SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "update beneficiary status when kindOfTrustPage is set to Employees" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
        .set(KindOfTrustPage, KindOfTrust.Employees).success.value

      when(mockCreateDraftRegistrationService.setBeneficiaryStatus(any())(any()))
        .thenReturn(Future.successful(true))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, settlorIndividualAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe fakeNavigator.desiredRoute.url

      verify(mockCreateDraftRegistrationService)
        .setBeneficiaryStatus(any())(any())

      application.stop()
    }

    "remove role in company answers when kindOfTrustPage is not set to Employees" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
        .set(KindOfTrustPage, KindOfTrust.Deed).success.value

      when(mockCreateDraftRegistrationService.removeRoleInCompanyAnswers(any())(any()))
        .thenReturn(Future.successful(HttpResponse(OK)))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(POST, settlorIndividualAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER
      redirectLocation(result).value mustBe fakeNavigator.desiredRoute.url

      verify(mockCreateDraftRegistrationService).removeRoleInCompanyAnswers(any())(any())

      application.stop()
    }

  }
}
