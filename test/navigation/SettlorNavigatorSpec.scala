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

package navigation

import base.SpecBase
import models.pages.AddASettlor._
import models.pages.FullName
import models.pages.IndividualOrBusiness._
import models.pages.Status.Completed
import pages.living_settlor.SettlorIndividualOrBusinessPage
import pages.living_settlor.business.SettlorBusinessNamePage
import pages.living_settlor.individual.SettlorIndividualNamePage
import pages.{AddASettlorPage, AddASettlorYesNoPage, LivingSettlorStatus}
import play.api.mvc.Call
import utils.Constants.MAX

class SettlorNavigatorSpec extends SpecBase {

  private val navigator: SettlorNavigator = injector.instanceOf[SettlorNavigator]

  "Settlor Navigator" when {

    "AddASettlorPage" when {
      "YesNow" must {
        "redirect to Individual or Business" when {

          "no living settlors" must {
            "redirect to index 0" in {
              val userAnswers = emptyUserAnswers.set(AddASettlorPage, YesNow).success.value

              navigator
                .nextPage(AddASettlorPage, fakeDraftId)(userAnswers)
                .mustBe(
                  controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(0, fakeDraftId)
                )
            }
          }

          "there are living settlors" must {
            "redirect to next index" in {
              val userAnswers = emptyUserAnswers
                .set(SettlorIndividualOrBusinessPage(0), Individual)
                .success
                .value
                .set(SettlorIndividualNamePage(0), FullName("Joe", None, "Bloggs"))
                .success
                .value
                .set(LivingSettlorStatus(0), Completed)
                .success
                .value
                .set(AddASettlorPage, YesNow)
                .success
                .value

              navigator
                .nextPage(AddASettlorPage, fakeDraftId)(userAnswers)
                .mustBe(
                  controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(1, fakeDraftId)
                )
            }
          }

          "individuals maxed out" must {
            "redirect to business journey" in {
              val userAnswers = (0 until MAX)
                .foldLeft(emptyUserAnswers) { (acc, index) =>
                  acc
                    .set(SettlorIndividualOrBusinessPage(index), Individual)
                    .success
                    .value
                    .set(SettlorIndividualNamePage(index), FullName("Joe", None, "Bloggs"))
                    .success
                    .value
                    .set(LivingSettlorStatus(index), Completed)
                    .success
                    .value
                }
                .set(AddASettlorPage, YesNow)
                .success
                .value

              navigator
                .nextPage(AddASettlorPage, fakeDraftId)(userAnswers)
                .mustBe(
                  controllers.living_settlor.business.routes.SettlorBusinessNameController.onPageLoad(MAX, fakeDraftId)
                )
            }
          }

          "businesses maxed out" must {
            "redirect to individual journey" in {
              val userAnswers = (0 until MAX)
                .foldLeft(emptyUserAnswers) { (acc, index) =>
                  acc
                    .set(SettlorIndividualOrBusinessPage(index), Business)
                    .success
                    .value
                    .set(SettlorBusinessNamePage(index), "Amazon")
                    .success
                    .value
                    .set(LivingSettlorStatus(index), Completed)
                    .success
                    .value
                }
                .set(AddASettlorPage, YesNow)
                .success
                .value

              navigator
                .nextPage(AddASettlorPage, fakeDraftId)(userAnswers)
                .mustBe(
                  controllers.living_settlor.individual.routes.SettlorAliveYesNoController.onPageLoad(MAX, fakeDraftId)
                )
            }
          }
        }
      }

      "YesLater" must {
        "redirect to task list" in {
          val userAnswers = emptyUserAnswers.set(AddASettlorPage, YesLater).success.value

          navigator
            .nextPage(AddASettlorPage, fakeDraftId)(userAnswers)
            .mustBe(Call("GET", frontendAppConfig.registrationProgressUrl(draftId)))
        }
      }

      "NoComplete" must {
        "redirect to task list" in {
          val userAnswers = emptyUserAnswers.set(AddASettlorPage, NoComplete).success.value

          navigator
            .nextPage(AddASettlorPage, fakeDraftId)(userAnswers)
            .mustBe(Call("GET", frontendAppConfig.registrationProgressUrl(draftId)))
        }
      }
    }

    "AddASettlorYesNoPage" when {
      "yes" must {
        "redirect to set up after settlor died" in {
          val userAnswers = emptyUserAnswers.set(AddASettlorYesNoPage, true).success.value

          navigator
            .nextPage(AddASettlorYesNoPage, fakeDraftId)(userAnswers)
            .mustBe(controllers.trust_type.routes.SetUpByLivingSettlorController.onPageLoad(fakeDraftId))
        }
      }

      "no" must {
        "redirect to task list" in {
          val userAnswers = emptyUserAnswers.set(AddASettlorYesNoPage, false).success.value

          navigator
            .nextPage(AddASettlorYesNoPage, fakeDraftId)(userAnswers)
            .mustBe(Call("GET", frontendAppConfig.registrationProgressUrl(draftId)))
        }
      }
    }

    "SettlorIndividualOrBusinessPage" when {

      val index: Int = 0

      "Individual" must {
        "redirect to Individual Name" in {
          val userAnswers = emptyUserAnswers.set(SettlorIndividualOrBusinessPage(index), Individual).success.value

          navigator
            .nextPage(SettlorIndividualOrBusinessPage(index), fakeDraftId)(userAnswers)
            .mustBe(
              controllers.living_settlor.individual.routes.SettlorAliveYesNoController.onPageLoad(index, fakeDraftId)
            )
        }
      }

      "Business" must {
        "redirect to Business Name" in {
          val userAnswers = emptyUserAnswers.set(SettlorIndividualOrBusinessPage(index), Business).success.value

          navigator
            .nextPage(SettlorIndividualOrBusinessPage(index), fakeDraftId)(userAnswers)
            .mustBe(
              controllers.living_settlor.business.routes.SettlorBusinessNameController.onPageLoad(index, fakeDraftId)
            )
        }
      }
    }
  }

}
