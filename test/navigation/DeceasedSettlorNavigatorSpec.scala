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
import controllers.deceased_settlor.mld5.{routes => mld5Rts}
import controllers.deceased_settlor.routes
import models.UserAnswers
import pages.deceased_settlor._
import pages.deceased_settlor.mld5._
import play.api.mvc.Call

class DeceasedSettlorNavigatorSpec extends SpecBase {

  private val navigator: DeceasedSettlorNavigator = injector.instanceOf[DeceasedSettlorNavigator]

  "DeceasedSettlor Navigator" when {

    "a 4mld trust" must {

      val baseAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = false)

      "SettlorsNamePage" must {
        "redirect to date of death yes/no" in {
          navigator.nextPage(SettlorsNamePage, fakeDraftId)(baseAnswers)
            .mustBe(routes.SettlorDateOfDeathYesNoController.onPageLoad(fakeDraftId))
        }
      }

      "SettlorDateOfDeathYesNoPage" when {

        "yes" must {
          "redirect to date of death" in {
            val userAnswers = baseAnswers.set(SettlorDateOfDeathYesNoPage, true).success.value

            navigator.nextPage(SettlorDateOfDeathYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorDateOfDeathController.onPageLoad(fakeDraftId))
          }
        }

        "no" must {
          "redirect to date of birth yes/no" in {
            val userAnswers = baseAnswers.set(SettlorDateOfDeathYesNoPage, false).success.value

            navigator.nextPage(SettlorDateOfDeathYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorDateOfBirthYesNoController.onPageLoad(fakeDraftId))
          }
        }
      }

      "SettlorDateOfDeathPage" must {
        "redirect to date of birth yes/no" in {
          navigator.nextPage(SettlorDateOfDeathPage, fakeDraftId)(baseAnswers)
            .mustBe(routes.SettlorDateOfBirthYesNoController.onPageLoad(fakeDraftId))
        }
      }

      "SettlorDateOfBirthYesNoPage" when {

        "yes" must {
          "redirect to date of birth" in {
            val userAnswers = baseAnswers.set(SettlorDateOfBirthYesNoPage, true).success.value

            navigator.nextPage(SettlorDateOfBirthYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorsDateOfBirthController.onPageLoad(fakeDraftId))
          }
        }

        "no" must {
          "redirect to NINO yes/no" in {
            val userAnswers = baseAnswers.set(SettlorDateOfBirthYesNoPage, false).success.value

            navigator.nextPage(SettlorDateOfBirthYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorsNINoYesNoController.onPageLoad(fakeDraftId))
          }
        }
      }

      "SettlorsDateOfBirthPage" must {
        "redirect to NINO yes/no" in {
          navigator.nextPage(SettlorsDateOfBirthPage, fakeDraftId)(baseAnswers)
            .mustBe(routes.SettlorsNINoYesNoController.onPageLoad(fakeDraftId))
        }
      }

      "SettlorsNationalInsuranceYesNoPage" when {

        "yes" must {
          "redirect to NINO" in {
            val userAnswers = baseAnswers.set(SettlorsNationalInsuranceYesNoPage, true).success.value

            navigator.nextPage(SettlorsNationalInsuranceYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorNationalInsuranceNumberController.onPageLoad(fakeDraftId))
          }
        }

        "no" must {
          "redirect to last known address yes/no" in {
            val userAnswers = baseAnswers.set(SettlorsNationalInsuranceYesNoPage, false).success.value

            navigator.nextPage(SettlorsNationalInsuranceYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorsLastKnownAddressYesNoController.onPageLoad(fakeDraftId))
          }
        }
      }

      "SettlorNationalInsuranceNumberPage" must {
        "redirect to check answers" in {
          navigator.nextPage(SettlorNationalInsuranceNumberPage, fakeDraftId)(baseAnswers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId))
        }
      }

      "SettlorsLastKnownAddressYesNoPage" when {

        "yes" must {
          "redirect to address UK yes/no" in {
            val userAnswers = baseAnswers.set(SettlorsLastKnownAddressYesNoPage, true).success.value

            navigator.nextPage(SettlorsLastKnownAddressYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(routes.WasSettlorsAddressUKYesNoController.onPageLoad(fakeDraftId))
          }
        }

        "no" must {
          "redirect to check answers" in {
            val userAnswers = baseAnswers.set(SettlorsLastKnownAddressYesNoPage, false).success.value

            navigator.nextPage(SettlorsLastKnownAddressYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId))
          }
        }
      }

      "WasSettlorsAddressUKYesNoPage" when {

        "yes" must {
          "redirect to UK address" in {
            val userAnswers = baseAnswers.set(WasSettlorsAddressUKYesNoPage, true).success.value

            navigator.nextPage(WasSettlorsAddressUKYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorsUKAddressController.onPageLoad(fakeDraftId))
          }
        }

        "no" must {
          "redirect to international address" in {
            val userAnswers = baseAnswers.set(WasSettlorsAddressUKYesNoPage, false).success.value

            navigator.nextPage(WasSettlorsAddressUKYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorsInternationalAddressController.onPageLoad(fakeDraftId))
          }
        }
      }

