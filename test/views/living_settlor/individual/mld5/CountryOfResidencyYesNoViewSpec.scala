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

package views.living_settlor.individual.mld5

import forms.YesNoFormProvider
import models.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.living_settlor.individual.mld5.CountryOfResidencyYesNoView

class CountryOfResidencyYesNoViewSpec extends YesNoViewBehaviours {

  private val index: Int     = 0
  private val name: FullName = FullName("First", None, "Last")

  override val form: Form[Boolean]                  = new YesNoFormProvider().withPrefix("settlorIndividualCountryOfResidencyYesNo")
  private val formContentInPastTense: Form[Boolean] =
    new YesNoFormProvider().withPrefix("settlorIndividualCountryOfResidencyYesNoPastTense")

  Seq(
    ("settlorIndividualCountryOfResidencyYesNo", true, form),
    ("settlorIndividualCountryOfResidencyYesNoPastTense", false, formContentInPastTense)
  ) foreach { case (messageKey, settlorAliveAtRegistration, formToUse) =>
    s"CountryOfResidencyYesNo View where settlorAliveAtRegistration = $settlorAliveAtRegistration" must {

      val view = viewFor[CountryOfResidencyYesNoView](Some(emptyUserAnswers))

      def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
        view.apply(form, index, fakeDraftId, name, settlorAliveAtRegistration)(fakeRequest, messages)

      behave like dynamicTitlePage(applyView(formToUse), messageKey, name.toString)

      behave like pageWithBackLink(applyView(formToUse))

      behave like yesNoPage(formToUse, applyView, messageKey, Some(messageKey), Seq(name.toString))

      behave like pageWithASubmitButton(applyView(formToUse))

      behave like pageWithHint(applyView(formToUse), expectedHintKey = s"$messageKey.hint")
    }
  }
}
