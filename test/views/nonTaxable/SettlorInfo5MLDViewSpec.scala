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

package views.nonTaxable

import controllers.trust_type.routes
import models.NormalMode
import views.behaviours.ViewBehaviours
import views.html.nonTaxable.SettlorInfo5MLDView

class SettlorInfo5MLDViewSpec extends ViewBehaviours {

  "SettlorInfo5MLD view" must {

    val view = viewFor[SettlorInfo5MLDView](Some(emptyUserAnswers))

    val applyView = view.apply(fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView, "5mld.settlorInfo",
      "caption",
      "subheading1",
      "paragraph1",
      "bulletpoint1",
      "bulletpoint2",
      "bulletpoint3",
      "bulletpoint4",
      "paragraph2",
      "bulletpoint5",
      "bulletpoint6",
      "bulletpoint7",
      "paragraph3",
      "bulletpoint8",
      "bulletpoint9",
      "bulletpoint10",
      "bulletpoint11",
      "paragraph4",
      "subheading2",
      "paragraph5",
      "details",
      "details.subheading1",
      "details.paragraph1",
      "details.subheading2",
      "details.paragraph2",
      "subheading3",
      "paragraph6",
      "paragraph7",
      "bulletpoint12",
      "bulletpoint13",
      "bulletpoint14"
    )

    behave like pageWithBackLink(applyView)

    behave like pageWithContinueButton(applyView, routes.SetUpAfterSettlorDiedController.onPageLoad(NormalMode, fakeDraftId).url )
  }
}
