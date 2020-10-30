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

package navigation

import base.SpecBase
import controllers.routes
import generators.Generators
import pages._
import models._
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks

class SettlorNavigatorSpec extends SpecBase with ScalaCheckPropertyChecks with Generators {

  val navigator: SettlorNavigator = injector.instanceOf[SettlorNavigator]
  val index = 0

  "Settlor Navigator" must {

    "go to WhatTypeOfBeneficiaryPage from AddABeneficiaryYesNoPage when selected yes" in {
      forAll(arbitrary[UserAnswers]) {
        userAnswers =>

          val answers = userAnswers.set(AddABeneficiaryYesNoPage, true).success.value

          navigator.nextPage(AddABeneficiaryYesNoPage, fakeDraftId, answers)
            .mustBe(controllers.register.beneficiaries.routes.WhatTypeOfBeneficiaryController.onPageLoad(fakeDraftId))
      }
    }
  }
}
