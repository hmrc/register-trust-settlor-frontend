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

package controllers

import org.scalatest.{FreeSpec, MustMatchers, OptionValues}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.test.FakeRequest
import play.api.test.Helpers._

class LogoutControllerSpec extends FreeSpec with MustMatchers with GuiceOneAppPerSuite with OptionValues {

  "logout" - {

    "must start a new session and redirect" in {

      lazy val request = FakeRequest("GET", routes.LogoutController.logout().url)
        .withSession("foo" -> "bar")

      val result = route(app, request).value

      status(result) mustBe SEE_OTHER
      redirectLocation(result).value mustBe "http://localhost:9514/feedback/trusts"
      session(result) mustBe empty
    }
  }
}