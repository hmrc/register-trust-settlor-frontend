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

import forms.DateOfBirthFormProvider
import models.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.living_settlor.individual.SettlorIndividualDateOfBirthView

import java.time.LocalDate

class SettlorIndividualDateOfBirthViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "settlorIndividualDateOfBirth"
  val index            = 0
  val name             = FullName("First", Some("middle"), "Last")

  val form = new DateOfBirthFormProvider(frontendAppConfig)()

  "SettlorIndividualDateOfBirthView view" must {

    val view = viewFor[SettlorIndividualDateOfBirthView](Some(emptyUserAnswers))

    def applyView(form: Form[LocalDate]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index, name)(fakeRequest, messages)

    val applyViewF = (form: Form[LocalDate]) => applyView(form)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.toString)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithDateFields(form, applyViewF, messageKeyPrefix, "value", name.toString)

    behave like pageWithASubmitButton(applyView(form))
  }
}
