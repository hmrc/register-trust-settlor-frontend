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

package controllers

import base.SpecBase
import connectors.SubmissionDraftConnector
import models.UserAnswers
import models.pages.FullName
import models.pages.IndividualOrBusiness.Individual
import models.pages.Status.{Completed, InProgress}
import org.mockito.ArgumentCaptor
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, verify, when}
import pages.living_settlor.{SettlorIndividualOrBusinessPage, individual => individualPages}
import pages.{DeceasedSettlorStatus, LivingSettlorStatus, deceased_settlor => deceasedPages}
import play.api.inject.bind
import play.api.test.FakeRequest
import play.api.test.Helpers._
import services.FeatureFlagService

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {

  private val name: FullName = FullName("Joe", None, "Bloggs")
  private val utr: String = "1234567890"

  private val featureFlagService: FeatureFlagService = mock[FeatureFlagService]
  private val submissionDraftConnector: SubmissionDraftConnector = mock[SubmissionDraftConnector]

  "Index Controller" when {

    "pre-existing user answers" must {

      "redirect to add to page" when {
        "there is at least one settlor" when {

          "in progress" in {

            val userAnswers = emptyUserAnswers
              .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
              .set(individualPages.SettlorIndividualNamePage(0), name).success.value
              .set(LivingSettlorStatus(0), InProgress).success.value

            val application = applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
              .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
              .build()

            when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
            when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))
            when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))
            when(submissionDraftConnector.getTrustUtr(any())(any(), any())).thenReturn(Future.successful(None))

            val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).get mustBe controllers.routes.AddASettlorController.onPageLoad(fakeDraftId).url

            application.stop()
          }

          "completed" in {

            val userAnswers = emptyUserAnswers
              .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
              .set(individualPages.SettlorIndividualNamePage(0), name).success.value
              .set(LivingSettlorStatus(0), Completed).success.value

            val application = applicationBuilder(userAnswers = Some(userAnswers))
              .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
              .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
              .build()

            when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
            when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))
            when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))
            when(submissionDraftConnector.getTrustUtr(any())(any(), any())).thenReturn(Future.successful(None))

            val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

            val result = route(application, request).value

            status(result) mustEqual SEE_OTHER

            redirectLocation(result).get mustBe controllers.routes.AddASettlorController.onPageLoad(fakeDraftId).url

            application.stop()
          }
        }
      }

      "redirect to deceased settlor check answers if there is a Completed deceased settlor" in {

        val userAnswers = emptyUserAnswers
          .set(deceasedPages.SettlorsNamePage, name).success.value
          .set(DeceasedSettlorStatus, Completed).success.value

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
          .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
          .build()

        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
        when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))
        when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))
        when(submissionDraftConnector.getTrustUtr(any())(any(), any())).thenReturn(Future.successful(None))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        status(result) mustEqual SEE_OTHER

        redirectLocation(result).get mustBe controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId).url

        application.stop()
      }

      "redirect to info page" when {

        "there is an in-progress deceased settlor" in {

          val userAnswers = emptyUserAnswers
            .set(deceasedPages.SettlorsNamePage, name).success.value
            .set(DeceasedSettlorStatus, InProgress).success.value

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
            .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
            .build()

          when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
          when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))
          when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))
          when(submissionDraftConnector.getTrustUtr(any())(any(), any())).thenReturn(Future.successful(None))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).get mustBe controllers.routes.SettlorInfoController.onPageLoad(fakeDraftId).url

          application.stop()
        }

        "there are no settlors" in {

          val userAnswers = emptyUserAnswers

          val application = applicationBuilder(userAnswers = Some(userAnswers))
            .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
            .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
            .build()

          when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
          when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))
          when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))
          when(submissionDraftConnector.getTrustUtr(any())(any(), any())).thenReturn(Future.successful(None))

          val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

          val result = route(application, request).value

          status(result) mustEqual SEE_OTHER

          redirectLocation(result).get mustBe controllers.routes.SettlorInfoController.onPageLoad(fakeDraftId).url

          application.stop()
        }
      }

      "update value of is5mldEnabled and isTaxable in user answers" in {

        reset(registrationsRepository)

        val userAnswers = emptyUserAnswers.copy(is5mldEnabled = false, isTaxable = false)

        val application = applicationBuilder(userAnswers = Some(userAnswers))
          .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
          .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
          .build()

        when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))
        when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
        when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(true))
        when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))
        when(submissionDraftConnector.getTrustUtr(any())(any(), any())).thenReturn(Future.successful(Some(utr)))

        val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

        route(application, request).value.map { _ =>
          val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
          verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

          uaCaptor.getValue.is5mldEnabled mustBe true
          uaCaptor.getValue.isTaxable mustBe true
          uaCaptor.getValue.existingTrustUtr.get mustBe utr

          application.stop()
        }
      }
    }

    "no pre-existing user answers" must {
      "instantiate new set of user answers" when {

        "5mld enabled" when {

          "taxable" must {
            "add is5mldEnabled = true and isTaxable = true to user answers" in {

              reset(registrationsRepository)

              val application = applicationBuilder(userAnswers = None)
                .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
                .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
                .build()

              when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))
              when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
              when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(true))
              when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))
              when(submissionDraftConnector.getTrustUtr(any())(any(), any())).thenReturn(Future.successful(Some(utr)))

              val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

              route(application, request).value.map { _ =>
                val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
                verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

                uaCaptor.getValue.is5mldEnabled mustBe true
                uaCaptor.getValue.isTaxable mustBe true
                uaCaptor.getValue.existingTrustUtr.get mustBe utr
                uaCaptor.getValue.draftId mustBe fakeDraftId
                uaCaptor.getValue.internalAuthId mustBe "id"

                application.stop()
              }
            }
          }

          "non-taxable" must {
            "add is5mldEnabled = true and isTaxable = false to user answers" in {

              reset(registrationsRepository)

              val application = applicationBuilder(userAnswers = None)
                .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
                .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
                .build()

              when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))
              when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
              when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(true))
              when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(false))
              when(submissionDraftConnector.getTrustUtr(any())(any(), any())).thenReturn(Future.successful(Some(utr)))

              val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

              route(application, request).value.map { _ =>
                val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
                verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

                uaCaptor.getValue.is5mldEnabled mustBe true
                uaCaptor.getValue.isTaxable mustBe false
                uaCaptor.getValue.existingTrustUtr.get mustBe utr
                uaCaptor.getValue.draftId mustBe fakeDraftId
                uaCaptor.getValue.internalAuthId mustBe "id"

                application.stop()
              }
            }
          }
        }

        "5mld not enabled" when {

          "taxable" must {
            "add is5mldEnabled = false value to user answers" in {

              reset(registrationsRepository)

              val application = applicationBuilder(userAnswers = None)
                .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
                .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
                .build()

              when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))
              when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
              when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))
              when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(true))
              when(submissionDraftConnector.getTrustUtr(any())(any(), any())).thenReturn(Future.successful(Some(utr)))

              val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

              route(application, request).value.map { _ =>
                val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
                verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

                uaCaptor.getValue.is5mldEnabled mustBe false
                uaCaptor.getValue.isTaxable mustBe true
                uaCaptor.getValue.existingTrustUtr.get mustBe utr
                uaCaptor.getValue.draftId mustBe fakeDraftId
                uaCaptor.getValue.internalAuthId mustBe "id"

                application.stop()
              }
            }
          }

          "non-taxable" must {
            "add is5mldEnabled = false value to user answers" in {

              reset(registrationsRepository)

              val application = applicationBuilder(userAnswers = None)
                .overrides(bind[FeatureFlagService].toInstance(featureFlagService))
                .overrides(bind[SubmissionDraftConnector].toInstance(submissionDraftConnector))
                .build()

              when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))
              when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))
              when(featureFlagService.is5mldEnabled()(any(), any())).thenReturn(Future.successful(false))
              when(submissionDraftConnector.getIsTrustTaxable(any())(any(), any())).thenReturn(Future.successful(false))
              when(submissionDraftConnector.getTrustUtr(any())(any(), any())).thenReturn(Future.successful(Some(utr)))

              val request = FakeRequest(GET, routes.IndexController.onPageLoad(fakeDraftId).url)

              route(application, request).value.map { _ =>
                val uaCaptor = ArgumentCaptor.forClass(classOf[UserAnswers])
                verify(registrationsRepository).set(uaCaptor.capture)(any(), any())

                uaCaptor.getValue.is5mldEnabled mustBe false
                uaCaptor.getValue.isTaxable mustBe false
                uaCaptor.getValue.existingTrustUtr.get mustBe utr
                uaCaptor.getValue.draftId mustBe fakeDraftId
                uaCaptor.getValue.internalAuthId mustBe "id"

                application.stop()
              }
            }
          }
        }
      }
    }
  }
}
