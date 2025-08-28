/*
 * Copyright 2025 HM Revenue & Customs
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

package handlers

import base.SpecBase
import play.api.i18n.{Lang, MessagesApi}
import play.api.mvc.RequestHeader
import play.api.test.FakeRequest
import play.twirl.api.Html
import views.html.{ErrorTemplate, PageNotFoundView}

import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

class ErrorHandlerSpec extends SpecBase {

  private val messageApi: MessagesApi        = app.injector.instanceOf[MessagesApi]
  private val errorTemplate: ErrorTemplate   = app.injector.instanceOf[ErrorTemplate]
  private val notFoundView: PageNotFoundView = app.injector.instanceOf[PageNotFoundView]
  val errorHandler: ErrorHandler             = new ErrorHandler(messageApi, errorTemplate, notFoundView)

  "ErrorHandler" when {
    ".notFoundTemplate"      should {
      "Return a not found template" in {
        implicit val request: RequestHeader = FakeRequest()
        val resultFuture: Future[Html]      = errorHandler.notFoundTemplate(request)
        val result                          = Await.result(resultFuture, 5.seconds)
        result.body must include(messageApi("pageNotFound.p1")(Lang("en")))
      }
    }
    ".standardErrorTemplate" should {
      "Return an Error page" in {
        implicit val request: RequestHeader = FakeRequest()
        val resultFuture: Future[Html]      =
          errorHandler.standardErrorTemplate(pageTitle = "pageTitle", heading = "heading", message = "message")
        val result                          = Await.result(resultFuture, 5.seconds)
        result.body must include("pageTitle")
        result.body must include("message")
      }
    }
  }

}
