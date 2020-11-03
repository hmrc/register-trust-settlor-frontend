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

package controllers.living_settlor.business

import java.time.{LocalDate, ZoneOffset}

import base.SpecBase
import controllers.living_settlor.routes.SettlorIndividualOrBusinessController
import controllers.routes._
import models.pages._
import models.{NormalMode, UserAnswers}
import pages.living_settlor._
import pages.living_settlor.business._
import pages.trust_type.{SetUpAfterSettlorDiedYesNoPage, _}
import play.api.Application
import play.api.mvc.{Call, Result}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.living_settlor.SettlorAnswersView

import scala.concurrent.Future

class SettlorBusinessAnswerControllerSpec extends SpecBase {

  def onwardRoute = Call("GET", "/foo")

  val settlorName = "Settlor Org"
  val validDate: LocalDate = LocalDate.now(ZoneOffset.UTC)
  val utr: String = "0987654321"
  val addressUK = UKAddress("line 1", "line 2", Some("line 3"), Some("line 4"), "line 5")
  val addressInternational = InternationalAddress("line 1", "line 2", Some("line 3"), "ES")
  val passportOrIDCardDetails = PassportOrIdCardDetails("Field 1", "Field 2", LocalDate.now(ZoneOffset.UTC))
  val index: Int = 0

  lazy val settlorIndividualAnswerRoute: String = routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId).url
  lazy val onSubmit: Call = routes.SettlorBusinessAnswerController.onSubmit(index, fakeDraftId)

  "SettlorBusinessAnswer Controller" must {

    "settlor business -  no utr, no address" must {

      "return OK and the correct view for a GET" in {

        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
            .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill).success.value
            .set(HoldoverReliefYesNoPage, false).success.value
            .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(SettlorBusinessNamePage(index), settlorName).success.value
            .set(SettlorBusinessUtrYesNoPage(index), false).success.value
            .set(SettlorBusinessAddressYesNoPage(index), false).success.value

        val countryOptions: CountryOptions = injector.instanceOf[CountryOptions]
        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = true)

        val expectedSections = Seq(
          AnswerSection(
            None,
            checkYourAnswersHelper.settlorBusinessQuestions(index)
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

    "settlor business -  with utr, and no address" must {

      "return OK and the correct view for a GET" in {

        val userAnswers: UserAnswers =
          emptyUserAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
            .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplaceAbsolute).success.value
            .set(HoldoverReliefYesNoPage, false).success.value
            .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(SettlorBusinessNamePage(index), settlorName).success.value
            .set(SettlorBusinessUtrYesNoPage(index), true).success.value
            .set(SettlorBusinessUtrPage(index), utr).success.value
            .set(SettlorBusinessAddressYesNoPage(index), false).success.value

        val countryOptions = injector.instanceOf[CountryOptions]
        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = true)

        val expectedSections = Seq(
          AnswerSection(
            None,
            checkYourAnswersHelper.settlorBusinessQuestions(index)
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

    "settlor business -  no utr, UK address" must {

      "return OK and the correct view for a GET" in {

        val userAnswers =
          emptyUserAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
            .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill).success.value
            .set(HoldoverReliefYesNoPage, false).success.value
            .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(SettlorBusinessNamePage(index), settlorName).success.value
            .set(SettlorBusinessUtrYesNoPage(index), false).success.value
            .set(SettlorBusinessAddressYesNoPage(index), true).success.value
            .set(SettlorBusinessAddressUKYesNoPage(index), true).success.value
            .set(SettlorBusinessAddressUKPage(index), addressUK).success.value

        val countryOptions = injector.instanceOf[CountryOptions]
        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = true)

        val expectedSections = Seq(
          AnswerSection(
            None,
            checkYourAnswersHelper.settlorBusinessQuestions(index)
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

    "settlor business -  no utr, International address" must {

      "return OK and the correct view for a GET" in {

        val userAnswers =
          emptyUserAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
            .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill).success.value
            .set(HoldoverReliefYesNoPage, false).success.value
            .set(SettlorIndividualOrBusinessPage(index), IndividualOrBusiness.Individual).success.value
            .set(SettlorBusinessNamePage(index), settlorName).success.value
            .set(SettlorBusinessUtrYesNoPage(index), false).success.value
            .set(SettlorBusinessAddressYesNoPage(index), true).success.value
            .set(SettlorBusinessAddressUKYesNoPage(index), false).success.value
            .set(SettlorBusinessAddressInternationalPage(index), addressInternational).success.value

        val countryOptions = injector.instanceOf[CountryOptions]
        val checkYourAnswersHelper = new CheckYourAnswersHelper(countryOptions)(userAnswers, fakeDraftId, canEdit = true)

        val expectedSections = Seq(
          AnswerSection(
            None,
            checkYourAnswersHelper.settlorBusinessQuestions(index)
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

    "redirect to SettlorIndividualOrBusinessPage on a GET if no answer for 'Is the settlor an individual or business?' at index" in {
      val answers =
        emptyUserAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
          .set(HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill).success.value
          .set(HoldoverReliefYesNoPage, false).success.value
          .set(SettlorBusinessNamePage(index), settlorName).success.value
          .set(SettlorBusinessUtrYesNoPage(index), false).success.value
          .set(SettlorBusinessAddressYesNoPage(index), false).success.value

      val application = applicationBuilder(userAnswers = Some(answers)).build()

      val request = FakeRequest(GET, settlorIndividualAnswerRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, fakeDraftId).url

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

  }
}
