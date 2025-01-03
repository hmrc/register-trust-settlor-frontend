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

import forms.UKAddressFormProvider
import models.pages.{FullName, UKAddress}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.UkAddressViewBehaviours
import views.html.living_settlor.individual.SettlorIndividualAddressUKView

class SettlorIndividualAddressUKViewSpec extends UkAddressViewBehaviours {

  override val form          = new UKAddressFormProvider()()
  private val index          = 0
  private val name: FullName = FullName("First", Some("middle"), "Last")

  Seq(
    ("settlorIndividualAddressUK", true),
    ("settlorIndividualAddressUKPastTense", false)
  ) foreach { case (messageKey, settlorAliveAtRegistration) =>
    s"SettlorIndividualAddressUKView where settlorAliveAtRegistration = $settlorAliveAtRegistration)" must {

      val view = viewFor[SettlorIndividualAddressUKView](Some(emptyUserAnswers))

      def applyView(form: Form[UKAddress]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId, index, name, settlorAliveAtRegistration)(fakeRequest, messages)

      behave like dynamicTitlePage(applyView(form), messageKey, name.toString)

      behave like pageWithBackLink(applyView(form))

      behave like ukAddressPage(
        applyView,
        Some(messageKey),
        name.toString
      )

      behave like pageWithASubmitButton(applyView(form))
    }
  }
}
