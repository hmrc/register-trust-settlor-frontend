/*
 * Copyright 2024 HM Revenue & Customs
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
import controllers.living_settlor.individual.mld5.routes._
import controllers.living_settlor.individual.routes._
import controllers.routes._
import models.UserAnswers
import pages.living_settlor.individual._
import pages.living_settlor.individual.mld5._

class IndividualSettlorNavigatorSpec extends SpecBase {

  private val navigator: IndividualSettlorNavigator = injector.instanceOf[IndividualSettlorNavigator]
  private val index: Int                            = 0
  private val nino: String                          = "nino"

  "IndividualSettlor Navigator" when {

    "taxable" when {

      val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = true)

      "SettlorAliveYesNoPage" must {
        val page = SettlorAliveYesNoPage(index)
        "redirect to settlor individual name" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(SettlorIndividualNameController.onPageLoad(index, fakeDraftId))
      }

      "SettlorIndividualNamePage" must {
        val page = SettlorIndividualNamePage(index)
        "redirect to date of birth yes/no" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(SettlorIndividualDateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
      }

      "SettlorIndividualDateOfBirthYesNoPage" when {
        val page = SettlorIndividualDateOfBirthYesNoPage(index)
        "yes" must {
          "redirect to date of birth" in {
            val userAnswers = baseAnswers.set(page, true).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualDateOfBirthController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to country of nationality yes/no" in {
            val userAnswers = baseAnswers.set(page, false).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualDateOfBirthPage" must {
        val page = SettlorIndividualDateOfBirthPage(index)
        "redirect to country of nationality yes/no" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
      }

      "CountryOfNationalityYesNoPage" when {
        val page = CountryOfNationalityYesNoPage(index)
        "yes" must {
          "redirect to uk country of nationality yes/no" in {
            val userAnswers = baseAnswers.set(page, true).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(UkCountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to NINO yes/no" in {
            val userAnswers = baseAnswers.set(page, false).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualNINOYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "UkCountryOfNationalityYesNoPage" when {
        val page = UkCountryOfNationalityYesNoPage(index)
        "yes" must {
          "redirect to NINO yes/no" in {
            val userAnswers = baseAnswers.set(page, true).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualNINOYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to country of nationality" in {
            val userAnswers = baseAnswers.set(page, false).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(CountryOfNationalityController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "CountryOfNationalityPage" must {
        val page = CountryOfNationalityPage(index)
        "redirect to NINO yes/no" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(SettlorIndividualNINOYesNoController.onPageLoad(index, fakeDraftId))
      }

      "SettlorIndividualNINOYesNoPage" when {
        val page = SettlorIndividualNINOYesNoPage(index)
        "yes" must {
          "redirect to NINO" in {
            val userAnswers = baseAnswers.set(page, true).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualNINOController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to country of residency yes/no" in {
            val userAnswers = baseAnswers.set(page, false).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(CountryOfResidencyYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualNINOPage" must {
        val page = SettlorIndividualNINOPage(index)
        "redirect to country of residency yes/no" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(CountryOfResidencyYesNoController.onPageLoad(index, fakeDraftId))
      }

      "CountryOfResidencyYesNoPage" when {
        val page = CountryOfResidencyYesNoPage(index)
        "yes" must {
          "redirect to uk country of residency yes/no" in {
            val userAnswers = baseAnswers.set(page, true).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(UkCountryOfResidencyYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" when {

          "NINO known" must {
            "redirect to legally incapable yes/no if the settlor is alive at registration" in {
              val userAnswers = baseAnswers
                .set(SettlorAliveYesNoPage(index), true)
                .success
                .value
                .set(SettlorIndividualNINOPage(index), nino)
                .success
                .value
                .set(page, false)
                .success
                .value

              navigator
                .nextPage(page, fakeDraftId)(userAnswers)
                .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
            }

            "redirect to check answers if the settlor is not alive at registration" in {
              val userAnswers = baseAnswers
                .set(SettlorAliveYesNoPage(index), false)
                .success
                .value
                .set(SettlorIndividualNINOPage(index), nino)
                .success
                .value
                .set(page, false)
                .success
                .value

              navigator
                .nextPage(page, fakeDraftId)(userAnswers)
                .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
            }
          }

          "NINO not known" must {
            "redirect to address yes/no" in {
              val userAnswers = baseAnswers.set(page, false).success.value

              navigator
                .nextPage(page, fakeDraftId)(userAnswers)
                .mustBe(SettlorIndividualAddressYesNoController.onPageLoad(index, fakeDraftId))
            }
          }
        }
      }

      "UkCountryOfResidencyYesNoPage" when {
        val page = UkCountryOfResidencyYesNoPage(index)
        "yes" when {

          "NINO known" must {
            "redirect to legally incapable yes/no if the settlor is alive at registration" in {
              val userAnswers = baseAnswers
                .set(SettlorAliveYesNoPage(index), true)
                .success
                .value
                .set(SettlorIndividualNINOPage(index), nino)
                .success
                .value
                .set(page, true)
                .success
                .value

              navigator
                .nextPage(page, fakeDraftId)(userAnswers)
                .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
            }

            "redirect to check answers if the settlor is not alive at registration" in {
              val userAnswers = baseAnswers
                .set(SettlorAliveYesNoPage(index), false)
                .success
                .value
                .set(SettlorIndividualNINOPage(index), nino)
                .success
                .value
                .set(page, true)
                .success
                .value

              navigator
                .nextPage(page, fakeDraftId)(userAnswers)
                .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
            }
          }

          "NINO not known" must {
            "redirect to address yes/no" in {
              val userAnswers = baseAnswers.set(page, true).success.value

              navigator
                .nextPage(page, fakeDraftId)(userAnswers)
                .mustBe(SettlorIndividualAddressYesNoController.onPageLoad(index, fakeDraftId))
            }
          }
        }

        "no" must {
          "redirect to country of residency" in {
            val userAnswers = baseAnswers.set(page, false).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(CountryOfResidencyController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "CountryOfResidencyPage" when {
        val page = CountryOfResidencyPage(index)
        "NINO known" must {
          "redirect to legally incapable yes/no if the settlor is alive at registration" in {
            val userAnswers = baseAnswers
              .set(SettlorAliveYesNoPage(index), true)
              .success
              .value
              .set(SettlorIndividualNINOPage(index), nino)
              .success
              .value
              .set(page, "FR")
              .success
              .value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
          }

          "redirect to check answers if the settlor is not alive at registration" in {
            val userAnswers = baseAnswers
              .set(SettlorAliveYesNoPage(index), false)
              .success
              .value
              .set(SettlorIndividualNINOPage(index), nino)
              .success
              .value
              .set(page, "FR")
              .success
              .value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
          }
        }

        "NINO not known" must {
          "redirect to address yes/no" in
            navigator
              .nextPage(page, fakeDraftId)(baseAnswers)
              .mustBe(SettlorIndividualAddressYesNoController.onPageLoad(index, fakeDraftId))
        }
      }

      "SettlorAddressYesNoPage" when {
        val page = SettlorAddressYesNoPage(index)
        "yes" when {

          "country of residency not known" must {
            "redirect to address UK yes/no" in {
              val userAnswers = baseAnswers.set(page, true).success.value

              navigator
                .nextPage(page, fakeDraftId)(userAnswers)
                .mustBe(SettlorIndividualAddressUKYesNoController.onPageLoad(index, fakeDraftId))
            }
          }

          "UK country of residency" must {
            "redirect to address UK yes/no" in {
              val userAnswers = baseAnswers
                .set(CountryOfResidencyPage(index), "GB")
                .success
                .value
                .set(page, true)
                .success
                .value

              navigator
                .nextPage(page, fakeDraftId)(userAnswers)
                .mustBe(SettlorIndividualAddressUKYesNoController.onPageLoad(index, fakeDraftId))
            }
          }

          "non-UK country of residency" must {
            "redirect to address UK yes/no" in {
              val userAnswers = baseAnswers
                .set(CountryOfResidencyPage(index), "FR")
                .success
                .value
                .set(page, true)
                .success
                .value

              navigator
                .nextPage(page, fakeDraftId)(userAnswers)
                .mustBe(SettlorIndividualAddressUKYesNoController.onPageLoad(index, fakeDraftId))
            }
          }
        }

        "no" must {
          "redirect to legally incapable yes/no if the settlor is alive at registration" in {
            val userAnswers = baseAnswers
              .set(SettlorAliveYesNoPage(index), true)
              .success
              .value
              .set(page, false)
              .success
              .value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
          }

          "redirect to check answers if the settlor is not alive at registration" in {
            val userAnswers = baseAnswers
              .set(SettlorAliveYesNoPage(index), false)
              .success
              .value
              .set(page, false)
              .success
              .value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorAddressUKYesNoPage" when {
        val page = SettlorAddressUKYesNoPage(index)
        "yes" must {
          "redirect to UK address" in {
            val userAnswers = baseAnswers.set(page, true).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualAddressUKController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to international address" in {
            val userAnswers = baseAnswers.set(page, false).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualAddressInternationalController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorAddressUKPage" must {
        val page = SettlorAddressUKPage(index)
        "redirect to passport yes/no" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(SettlorIndividualPassportYesNoController.onPageLoad(index, fakeDraftId))
      }

      "SettlorAddressInternationalPage" must {
        val page = SettlorAddressInternationalPage(index)
        "redirect to passport yes/no" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(SettlorIndividualPassportYesNoController.onPageLoad(index, fakeDraftId))
      }

      "SettlorIndividualPassportYesNoPage" when {
        val page = SettlorIndividualPassportYesNoPage(index)
        "yes" must {
          "redirect to passport" in {
            val userAnswers = baseAnswers.set(page, true).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualPassportController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to ID card yes/no" in {
            val userAnswers = baseAnswers.set(page, false).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualIDCardYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualPassportPage" must {
        val page = SettlorIndividualPassportPage(index)
        "redirect to legally incapable yes/no if the settlor is alive at registration" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers.set(SettlorAliveYesNoPage(index), true).success.value)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))

        "redirect to check answers if the settlor is not alive at registration" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers.set(SettlorAliveYesNoPage(index), false).success.value)
            .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
      }

      "SettlorIndividualIDCardYesNoPage" when {
        val page = SettlorIndividualIDCardYesNoPage(index)
        "yes" must {
          "redirect to ID card" in {
            val userAnswers = baseAnswers.set(page, true).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualIDCardController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to legally incapable yes/no if the settlor is alive at registration" in {
            val userAnswers = baseAnswers
              .set(SettlorAliveYesNoPage(index), true)
              .success
              .value
              .set(page, false)
              .success
              .value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
          }

          "redirect to check answers if the settlor is not alive at registration" in {
            val userAnswers = baseAnswers
              .set(SettlorAliveYesNoPage(index), false)
              .success
              .value
              .set(page, false)
              .success
              .value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualIDCardPage" must {
        val page = SettlorIndividualIDCardPage(index)
        "redirect to legally incapable yes/no if the settlor is alive at registration" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers.set(SettlorAliveYesNoPage(index), true).success.value)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))

        "redirect to check answers if the settlor is not alive at registration" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers.set(SettlorAliveYesNoPage(index), false).success.value)
            .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
      }

      "LegallyIncapableYesNoPage" must {
        val page = MentalCapacityYesNoPage(index)
        "redirect to check answers" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
      }

      "SettlorIndividualAnswerPage" must {
        val page = SettlorIndividualAnswerPage
        "redirect to add-to page" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(AddASettlorController.onPageLoad(fakeDraftId))
      }
    }

    "non-taxable" when {

      val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = false)

      "SettlorIndividualNamePage" must {
        val page = SettlorIndividualNamePage(index)
        "redirect to date of birth yes/no" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(SettlorIndividualDateOfBirthYesNoController.onPageLoad(index, fakeDraftId))
      }

      "SettlorIndividualDateOfBirthYesNoPage" when {
        val page = SettlorIndividualDateOfBirthYesNoPage(index)
        "yes" must {
          "redirect to date of birth" in {
            val userAnswers = baseAnswers.set(page, true).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualDateOfBirthController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to country of nationality yes/no" in {
            val userAnswers = baseAnswers.set(page, false).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualDateOfBirthPage" must {
        val page = SettlorIndividualDateOfBirthPage(index)
        "redirect to country of nationality yes/no" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
      }

      "CountryOfNationalityYesNoPage" when {
        val page = CountryOfNationalityYesNoPage(index)
        "yes" must {
          "redirect to uk country of nationality yes/no" in {
            val userAnswers = baseAnswers.set(page, true).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(UkCountryOfNationalityYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to country of residency yes/no" in {
            val userAnswers = baseAnswers.set(page, false).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(CountryOfResidencyYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "UkCountryOfNationalityYesNoPage" when {
        val page = UkCountryOfNationalityYesNoPage(index)
        "yes" must {
          "redirect to country of residency yes/no" in {
            val userAnswers = baseAnswers.set(page, true).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(CountryOfResidencyYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to country of nationality" in {
            val userAnswers = baseAnswers.set(page, false).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(CountryOfNationalityController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "CountryOfNationalityPage" must {
        val page = CountryOfNationalityPage(index)
        "redirect to country of residency yes/no" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(CountryOfResidencyYesNoController.onPageLoad(index, fakeDraftId))
      }

      "CountryOfResidencyYesNoPage" when {
        val page = CountryOfResidencyYesNoPage(index)
        "yes" must {
          "redirect to uk country of residency yes/no" in {
            val userAnswers = baseAnswers.set(page, true).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(UkCountryOfResidencyYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to legally incapable yes/no if the settlor is alive at registration" in {
            val userAnswers = baseAnswers
              .set(SettlorAliveYesNoPage(index), true)
              .success
              .value
              .set(page, false)
              .success
              .value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
          }

          "redirect to check answers if the settlor is not alive at registration" in {
            val userAnswers = baseAnswers
              .set(SettlorAliveYesNoPage(index), false)
              .success
              .value
              .set(page, false)
              .success
              .value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "UkCountryOfResidencyYesNoPage" when {
        val page = UkCountryOfResidencyYesNoPage(index)
        "yes" must {
          "redirect to legally incapable yes/no if the settlor is not alive at registration" in {
            val userAnswers = baseAnswers
              .set(SettlorAliveYesNoPage(index), true)
              .success
              .value
              .set(page, true)
              .success
              .value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
          }

          "redirect to check answers if the settlor is not alive at registration" in {
            val userAnswers = baseAnswers
              .set(SettlorAliveYesNoPage(index), false)
              .success
              .value
              .set(page, true)
              .success
              .value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to country of residency" in {
            val userAnswers = baseAnswers.set(page, false).success.value

            navigator
              .nextPage(page, fakeDraftId)(userAnswers)
              .mustBe(CountryOfResidencyController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "CountryOfResidencyPage" must {
        val page = CountryOfResidencyPage(index)
        "redirect to legally incapable yes/no if the settlor is alive at registration" in {
          val userAnswers = baseAnswers
            .set(SettlorAliveYesNoPage(index), true)
            .success
            .value
            .set(page, "FR")
            .success
            .value

          navigator
            .nextPage(page, fakeDraftId)(userAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(index, fakeDraftId))
        }

        "redirect to check answers if the settlor is not alive at registration" in {
          val userAnswers = baseAnswers.set(page, "FR").success.value

          navigator
            .nextPage(page, fakeDraftId)(userAnswers)
            .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
        }
      }

      "LegallyIncapableYesNoPage" must {
        val page = MentalCapacityYesNoPage(index)
        "redirect to check answers" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
      }

      "SettlorIndividualAnswerPage" must {
        val page = SettlorIndividualAnswerPage
        "redirect to add-to page" in
          navigator
            .nextPage(page, fakeDraftId)(baseAnswers)
            .mustBe(AddASettlorController.onPageLoad(fakeDraftId))
      }
    }
  }

}
