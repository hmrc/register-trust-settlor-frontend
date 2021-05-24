/*
 * Copyright 2021 HM Revenue & Customs
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

import controllers.living_settlor.individual.routes
import forms.InternationalAddressFormProvider
import models.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.behaviours.InternationalAddressViewBehaviours
import views.html.living_settlor.individual.SettlorIndividualAddressInternationalView

class SettlorIndividualAddressInternationalViewSpec extends InternationalAddressViewBehaviours {

  val messageKeyPrefix = "settlorIndividualAddressInternational"
  val index = 0
  val name = FullName("First", None, "Last")

  override val form = new InternationalAddressFormProvider()()

  "SettlorIndividualAddressInternationalView" must {

    val view = viewFor[SettlorIndividualAddressInternationalView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, index, fakeDraftId, name)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.toString)

    behave like pageWithBackLink(applyView(form))

    behave like internationalAddress(
      applyView,
      Some("taskList.settlors.label"),
      Some(messageKeyPrefix),
      routes.SettlorIndividualAddressInternationalController.onSubmit(index, fakeDraftId).url,
      name.toString
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
