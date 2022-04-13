/*
 * Copyright 2022 HM Revenue & Customs
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

package views.living_settlor.business

import forms.living_settlor.SettlorBusinessNameFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.living_settlor.business.SettlorBusinessNameView

class SettlorBusinessNameViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "settlorBusinessName"

  val form = new SettlorBusinessNameFormProvider()()

  "SettlorBusinessNameView view" must {

    val view = viewFor[SettlorBusinessNameView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, 0, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(
      form,
      applyView,
      messageKeyPrefix,
      None,
      controllers.living_settlor.business.routes.SettlorBusinessNameController.onSubmit(0, fakeDraftId).url
    )
  }
}
