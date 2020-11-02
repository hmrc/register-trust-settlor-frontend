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
import controllers.living_settlor.individual.routes
import models.{Mode, NormalMode}
import pages.living_settlor.individual._

class IndividualSettlorNavigatorSpec extends SpecBase {

  private val navigator: IndividualSettlorNavigator = injector.instanceOf[IndividualSettlorNavigator]
  private val mode: Mode = NormalMode
  private val index: Int = 0

  "IndividualSettlor Navigator" when {

    "SettlorIndividualNamePage" must {
      "redirect to date of birth yes/no" in {
        navigator.nextPage(SettlorIndividualNamePage(index), mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId))
      }
    }

    "SettlorIndividualDateOfBirthYesNoPage" when {

      "yes" must {
        "redirect to date of birth" in {
          val userAnswers = emptyUserAnswers.set(SettlorIndividualDateOfBirthYesNoPage(index), true).success.value

          navigator.nextPage(SettlorIndividualDateOfBirthYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualDateOfBirthController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "no" must {
        "redirect to NINO yes/no" in {
          val userAnswers = emptyUserAnswers.set(SettlorIndividualDateOfBirthYesNoPage(index), false).success.value

          navigator.nextPage(SettlorIndividualDateOfBirthYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }
    }

    "SettlorIndividualDateOfBirthPage" must {
      "redirect to NINO yes/no" in {
        navigator.nextPage(SettlorIndividualDateOfBirthPage(index), mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId))
      }
    }

    "SettlorIndividualNINOYesNoPage" when {

      "yes" must {
        "redirect to NINO" in {
          val userAnswers = emptyUserAnswers.set(SettlorIndividualNINOYesNoPage(index), true).success.value

          navigator.nextPage(SettlorIndividualNINOYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualNINOController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "no" must {
        "redirect to address yes/no" in {
          val userAnswers = emptyUserAnswers.set(SettlorIndividualNINOYesNoPage(index), false).success.value

          navigator.nextPage(SettlorIndividualNINOYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }
    }

    "SettlorIndividualNINOPage" must {
      "redirect to check answers" in {
        navigator.nextPage(SettlorIndividualNINOPage(index), mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
      }
    }

    "SettlorAddressYesNoPage" when {

      "yes" must {
        "redirect to address UK yes/no" in {
          val userAnswers = emptyUserAnswers.set(SettlorAddressYesNoPage(index), true).success.value

          navigator.nextPage(SettlorAddressYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "no" must {
        "redirect to check answers" in {
          val userAnswers = emptyUserAnswers.set(SettlorAddressYesNoPage(index), false).success.value

          navigator.nextPage(SettlorAddressYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "SettlorAddressUKYesNoPage" when {

      "yes" must {
        "redirect to UK address" in {
          val userAnswers = emptyUserAnswers.set(SettlorAddressUKYesNoPage(index), true).success.value

          navigator.nextPage(SettlorAddressUKYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualAddressUKController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "no" must {
        "redirect to international address" in {
          val userAnswers = emptyUserAnswers.set(SettlorAddressUKYesNoPage(index), false).success.value

          navigator.nextPage(SettlorAddressUKYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualAddressInternationalController.onPageLoad(mode, index, fakeDraftId))
        }
      }
    }

    "SettlorAddressUKPage" must {
      "redirect to passport yes/no" in {
        navigator.nextPage(SettlorAddressUKPage(index), mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorIndividualPassportYesNoController.onPageLoad(mode, index, fakeDraftId))
      }
    }

    "SettlorAddressInternationalPage" must {
      "redirect to passport yes/no" in {
        navigator.nextPage(SettlorAddressInternationalPage(index), mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorIndividualPassportYesNoController.onPageLoad(mode, index, fakeDraftId))
      }
    }

    "SettlorIndividualPassportYesNoPage" when {

      "yes" must {
        "redirect to passport" in {
          val userAnswers = emptyUserAnswers.set(SettlorIndividualPassportYesNoPage(index), true).success.value

          navigator.nextPage(SettlorIndividualPassportYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualPassportController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "no" must {
        "redirect to ID card yes/no" in {
          val userAnswers = emptyUserAnswers.set(SettlorIndividualPassportYesNoPage(index), false).success.value

          navigator.nextPage(SettlorIndividualPassportYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualIDCardYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }
    }

    "SettlorIndividualPassportPage" must {
      "redirect to check answers" in {
        navigator.nextPage(SettlorIndividualPassportPage(index), mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
      }
    }

    "SettlorIndividualIDCardYesNoPage" when {

      "yes" must {
        "redirect to ID card" in {
          val userAnswers = emptyUserAnswers.set(SettlorIndividualIDCardYesNoPage(index), true).success.value

          navigator.nextPage(SettlorIndividualIDCardYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualIDCardController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "no" must {
        "redirect to check answers" in {
          val userAnswers = emptyUserAnswers.set(SettlorIndividualIDCardYesNoPage(index), false).success.value

          navigator.nextPage(SettlorIndividualIDCardYesNoPage(index), mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
        }
      }
    }

    "SettlorIndividualIDCardPage" must {
      "redirect to check answers" in {
        navigator.nextPage(SettlorIndividualIDCardPage(index), mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
      }
    }

    "SettlorIndividualAnswerPage" must {
      "redirect to add-to page" in {
        navigator.nextPage(SettlorIndividualAnswerPage, mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(controllers.routes.AddASettlorController.onPageLoad(fakeDraftId))
      }
    }
  }
}
