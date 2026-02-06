/*
 * Copyright 2026 HM Revenue & Customs
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

package utils

import base.SpecBase
import controllers.living_settlor.business.{routes => busRoutes}
import controllers.living_settlor.individual.{routes => indRoutes}
import controllers.routes.RemoveSettlorYesNoController
import models.pages.FullName
import models.pages.IndividualOrBusiness._
import models.pages.Status.Completed
import pages.LivingSettlorStatus
import pages.living_settlor.{SettlorIndividualOrBusinessPage, business => bus, individual => ind}
import viewmodels.{AddRow, AddToRows}

class AddASettlorViewHelperSpec extends SpecBase {

  private val individualName: FullName = FullName("Joe", None, "Bloggs")
  private val businessName: String     = "Business Ltd."

  private val individualLabel: String = "Individual Settlor"
  private val businessLabel: String   = "Business Settlor"

  "AddASettlor view helper" must {

    "render rows" when {

      "no settlors" in {

        val helper = new AddASettlorViewHelper(emptyUserAnswers, fakeDraftId)

        helper.rows mustBe AddToRows(
          inProgress = Nil,
          complete = Nil
        )
      }

      "individual settlor" when {

        "in progress" in {

          val userAnswers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), Individual)
            .success
            .value
            .set(ind.SettlorIndividualNamePage(0), individualName)
            .success
            .value

          val helper = new AddASettlorViewHelper(userAnswers, fakeDraftId)

          helper.rows mustBe AddToRows(
            inProgress = List(
              AddRow(
                name = individualName.toString,
                typeLabel = individualLabel,
                changeUrl = indRoutes.SettlorIndividualNameController.onPageLoad(0, fakeDraftId).url,
                removeUrl = RemoveSettlorYesNoController.onPageLoad(0, fakeDraftId).url
              )
            ),
            complete = Nil
          )
        }

        "complete" in {

          val userAnswers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), Individual)
            .success
            .value
            .set(ind.SettlorIndividualNamePage(0), individualName)
            .success
            .value
            .set(ind.SettlorIndividualDateOfBirthYesNoPage(0), false)
            .success
            .value
            .set(ind.SettlorIndividualNINOYesNoPage(0), false)
            .success
            .value
            .set(ind.SettlorAddressYesNoPage(0), false)
            .success
            .value
            .set(LivingSettlorStatus(0), Completed)
            .success
            .value

          val helper = new AddASettlorViewHelper(userAnswers, fakeDraftId)

          helper.rows mustBe AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = individualName.toString,
                typeLabel = individualLabel,
                changeUrl = indRoutes.SettlorIndividualAnswerController.onPageLoad(0, fakeDraftId).url,
                removeUrl = RemoveSettlorYesNoController.onPageLoad(0, fakeDraftId).url
              )
            )
          )
        }
      }

      "business settlor" when {

        "in progress" in {

          val userAnswers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), Business)
            .success
            .value
            .set(bus.SettlorBusinessNamePage(0), businessName)
            .success
            .value

          val helper = new AddASettlorViewHelper(userAnswers, fakeDraftId)

          helper.rows mustBe AddToRows(
            inProgress = List(
              AddRow(
                name = businessName,
                typeLabel = businessLabel,
                changeUrl = busRoutes.SettlorBusinessNameController.onPageLoad(0, fakeDraftId).url,
                removeUrl = RemoveSettlorYesNoController.onPageLoad(0, fakeDraftId).url
              )
            ),
            complete = Nil
          )
        }

        "complete" in {

          val userAnswers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), Business)
            .success
            .value
            .set(bus.SettlorBusinessNamePage(0), businessName)
            .success
            .value
            .set(bus.SettlorBusinessUtrYesNoPage(0), false)
            .success
            .value
            .set(bus.SettlorBusinessAddressYesNoPage(0), false)
            .success
            .value
            .set(LivingSettlorStatus(0), Completed)
            .success
            .value

          val helper = new AddASettlorViewHelper(userAnswers, fakeDraftId)

          helper.rows mustBe AddToRows(
            inProgress = Nil,
            complete = List(
              AddRow(
                name = businessName,
                typeLabel = businessLabel,
                changeUrl = busRoutes.SettlorBusinessAnswerController.onPageLoad(0, fakeDraftId).url,
                removeUrl = RemoveSettlorYesNoController.onPageLoad(0, fakeDraftId).url
              )
            )
          )
        }
      }

      "individual and business settlors" in {

        val userAnswers = emptyUserAnswers
          .set(SettlorIndividualOrBusinessPage(0), Individual)
          .success
          .value
          .set(ind.SettlorIndividualNamePage(0), individualName)
          .success
          .value
          .set(SettlorIndividualOrBusinessPage(1), Individual)
          .success
          .value
          .set(ind.SettlorIndividualNamePage(1), individualName)
          .success
          .value
          .set(ind.SettlorIndividualDateOfBirthYesNoPage(1), false)
          .success
          .value
          .set(ind.SettlorIndividualNINOYesNoPage(1), false)
          .success
          .value
          .set(ind.SettlorAddressYesNoPage(1), false)
          .success
          .value
          .set(LivingSettlorStatus(1), Completed)
          .success
          .value
          .set(SettlorIndividualOrBusinessPage(2), Business)
          .success
          .value
          .set(bus.SettlorBusinessNamePage(2), businessName)
          .success
          .value
          .set(SettlorIndividualOrBusinessPage(3), Business)
          .success
          .value
          .set(bus.SettlorBusinessNamePage(3), businessName)
          .success
          .value
          .set(bus.SettlorBusinessUtrYesNoPage(3), false)
          .success
          .value
          .set(bus.SettlorBusinessAddressYesNoPage(3), false)
          .success
          .value
          .set(LivingSettlorStatus(3), Completed)
          .success
          .value

        val helper = new AddASettlorViewHelper(userAnswers, fakeDraftId)

        helper.rows mustBe AddToRows(
          inProgress = List(
            AddRow(
              name = individualName.toString,
              typeLabel = individualLabel,
              changeUrl = indRoutes.SettlorIndividualNameController.onPageLoad(0, fakeDraftId).url,
              removeUrl = RemoveSettlorYesNoController.onPageLoad(0, fakeDraftId).url
            ),
            AddRow(
              name = businessName,
              typeLabel = businessLabel,
              changeUrl = busRoutes.SettlorBusinessNameController.onPageLoad(2, fakeDraftId).url,
              removeUrl = RemoveSettlorYesNoController.onPageLoad(2, fakeDraftId).url
            )
          ),
          complete = List(
            AddRow(
              name = individualName.toString,
              typeLabel = individualLabel,
              changeUrl = indRoutes.SettlorIndividualAnswerController.onPageLoad(1, fakeDraftId).url,
              removeUrl = RemoveSettlorYesNoController.onPageLoad(1, fakeDraftId).url
            ),
            AddRow(
              name = businessName,
              typeLabel = businessLabel,
              changeUrl = busRoutes.SettlorBusinessAnswerController.onPageLoad(3, fakeDraftId).url,
              removeUrl = RemoveSettlorYesNoController.onPageLoad(3, fakeDraftId).url
            )
          )
        )
      }
    }
  }

}
