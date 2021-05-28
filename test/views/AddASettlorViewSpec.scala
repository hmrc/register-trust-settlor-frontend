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

package views

import forms.AddASettlorFormProvider
import models.pages.{AddASettlor, IndividualOrBusiness}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import viewmodels.AddRow
import views.behaviours.{OptionsViewBehaviours, TabularDataViewBehaviours}
import views.html.AddASettlorView

class AddASettlorViewSpec extends OptionsViewBehaviours with TabularDataViewBehaviours {

  val completeSettlors = Seq(
    AddRow("Joe Blogs", IndividualOrBusiness.Individual.toString, "#", "#"),
    AddRow("Tom Jones", IndividualOrBusiness.Individual.toString, "#", "#")
  )

  val inProgressSettlors = Seq(
    AddRow("Jon Doe", IndividualOrBusiness.Individual.toString, "#", "#")
  )

  val messageKeyPrefix = "addASettlor"

  val form = new AddASettlorFormProvider()()

  val view: AddASettlorView = viewFor[AddASettlorView](Some(emptyUserAnswers))

  val hint: String = messages("addASettlor.Lifetime")

  def applyView(form: Form[_]): HtmlFormat.Appendable =
    view.apply(form, fakeDraftId, Nil, Nil, "Add a settlor", Some(hint))(fakeRequest, messages)

  def applyView(form: Form[_], inProgressAssets: Seq[AddRow], completeAssets: Seq[AddRow], count: Int): HtmlFormat.Appendable = {
    val title = if (count > 1) s"You have added $count settlors" else "Add a settlor"
    view.apply(form, fakeDraftId, inProgressAssets, completeAssets, title, Some(hint))(fakeRequest, messages)
  }

  "AddASettlorView" when {

    "there are no settlors" must {
      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithNoTabularData(applyView(form))

      behave like pageWithBackLink(applyView(form))

      behave like pageWithOptions(form, applyView, AddASettlor.options)
    }

    "there is data in progress" must {

      val viewWithData = applyView(form, inProgressSettlors, Nil, 1)

      behave like dynamicTitlePage(viewWithData, "addASettlor", "1")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithInProgressTabularData(viewWithData, inProgressSettlors)

      behave like pageWithOptions(form, applyView, AddASettlor.options)

    }

    "there is complete data" must {

      val viewWithData = applyView(form, Nil, completeSettlors, 2)

      behave like dynamicTitlePage(viewWithData, "addASettlor.count", "2")

      behave like pageWithBackLink(viewWithData)

      behave like pageWithCompleteTabularData(viewWithData, completeSettlors)

      behave like pageWithOptions(form, applyView, AddASettlor.options)
    }

  }
}