      "SettlorsUKAddressPage" must {
        "redirect to check answers" in {
          navigator.nextPage(SettlorsUKAddressPage, fakeDraftId)(baseAnswers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId))
        }
      }

      "SettlorsInternationalAddressPage" must {
        "redirect to check answers" in {
          navigator.nextPage(SettlorsInternationalAddressPage, fakeDraftId)(baseAnswers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(fakeDraftId))
        }
      }

      "DeceasedSettlorAnswerPage" must {
        "redirect to task list" in {
          navigator.nextPage(DeceasedSettlorAnswerPage, fakeDraftId)(baseAnswers)
            .mustBe(Call("GET", frontendAppConfig.registrationProgressUrl(fakeDraftId)))
        }
      }
    }

    "a 5mld trust" when {

      "taxable" must {
        val baseAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = true)

        "SettlorsDateOfBirthYesNoPage -> Yes -> SettlorsDateOfBirth page" in {

          val answers = baseAnswers
            .set(SettlorDateOfBirthYesNoPage, true).success.value

          navigator.nextPage(SettlorDateOfBirthYesNoPage, draftId)(answers)
            .mustBe(routes.SettlorsDateOfBirthController.onPageLoad(draftId))

        }

        "SettlorsDateOfBirthYesNoPage -> No -> CountryOfNationality Yes No page" in {

          val answers = baseAnswers
            .set(SettlorDateOfBirthYesNoPage, false).success.value

          navigator.nextPage(SettlorDateOfBirthYesNoPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfNationalityYesNoController.onPageLoad(draftId))

        }

        "CountryOfNationalityYesNoPage -> Yes -> CountryOfNationalityInTheUk Yes No page" in {

          val answers = baseAnswers
            .set(CountryOfNationalityYesNoPage, true).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(draftId))

        }

        "CountryOfNationalityYesNoPage -> No -> SettlorsNINo Yes No page" in {

          val answers = baseAnswers
            .set(CountryOfNationalityYesNoPage, false).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage, draftId)(answers)
            .mustBe(routes.SettlorsNINoYesNoController.onPageLoad(draftId))

        }

        "CountryOfNationalityInTheUk -> Yes -> SettlorsNINo Yes No page" in {

          val answers = baseAnswers
            .set(CountryOfNationalityInTheUkYesNoPage, true).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage, draftId)(answers)
            .mustBe(routes.SettlorsNINoYesNoController.onPageLoad(draftId))

        }

        "CountryOfNationalityInTheUk -> No -> CountryOfNationality page" in {

          val answers = baseAnswers
            .set(CountryOfNationalityInTheUkYesNoPage, false).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfNationalityController.onPageLoad(draftId))

        }

        "CountryOfNationality -> SettlorsNINo Yes No page" in {

          val answers = baseAnswers
            .set(CountryOfNationalityPage, "ES").success.value

          navigator.nextPage(CountryOfNationalityPage, draftId)(answers)
            .mustBe(routes.SettlorsNINoYesNoController.onPageLoad(draftId))

        }

        "CountryOfResidenceYesNoPage -> Yes -> CountryOfResidenceInTheUk Yes No page" in {

          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage, true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(draftId))

        }

        "CountryOfResidenceYesNoPage -> No (with Nino) -> DeceasedSettlorAnswer page" in {

          val answers = baseAnswers
            .set(SettlorsNationalInsuranceYesNoPage, true).success.value
            .set(CountryOfResidenceYesNoPage, false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage, draftId)(answers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(draftId))

        }

        "CountryOfResidenceYesNoPage -> No (with No Nino) -> SettlorsLastKnownAddress Yes No page" in {

          val answers = baseAnswers
            .set(SettlorsNationalInsuranceYesNoPage, false).success.value
            .set(CountryOfResidenceYesNoPage, false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage, draftId)(answers)
            .mustBe(routes.SettlorsLastKnownAddressYesNoController.onPageLoad(draftId))

        }

        "CountryOfResidenceInTheUk -> No -> CountryOfResidence page" in {

          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage, false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfResidenceController.onPageLoad(draftId))

        }


        "CountryOfResidenceInTheUk -> Yes (with Nino) -> DeceasedSettlorAnswer page" in {

          val answers = baseAnswers
            .set(SettlorsNationalInsuranceYesNoPage, true).success.value
            .set(CountryOfResidenceInTheUkYesNoPage, true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, draftId)(answers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(draftId))

        }

        "CountryOfResidenceInTheUk -> Yes (with No Nino) -> SettlorsLastKnownAddress Yes No page" in {

          val answers = baseAnswers
            .set(SettlorsNationalInsuranceYesNoPage, false).success.value
            .set(CountryOfResidenceInTheUkYesNoPage, true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, draftId)(answers)
            .mustBe(routes.SettlorsLastKnownAddressYesNoController.onPageLoad(draftId))

        }

        "CountryOfResidence (with Nino) -> DeceasedSettlorAnswer page" in {

          val answers = baseAnswers
            .set(SettlorsNationalInsuranceYesNoPage, true).success.value
            .set(CountryOfResidencePage, "ES").success.value

          navigator.nextPage(CountryOfResidencePage, draftId)(answers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(draftId))

        }

        "CountryOfResidence (with No Nino) -> SettlorsLastKnownAddress Yes No page" in {

          val answers = baseAnswers
            .set(SettlorsNationalInsuranceYesNoPage, false).success.value
            .set(CountryOfResidencePage, "ES").success.value

          navigator.nextPage(CountryOfResidencePage, draftId)(answers)
            .mustBe(routes.SettlorsLastKnownAddressYesNoController.onPageLoad(draftId))

        }
      }

      "non taxable" must {
        val baseAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = false)

        "SettlorsDateOfBirthYesNoPage -> Yes -> SettlorsDateOfBirth page" in {

          val answers = baseAnswers
            .set(SettlorDateOfBirthYesNoPage, true).success.value

          navigator.nextPage(SettlorDateOfBirthYesNoPage, draftId)(answers)
            .mustBe(routes.SettlorsDateOfBirthController.onPageLoad(draftId))

        }

        "SettlorsDateOfBirthYesNoPage -> No -> CountryOfNationality Yes No page" in {

          val answers = baseAnswers
            .set(SettlorDateOfBirthYesNoPage, false).success.value

          navigator.nextPage(SettlorDateOfBirthYesNoPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfNationalityYesNoController.onPageLoad(draftId))

        }

        "CountryOfNationalityYesNoPage -> Yes -> CountryOfNationalityInTheUk Yes No page" in {

          val answers = baseAnswers
            .set(CountryOfNationalityYesNoPage, true).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfNationalityInTheUkYesNoController.onPageLoad(draftId))

        }

        "CountryOfNationalityYesNoPage -> No -> CountryOfResidence Yes No page" in {

          val answers = baseAnswers
            .set(CountryOfNationalityYesNoPage, false).success.value

          navigator.nextPage(CountryOfNationalityYesNoPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfResidenceYesNoController.onPageLoad(draftId))

        }

        "CountryOfNationalityInTheUk -> Yes -> CountryOfResidence Yes No page" in {

          val answers = baseAnswers
            .set(CountryOfNationalityInTheUkYesNoPage, true).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfResidenceYesNoController.onPageLoad(draftId))

        }

        "CountryOfNationalityInTheUk -> No -> CountryOfNationality page" in {

          val answers = baseAnswers
            .set(CountryOfNationalityInTheUkYesNoPage, false).success.value

          navigator.nextPage(CountryOfNationalityInTheUkYesNoPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfNationalityController.onPageLoad(draftId))

        }

        "CountryOfNationality -> CountryOfResidence Yes No page" in {

          val answers = baseAnswers
            .set(CountryOfNationalityPage, "ES").success.value

          navigator.nextPage(CountryOfNationalityPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfResidenceYesNoController.onPageLoad(draftId))

        }

        "CountryOfResidenceYesNoPage -> Yes -> CountryOfResidenceInTheUk Yes No page" in {

          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage, true).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfResidenceInTheUkYesNoController.onPageLoad(draftId))

        }

        "CountryOfResidenceYesNoPage -> No -> DeceasedSettlorAnswer page" in {

          val answers = baseAnswers
            .set(CountryOfResidenceYesNoPage, false).success.value

          navigator.nextPage(CountryOfResidenceYesNoPage, draftId)(answers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(draftId))

        }

        "CountryOfResidenceInTheUk -> No -> CountryOfResidence page" in {

          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage, false).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, draftId)(answers)
            .mustBe(mld5Rts.CountryOfResidenceController.onPageLoad(draftId))

        }


        "CountryOfResidenceInTheUk -> Yes -> DeceasedSettlorAnswer page" in {

          val answers = baseAnswers
            .set(CountryOfResidenceInTheUkYesNoPage, true).success.value

          navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, draftId)(answers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(draftId))

        }

        "CountryOfResidence -> DeceasedSettlorAnswer page" in {

          val answers = baseAnswers
            .set(CountryOfResidencePage, "ES").success.value

          navigator.nextPage(CountryOfResidencePage, draftId)(answers)
            .mustBe(routes.DeceasedSettlorAnswerController.onPageLoad(draftId))

        }
      }

    }

  }
}
