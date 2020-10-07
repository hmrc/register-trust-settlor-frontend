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

package controllers.actions

import base.SpecBase
import config.FrontendAppConfig
import org.mockito.Matchers.any
import org.mockito.Mockito._
import play.api.mvc.{Action, AnyContent, Results}
import play.api.test.Helpers._
import uk.gov.hmrc.auth.core._
import uk.gov.hmrc.auth.core.retrieve.{Retrieval, ~}

import scala.concurrent.Future

class AffinityGroupIdentifierActionSpec extends SpecBase {

  type RetrievalType = Option[String] ~ Option[AffinityGroup] ~ Enrolments

  val mockAuthConnector: AuthConnector = mock[AuthConnector]
  val appConfig: FrontendAppConfig = injector.instanceOf[FrontendAppConfig]
  val fakeAction: Action[AnyContent] = Action { _ => Results.Ok }

  val utr = "0987654321"

  lazy override val trustsAuth = new TrustsAuthorisedFunctions(mockAuthConnector, appConfig)

  private val noEnrollment = Enrolments(Set())

  private def authRetrievals(affinityGroup: AffinityGroup, enrolment: Enrolments): Future[Some[String] ~ Some[AffinityGroup] ~ Enrolments] =
    Future.successful(new ~(new ~(Some("id"), Some(affinityGroup)), enrolment))

  private val agentEnrolment = Enrolments(Set(Enrolment("HMRC-AS-AGENT", List(EnrolmentIdentifier("AgentReferenceNumber", "SomeVal")), "Activated", None)))
  private val trustsEnrolment = Enrolments(Set(Enrolment("HMRC-TERS-ORG", List(EnrolmentIdentifier("SAUTR", utr)), "Activated", None)))


  "invoking an AuthenticatedIdentifier" when {
    "an Agent user" when {
      "hasn't enrolled an Agent Services Account" must {
        "redirect the user to the create agent services page" in {

          val application = applicationBuilder(userAnswers = None).build()

          when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
            .thenReturn(authRetrievals(AffinityGroup.Agent, noEnrollment))

          val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig).apply(fakeRequest)

          status(result) mustBe SEE_OTHER
          redirectLocation(result) mustBe Some(appConfig.createAgentServicesAccountUrl)
          application.stop()
        }
      }

      "has correct enrolled in Agent Services Account" must {
        "allow user to continue" in {

          val application = applicationBuilder(userAnswers = None).build()

          when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
            .thenReturn(authRetrievals(AffinityGroup.Agent, agentEnrolment))

          val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig).apply(fakeRequest)

          status(result) mustBe OK
          application.stop()
        }
      }
    }

    "an Org user" when {
      "with no enrolments" must {
        "allow user to continue" in {

          val application = applicationBuilder(userAnswers = None).build()

          when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
            .thenReturn(authRetrievals(AffinityGroup.Organisation, agentEnrolment))

          val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig).apply(fakeRequest)

          status(result) mustBe OK
          application.stop()
        }
      }
      "with trusts enrolments" must {
        "redirect to maintain-a-trust" in {

          val application = applicationBuilder(userAnswers = None).build()

          when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
            .thenReturn(authRetrievals(AffinityGroup.Organisation, trustsEnrolment))

          val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig).apply(fakeRequest)

          status(result) mustBe SEE_OTHER
          redirectLocation(result).value mustBe s"${appConfig.maintainATrustFrontendUrl}"
          application.stop()
        }
      }
    }

    "and Individual user" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any()))
          .thenReturn(authRetrievals(AffinityGroup.Individual, noEnrollment))

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig).apply(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)

        application.stop()
      }
    }

    "the user hasn't logged in" must {
      "redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed MissingBearerToken())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
        application.stop()
      }
    }

    "the user's session has expired" must {
      "redirect the user to log in " in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed BearerTokenExpired())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result).get must startWith(frontendAppConfig.loginUrl)
        application.stop()
      }
    }

    "the user doesn't have sufficient enrolments" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed InsufficientEnrolments())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }

    "the user doesn't have sufficient confidence level" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed InsufficientConfidenceLevel())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }

    "the user used an unaccepted auth provider" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed UnsupportedAuthProvider())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }

    "the user has an unsupported affinity group" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed UnsupportedAffinityGroup())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }

    "the user has an unsupported credential role" must {
      "redirect the user to the unauthorised page" in {

        val application = applicationBuilder(userAnswers = None).build()

        when(mockAuthConnector.authorise(any(), any[Retrieval[RetrievalType]]())(any(), any())) thenReturn (Future failed UnsupportedCredentialRole())

        val result = new AffinityGroupIdentifierAction(fakeAction, trustsAuth, appConfig).apply(fakeRequest)

        status(result) mustBe SEE_OTHER

        redirectLocation(result) mustBe Some(controllers.routes.UnauthorisedController.onPageLoad().url)
        application.stop()
      }
    }
  }
}

