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
import controllers.living_settlor.business.routes
import models.pages.KindOfTrust.{Employees, Intervivos}
import models.{Mode, NormalMode}
import pages.living_settlor.business._
import pages.trust_type.KindOfTrustPage

class BusinessSettlorNavigatorSpec extends SpecBase {

  private val navigator: BusinessSettlorNavigator = injector.instanceOf[BusinessSettlorNavigator]
  private val mode: Mode = NormalMode
  private val index: Int = 0

  "BusinessSettlor Navigator" when {

    "SettlorBusinessNamePage" must {
      "redirect to UTR yes/no" in {
        navigator.nextPage(SettlorBusinessNamePage(index), mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorBusinessUtrYesNoController.onPageLoad(mode, index, fakeDraftId))
      }
    }

    "SettlorBusinessUtrYesNoPage" when {

      "yes" must {
        "redirect to UTR" in {
          val userAnswers = emptyUserAnswers.set(SettlorBusinessUtrYesNoPage(index), true).success.value

          navigator.nextPage(SettlorBusinessUtrYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorBusinessUtrController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "no" must {
        "redirect to address yes/no" in {
          val userAnswers = emptyUserAnswers.set(SettlorBusinessUtrYesNoPage(index), false).success.value

          navigator.nextPage(SettlorBusinessUtrYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorBusinessAddressYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }
    }

    "SettlorBusinessUtrPage" when {
      "a trust for the employees of a company" must {
        "redirect to kind of business" in {
          val userAnswers = emptyUserAnswers.set(KindOfTrustPage, Employees).success.value

          navigator.nextPage(SettlorBusinessUtrPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorBusinessTypeController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "not a trust for the employees of a company" must {
        "redirect to check answers" in {
          val userAnswers = emptyUserAnswers.set(KindOfTrustPage, Intervivos).success.value

          navigator.nextPage(SettlorBusinessUtrPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "SettlorBusinessAddressYesNoPage" when {

      "yes" must {
        "redirect to address UK yes/no" in {
          val userAnswers = emptyUserAnswers.set(SettlorBusinessAddressYesNoPage(index), true).success.value

          navigator.nextPage(SettlorBusinessAddressYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorBusinessAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "no" when {
        "a trust for the employees of a company" must {
          "redirect to kind of business" in {
            val userAnswers = emptyUserAnswers
              .set(KindOfTrustPage, Employees).success.value
              .set(SettlorBusinessAddressYesNoPage(index), false).success.value

            navigator.nextPage(SettlorBusinessAddressYesNoPage(index), mode, fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessTypeController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "not a trust for the employees of a company" must {
          "redirect to check answers" in {
            val userAnswers = emptyUserAnswers
              .set(KindOfTrustPage, Intervivos).success.value
              .set(SettlorBusinessAddressYesNoPage(index), false).success.value

            navigator.nextPage(SettlorBusinessAddressYesNoPage(index), mode, fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
          }
        }
      }
    }

    "SettlorBusinessAddressUKYesNoPage" when {

      "yes" must {
        "redirect to UK address" in {
          val userAnswers = emptyUserAnswers.set(SettlorBusinessAddressUKYesNoPage(index), true).success.value

          navigator.nextPage(SettlorBusinessAddressUKYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorBusinessAddressUKController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "no" must {
        "redirect to international address" in {
          val userAnswers = emptyUserAnswers.set(SettlorBusinessAddressUKYesNoPage(index), false).success.value

          navigator.nextPage(SettlorBusinessAddressUKYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorBusinessAddressInternationalController.onPageLoad(mode, index, fakeDraftId))
        }
      }
    }

    "SettlorBusinessAddressUKPage" when {
      "a trust for the employees of a company" must {
        "redirect to kind of business" in {
          val userAnswers = emptyUserAnswers.set(KindOfTrustPage, Employees).success.value

          navigator.nextPage(SettlorBusinessAddressUKPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorBusinessTypeController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "not a trust for the employees of a company" must {
        "redirect to check answers" in {
          val userAnswers = emptyUserAnswers.set(KindOfTrustPage, Intervivos).success.value

          navigator.nextPage(SettlorBusinessAddressUKPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "SettlorBusinessAddressInternationalPage" must {
      "a trust for the employees of a company" must {
        "redirect to kind of business" in {
          val userAnswers = emptyUserAnswers.set(KindOfTrustPage, Employees).success.value

          navigator.nextPage(SettlorBusinessAddressInternationalPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorBusinessTypeController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "not a trust for the employees of a company" must {
        "redirect to check answers" in {
          val userAnswers = emptyUserAnswers.set(KindOfTrustPage, Intervivos).success.value

          navigator.nextPage(SettlorBusinessAddressInternationalPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "SettlorBusinessTypePage" must {
      "redirect to existed for 2 years yes/no" in {
        navigator.nextPage(SettlorBusinessTypePage(index), mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorBusinessTimeYesNoController.onPageLoad(mode, index, fakeDraftId))
      }
    }

    "SettlorBusinessTimeYesNoPage" must {
      "redirect to check answers" in {
        navigator.nextPage(SettlorBusinessTimeYesNoPage(index), mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
      }
    }

    "SettlorBusinessAnswerPage" must {
      "redirect to add-to page" in {
        navigator.nextPage(SettlorBusinessAnswerPage, mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(controllers.routes.AddASettlorController.onPageLoad(fakeDraftId))
      }
    }
  }
}
