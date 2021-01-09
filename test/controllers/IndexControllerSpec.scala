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
import models.pages.FullName
import models.pages.IndividualOrBusiness.Individual
import models.pages.Status.Completed
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.living_settlor.{SettlorIndividualOrBusinessPage, individual => individualPages}
import pages.{DeceasedSettlorStatus, LivingSettlorStatus, deceased_settlor => deceasedPages}
import play.api.test.FakeRequest
import play.api.test.Helpers._

import scala.concurrent.Future

class IndexControllerSpec extends SpecBase {

  private val name: FullName = FullName("Joe", None, "Bloggs")

  "Index Controller" must {

    "redirect to add to page if there is at least one Completed living settlor" in {

      val userAnswers = emptyUserAnswers
        .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
        .set(individualPages.SettlorIndividualNamePage(0), name).success.value
        .set(LivingSettlorStatus(0), Completed).success.value

      when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).get mustBe controllers.routes.AddASettlorController.onPageLoad(fakeDraftId).url

      application.stop()
    }

    "redirect to deceased settlor check answers if there is a Completed deceased settlor" in {

      val userAnswers = emptyUserAnswers
        .set(deceasedPages.SettlorsNamePage, name).success.value
        .set(DeceasedSettlorStatus, Completed).success.value

      when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).get mustBe controllers.deceased_settlor.routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId).url

      application.stop()
    }

    "redirect to info page if there are no completed settlors" in {

      val userAnswers = emptyUserAnswers

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(Some(userAnswers)))

      val request = FakeRequest(GET, routes.IndexController.onPageLoad(draftId).url)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).get mustBe controllers.routes.SettlorInfoController.onPageLoad(fakeDraftId).url

      application.stop()
    }
  }
}
