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

import forms.living_settlor.SettlorIndividualNameFormProvider
import models.UserAnswers
import models.pages.FullName
import pages.living_settlor.individual.SettlorAliveYesNoPage
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.living_settlor.individual.SettlorIndividualNameView

class SettlorIndividualNameViewSpec extends QuestionViewBehaviours[FullName] {

  val index = 0

  override val form = new SettlorIndividualNameFormProvider()()

  Seq(
    ("settlorIndividualName", true),
    ("settlorIndividualNamePastTense", false)
  ) foreach { case (setUpBeforeSettlorMessage, setUpBeforeSettlorDiedBool) =>
    s"SettlorIndividualNameView view (when setUpBeforeSettlorDied is $setUpBeforeSettlorDiedBool)" must {

      val userAnswers: UserAnswers =
        emptyUserAnswers.set(SettlorAliveYesNoPage(index), setUpBeforeSettlorDiedBool).success.value

      val view = viewFor[SettlorIndividualNameView](Some(userAnswers))

      def applyView(form: Form[_]): HtmlFormat.Appendable =
        view.apply(form, fakeDraftId, index, setUpBeforeSettlorDied = setUpBeforeSettlorDiedBool)(fakeRequest, messages)

      behave like normalPage(applyView(form), setUpBeforeSettlorMessage)

      behave like pageWithBackLink(applyView(form))

      behave like pageWithTextFields(
        form,
        applyView,
        setUpBeforeSettlorMessage,
        Seq(("firstName", None), ("middleName", None), ("lastName", None))
      )

      behave like pageWithASubmitButton(applyView(form))
    }
  }
}
