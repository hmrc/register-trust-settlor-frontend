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

package navigation

import base.SpecBase
import controllers.deceased_settlor.routes
import controllers.deceased_settlor.nonTaxable.routes._
import models.{Mode, NormalMode}
import pages.deceased_settlor._
import pages.deceased_settlor.nonTaxable.{CountryOfNationalityInTheUkYesNoPage, CountryOfNationalityPage, CountryOfNationalityYesNoPage}
import play.api.mvc.Call

class DeceasedSettlorNavigatorSpec extends SpecBase {

  private val navigator: DeceasedSettlorNavigator = injector.instanceOf[DeceasedSettlorNavigator]
  private val mode: Mode = NormalMode

  "DeceasedSettlor Navigator" when {

    "SettlorsNamePage" must {
      "redirect to date of death yes/no" in {
        navigator.nextPage(SettlorsNamePage, mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorDateOfDeathYesNoController.onPageLoad(mode, fakeDraftId))
      }
    }

    "SettlorDateOfDeathYesNoPage" when {

      "yes" must {
        "redirect to date of death" in {
          val userAnswers = emptyUserAnswers.set(SettlorDateOfDeathYesNoPage, true).success.value

          navigator.nextPage(SettlorDateOfDeathYesNoPage, mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorDateOfDeathController.onPageLoad(mode, fakeDraftId))
        }
      }

      "no" must {
        "redirect to date of birth yes/no" in {
          val userAnswers = emptyUserAnswers.set(SettlorDateOfDeathYesNoPage, false).success.value

          navigator.nextPage(SettlorDateOfDeathYesNoPage, mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorDateOfBirthYesNoController.onPageLoad(mode, fakeDraftId))
        }
      }
    }

    "SettlorDateOfDeathPage" must {
      "redirect to date of birth yes/no" in {
        navigator.nextPage(SettlorDateOfDeathPage, mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorDateOfBirthYesNoController.onPageLoad(mode, fakeDraftId))
      }
    }

    "SettlorDateOfBirthYesNoPage" when {

      "yes" must {
        "redirect to date of birth" in {
          val userAnswers = emptyUserAnswers.set(SettlorDateOfBirthYesNoPage, true).success.value

          navigator.nextPage(SettlorDateOfBirthYesNoPage, mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorsDateOfBirthController.onPageLoad(mode, fakeDraftId))
        }
      }

      "no" must {
        "redirect to NINO yes/no" in {
          val userAnswers = emptyUserAnswers.set(SettlorDateOfBirthYesNoPage, false).success.value

          navigator.nextPage(SettlorDateOfBirthYesNoPage, mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorsNINoYesNoController.onPageLoad(mode, fakeDraftId))
        }
      }
    }

    "SettlorsDateOfBirthPage" must {
      "redirect to NINO yes/no" in {
        navigator.nextPage(SettlorsDateOfBirthPage, mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.SettlorsNINoYesNoController.onPageLoad(mode, fakeDraftId))
      }
    }

    "SettlorsNationalInsuranceYesNoPage" when {

      "yes" must {
        "redirect to NINO" in {
          val userAnswers = emptyUserAnswers.set(SettlorsNationalInsuranceYesNoPage, true).success.value

          navigator.nextPage(SettlorsNationalInsuranceYesNoPage, mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorNationalInsuranceNumberController.onPageLoad(mode, fakeDraftId))
        }
      }

      "no" must {
        "redirect to last known address yes/no" in {
          val userAnswers = emptyUserAnswers.set(SettlorsNationalInsuranceYesNoPage, false).success.value

          navigator.nextPage(SettlorsNationalInsuranceYesNoPage, mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorsLastKnownAddressYesNoController.onPageLoad(mode, fakeDraftId))
        }
      }
    }

    "SettlorNationalInsuranceNumberPage" must {
      "redirect to check answers" in {
        navigator.nextPage(SettlorNationalInsuranceNumberPage, mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId))
      }
    }

