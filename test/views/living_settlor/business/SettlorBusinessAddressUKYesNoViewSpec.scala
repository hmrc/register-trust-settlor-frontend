/*
 * Copyright 2020 HM Revenue & Customs
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

import forms.YesNoFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.living_settlor.business.SettlorBusinessAddressUKYesNoView

class SettlorBusinessAddressUKYesNoViewSpec extends YesNoViewBehaviours {

  val messageKeyPrefix = "settlorBusinessAddressUKYesNo"
  val index = 0
  val name = "Business name"
  val form = new YesNoFormProvider().withPrefix(messageKeyPrefix)

  "SettlorBusinessAddressUKYesNo view" must {

    val view = viewFor[SettlorBusinessAddressUKYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId, index, name)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), Some("taskList.settlors.label"), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, Some("taskList.settlors.label"), messageKeyPrefix, None, Seq(name))

    behave like pageWithASubmitButton(applyView(form))
  }
}
