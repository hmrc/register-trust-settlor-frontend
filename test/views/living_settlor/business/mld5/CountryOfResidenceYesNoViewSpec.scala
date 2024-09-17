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

package views.living_settlor.business.mld5

import forms.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.living_settlor.business.mld5.CountryOfResidenceYesNoView

class CountryOfResidenceYesNoViewSpec extends YesNoViewBehaviours {

  val prefix    = "settlorBusiness.5mld.countryOfResidenceYesNo"
  val index     = 0
  val trustName = "Test"

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix(prefix)

  "countryOfResidenceYesNo view" must {

    val view = viewFor[CountryOfResidenceYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index, trustName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), prefix, trustName)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, prefix, Some(prefix), Seq(trustName))

    behave like pageWithASubmitButton(applyView(form))
  }
}
