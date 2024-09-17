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

package views.living_settlor.individual

import forms.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.living_settlor.individual.SettlorAliveYesNoView

class SettlorAliveYesNoViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix     = "settlorAliveYesNo"
  private val index                = 0
  override val form: Form[Boolean] = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  "SettlorAliveYesNo view" must {

    val view = viewFor[SettlorAliveYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, None)

    behave like pageWithASubmitButton(applyView(form))
  }
}
