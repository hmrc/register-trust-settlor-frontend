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

package views.deceased_settlor

import forms.deceased_settlor.SettlorsDateOfBirthFormProvider
import models.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.deceased_settlor.SettlorsDateOfBirthView

import java.time.LocalDate

class SettlorsDateOfBirthViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "settlorsDateOfBirth"

  val form: Form[LocalDate] = new SettlorsDateOfBirthFormProvider(frontendAppConfig).withConfig()

  "SettlorsDateOfBirthView view" must {

    val name = FullName("First", None, "Last")

    val view = viewFor[SettlorsDateOfBirthView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, name)(fakeRequest, messages)

    val applyViewF = (form: Form[_]) => applyView(form)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.toString, "hint")

    behave like pageWithBackLink(applyView(form))

    behave like pageWithDateFields(form, applyViewF,
      messageKeyPrefix,
      "value",
      name.toString
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
