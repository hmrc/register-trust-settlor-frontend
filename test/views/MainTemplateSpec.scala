/*
 * Copyright 2026 HM Revenue & Customs
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

package views

import play.twirl.api.Html
import views.html.MainTemplate

import scala.jdk.CollectionConverters._

class MainTemplateSpec extends ViewSpecBase {

  private val view: MainTemplate = injector.instanceOf[MainTemplate]

  private val mainContent: Html = Html("""<p class="govuk-body">Test content</p>""")

  private def render(): Html = view("Test page")(mainContent)(fakeRequest, messages)

  "MainTemplate" must {

    "render the service navigation component" in
      assertRenderedByCssSelector(asDocument(render()), ".govuk-service-navigation")

    "request the service navigation component on every generated link to a shared PlatUI page" in {
      val doc = asDocument(render())

      val sharedPagePaths = Seq(
        "/accessibility-statement/",
        "/contact/report-technical-problem",
        "/help/cookies",
        "/help/privacy",
        "/help/terms-and-conditions"
      )

      sharedPagePaths.foreach { path =>
        withClue(s"links to $path: ") {
          val hrefs = doc.select(s"""a[href*="$path"]""").eachAttr("href").asScala.toSeq

          hrefs must not be empty
          hrefs.foreach(_ must include("useServiceNavigation"))
        }
      }
    }

    "render the report technical issue link" in
      assertRenderedByCssSelector(asDocument(render()), "a.hmrc-report-technical-issue")
  }

}
