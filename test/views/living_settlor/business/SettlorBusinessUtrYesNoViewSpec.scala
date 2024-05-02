/*
 * Copyright 2024 HM Revenue & Customs
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

import forms.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.living_settlor.business.SettlorBusinessUtrYesNoView

class SettlorBusinessUtrYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "settlorBusinessUtrYesNo"

  val form = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  val index = 0

  val settlorBusinessName = "Business name"

  "SettlorBusinessUtrYesNo view" must {

    val view = viewFor[SettlorBusinessUtrYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index, settlorBusinessName)(fakeRequest, messages)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, None, Seq(settlorBusinessName))
  }
}
