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

package views.deceased_settlor

import views.behaviours.ViewBehaviours
import views.html.deceased_settlor.DeceasedSettlorAnswerView

class DeceasedSettlorAnswerViewSpec extends ViewBehaviours {

  "DeceasedSettlorAnswer view" must {

    val view = viewFor[DeceasedSettlorAnswerView](Some(emptyUserAnswers))

    val applyView = view.apply(fakeDraftId, Nil)(fakeRequest, messages)

    behave like normalPage(applyView, "deceasedSettlorAnswer")

    behave like pageWithBackLink(applyView)
  }
}
