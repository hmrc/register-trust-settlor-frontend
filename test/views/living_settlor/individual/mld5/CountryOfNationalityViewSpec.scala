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

import forms.CountryFormProvider
import models.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.SelectCountryViewBehaviours
import views.html.living_settlor.individual.mld5.CountryOfNationalityView

class CountryOfNationalityViewSpec extends SelectCountryViewBehaviours {

  private val index: Int     = 0
  private val name: FullName = FullName("First", None, "Last")

  override val form: Form[String]          = new CountryFormProvider().withPrefix("settlorIndividualCountryOfNationality")

  val formContentInPastTense: Form[String] =
    new CountryFormProvider().withPrefix("settlorIndividualCountryOfNationalityPastTense")

  Seq(
    ("settlorIndividualCountryOfNationality", true, form),
    ("settlorIndividualCountryOfNationalityPastTense", false, formContentInPastTense)
  ) foreach { case (messageKey, settlorAliveAtRegistration, formToUse) =>
    s"CountryOfNationality View where settlorAliveAtRegistration = $settlorAliveAtRegistration" must {

      val view = viewFor[CountryOfNationalityView](Some(emptyUserAnswers))

      def applyView(form: Form[String]): HtmlFormat.Appendable =
        view.apply(form, index, fakeDraftId, countryOptions, name, settlorAliveAtRegistration)(fakeRequest, messages)

      behave like dynamicTitlePage(applyView(formToUse), messageKey, name.toString)

      behave like pageWithBackLink(applyView(formToUse))

      behave like selectCountryPage(formToUse, applyView, messageKey, name.toString)

      behave like pageWithASubmitButton(applyView(formToUse))
    }
  }

}