    "SettlorsLastKnownAddressYesNoPage" when {

      "yes" must {
        "redirect to address UK yes/no" in {
          val userAnswers = emptyUserAnswers.set(SettlorsLastKnownAddressYesNoPage, true).success.value

          navigator.nextPage(SettlorsLastKnownAddressYesNoPage, mode, fakeDraftId)(userAnswers)
            .mustBe(routes.WasSettlorsAddressUKYesNoController.onPageLoad(mode, fakeDraftId))
        }
      }

      "no" must {
        "redirect to check answers" in {
          val userAnswers = emptyUserAnswers.set(SettlorsLastKnownAddressYesNoPage, false).success.value

          navigator.nextPage(SettlorsLastKnownAddressYesNoPage, mode, fakeDraftId)(userAnswers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId))
        }
      }
    }

    "WasSettlorsAddressUKYesNoPage" when {

      "yes" must {
        "redirect to UK address" in {
          val userAnswers = emptyUserAnswers.set(WasSettlorsAddressUKYesNoPage, true).success.value

          navigator.nextPage(WasSettlorsAddressUKYesNoPage, mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorsUKAddressController.onPageLoad(mode, fakeDraftId))
        }
      }

      "no" must {
        "redirect to international address" in {
          val userAnswers = emptyUserAnswers.set(WasSettlorsAddressUKYesNoPage, false).success.value

          navigator.nextPage(WasSettlorsAddressUKYesNoPage, mode, fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorsInternationalAddressController.onPageLoad(mode, fakeDraftId))
        }
      }
    }

    "SettlorsUKAddressPage" must {
      "redirect to check answers" in {
        navigator.nextPage(SettlorsUKAddressPage, mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId))
      }
    }

    "SettlorsInternationalAddressPage" must {
      "redirect to  check answers" in {
        navigator.nextPage(SettlorsInternationalAddressPage, mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId))
      }
    }

    "DeceasedSettlorAnswerPage" must {
      "redirect to task list" in {
        navigator.nextPage(DeceasedSettlorAnswerPage, mode, fakeDraftId)(emptyUserAnswers)
          .mustBe(Call("GET", frontendAppConfig.registrationProgressUrl(fakeDraftId)))
      }
    }


    "a 5mld trust" must {

      "SettlorsDateOfBirthYesNoPage -> Yes -> SettlorsDateOfBirth page" in {

        val answers = emptyUserAnswers
          .set(SettlorDateOfBirthYesNoPage, true).success.value

        navigator.nextPage(SettlorDateOfBirthYesNoPage, NormalMode, draftId, fiveMldEnabled = true)(answers)
          .mustBe(routes.SettlorsDateOfBirthController.onPageLoad(NormalMode, draftId))

      }

      "SettlorsDateOfBirthYesNoPage -> No -> CountryOfNationality Yes No page" in {

        val answers = emptyUserAnswers
          .set(SettlorDateOfBirthYesNoPage, false).success.value

        navigator.nextPage(SettlorDateOfBirthYesNoPage, NormalMode, draftId, fiveMldEnabled = true)(answers)
          .mustBe(CountryOfNationalityYesNoController.onPageLoad(NormalMode, draftId))

      }

      "CountryOfNationalityYesNoPage -> Yes -> CountryOfNationalityInTheUk Yes No page" in {

        val answers = emptyUserAnswers
          .set(CountryOfNationalityYesNoPage, true).success.value

        navigator.nextPage(CountryOfNationalityYesNoPage, NormalMode, draftId, fiveMldEnabled = true)(answers)
          .mustBe(CountryOfNationalityInTheUkYesNoController.onPageLoad(NormalMode, draftId))

      }

      "CountryOfNationalityYesNoPage -> No -> SettlorsNINo Yes No page" in {

        val answers = emptyUserAnswers
          .set(CountryOfNationalityYesNoPage, false).success.value

        navigator.nextPage(CountryOfNationalityYesNoPage, NormalMode, draftId, fiveMldEnabled = true)(answers)
          .mustBe(routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId))

      }

      "CountryOfNationalityInTheUk -> Yes -> SettlorsNINo Yes No page" in {

        val answers = emptyUserAnswers
          .set(CountryOfNationalityInTheUkYesNoPage, true).success.value

        navigator.nextPage(CountryOfNationalityInTheUkYesNoPage, NormalMode, draftId, fiveMldEnabled = true)(answers)
          .mustBe(routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId))

      }

      "CountryOfNationalityInTheUk -> No -> CountryOfNationality page" in {

        val answers = emptyUserAnswers
          .set(CountryOfNationalityInTheUkYesNoPage, false).success.value

        navigator.nextPage(CountryOfNationalityInTheUkYesNoPage, NormalMode, draftId, fiveMldEnabled = true)(answers)
          .mustBe(CountryOfNationalityController.onPageLoad(NormalMode, draftId))

      }

      "CountryOfNationality -> SettlorsNINo Yes No page" in {

        val answers = emptyUserAnswers
          .set(CountryOfNationalityPage, "ES").success.value

        navigator.nextPage(CountryOfNationalityPage, NormalMode, draftId, fiveMldEnabled = true)(answers)
          .mustBe(routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId))

      }


    }








  }
}
