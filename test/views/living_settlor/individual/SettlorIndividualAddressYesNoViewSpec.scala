/*
 * Copyright 2023 HM Revenue & Customs
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
import models.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.living_settlor.individual.SettlorIndividualAddressYesNoView

class SettlorIndividualAddressYesNoViewSpec extends YesNoViewBehaviours {

  override val form: Form[Boolean] = new YesNoFormProvider().withPrefix("settlorIndividualAddressYesNo")
  private val formContentInPastTense: Form[Boolean] = new YesNoFormProvider().withPrefix("settlorIndividualAddressYesNoPastTense")
  private val index = 0
  private val name = FullName("First", Some("Middle"), "Last")

  Seq(
    ("settlorIndividualAddressYesNo", true, form),
    ("settlorIndividualAddressYesNoPastTense", false, formContentInPastTense)
  ) foreach {
    case (messageKey, settlorAliveAtRegistration, formToUse) =>

      s"SettlorIndividualAddressYesNo view where settlorAliveAtRegistration = $settlorAliveAtRegistration" must {

        val view = viewFor[SettlorIndividualAddressYesNoView](Some(emptyUserAnswers))

        def applyView(form: Form[_]): HtmlFormat.Appendable =
          view.apply(form, fakeDraftId, index, name, settlorAliveAtRegistration)(fakeRequest, messages)

        behave like dynamicTitlePage(applyView(formToUse), messageKey, name.toString)

        behave like pageWithBackLink(applyView(formToUse))

        behave like yesNoPage(formToUse, applyView, messageKey, None, Seq(name.toString))

        behave like pageWithASubmitButton(applyView(formToUse))
      }
  }
}
