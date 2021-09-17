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
import play.api.test.FakeRequest
import play.api.test.Helpers._
import views.html.SettlorInfoView

class SettlorInfoControllerSpec extends SpecBase {

  "SettlorInfo Controller" must {

    "return OK and the correct view for a GET" when {

      "taxable" in {

        val userAnswers = emptyUserAnswers.copy(isTaxable = true)

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, routes.SettlorInfoController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SettlorInfoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId, isTaxable = true)(request, messages).toString

        application.stop()
      }

      "non-taxable" in {

        val userAnswers = emptyUserAnswers.copy(isTaxable = false)

        val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

        val request = FakeRequest(GET, routes.SettlorInfoController.onPageLoad(fakeDraftId).url)

        val result = route(application, request).value

        val view = application.injector.instanceOf[SettlorInfoView]

        status(result) mustEqual OK

        contentAsString(result) mustEqual
          view(fakeDraftId, isTaxable = false)(request, messages).toString

        application.stop()
      }
    }
  }
}
