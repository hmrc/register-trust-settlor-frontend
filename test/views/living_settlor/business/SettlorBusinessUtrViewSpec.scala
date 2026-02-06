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

package views.living_settlor.business

import forms.UtrFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.living_settlor.business.SettlorBusinessUtrView

class SettlorBusinessUtrViewSpec extends StringViewBehaviours {

  val messageKeyPrefix            = "settlorBusinessUtr"
  val index                       = 0
  val settlorBusinessBusinessName = "BusinessName"
  val hintKey                     = s"$messageKeyPrefix.hint"

  val form: Form[String] = new UtrFormProvider().apply(messageKeyPrefix, emptyUserAnswers, index)

  "SettlorBusinessUtrView view" must {

    val view = viewFor[SettlorBusinessUtrView](Some(emptyUserAnswers))

    def applyView(form: Form[String]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index, settlorBusinessBusinessName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, settlorBusinessBusinessName, "hint")

    behave like stringPageWithDynamicTitle(
      form,
      applyView,
      messageKeyPrefix,
      settlorBusinessBusinessName,
      Some(hintKey)
    )

    behave like pageWithBackLink(applyView(form))
  }

}
