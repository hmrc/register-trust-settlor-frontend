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
import views.html.living_settlor.individual.mld5.CountryOfNationalityYesNoView

class CountryOfNationalityYesNoViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix: String = "settlorIndividualCountryOfNationalityYesNo"
  private val index: Int               = 0
  private val name: FullName           = FullName("First", None, "Last")

  override val form: Form[Boolean] = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  "CountryOfNationalityYesNo View" must {

    val view = viewFor[CountryOfNationalityYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[Boolean]): HtmlFormat.Appendable =
      view.apply(form, index, fakeDraftId, name)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.toString)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, None, Seq(name.toString))

    behave like pageWithASubmitButton(applyView(form))

    behave like pageWithHint(applyView(form), s"$messageKeyPrefix.hint")
  }
}
