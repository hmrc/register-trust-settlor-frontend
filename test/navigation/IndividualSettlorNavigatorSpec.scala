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
import controllers.living_settlor.individual.routes._
import controllers.living_settlor.individual.mld5.routes._
import controllers.routes._
import models.{Mode, NormalMode}
import pages.living_settlor.individual._
import pages.living_settlor.individual.mld5._

class IndividualSettlorNavigatorSpec extends SpecBase {

  private val navigator: IndividualSettlorNavigator = injector.instanceOf[IndividualSettlorNavigator]
  private val mode: Mode = NormalMode
  private val index: Int = 0
  private val nino: String = "nino"

  "IndividualSettlor Navigator" when {
    
    "5mld not enabled" when {
      
      val is5mldEnabled: Boolean = false
      
      "SettlorIndividualNamePage" must {
        val page = SettlorIndividualNamePage(index)
        "redirect to date of birth yes/no" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "SettlorIndividualDateOfBirthYesNoPage" when {
        val page = SettlorIndividualDateOfBirthYesNoPage(index)
        "yes" must {
          "redirect to date of birth" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualDateOfBirthController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to NINO yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualDateOfBirthPage" must {
        val page = SettlorIndividualDateOfBirthPage(index)
        "redirect to NINO yes/no" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "SettlorIndividualNINOYesNoPage" when {
        val page = SettlorIndividualNINOYesNoPage(index)
        "yes" must {
          "redirect to NINO" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualNINOController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to address yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualNINOPage" must {
        val page = SettlorIndividualNINOPage(index)
        "redirect to check answers" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
        }
      }

      "SettlorAddressYesNoPage" when {
        val page = SettlorAddressYesNoPage(index)
        "yes" must {
          "redirect to address UK yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to check answers" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorAddressUKYesNoPage" when {
        val page = SettlorAddressUKYesNoPage(index)
        "yes" must {
          "redirect to UK address" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualAddressUKController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to international address" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualAddressInternationalController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "SettlorAddressUKPage" must {
        val page = SettlorAddressUKPage(index)
        "redirect to passport yes/no" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(SettlorIndividualPassportYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "SettlorAddressInternationalPage" must {
        val page = SettlorAddressInternationalPage(index)
        "redirect to passport yes/no" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(SettlorIndividualPassportYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "SettlorIndividualPassportYesNoPage" when {
        val page = SettlorIndividualPassportYesNoPage(index)
        "yes" must {
          "redirect to passport" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualPassportController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to ID card yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualIDCardYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualPassportPage" must {
        val page = SettlorIndividualPassportPage(index)
        "redirect to check answers" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
        }
      }

      "SettlorIndividualIDCardYesNoPage" when {
        val page = SettlorIndividualIDCardYesNoPage(index)
        "yes" must {
          "redirect to ID card" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualIDCardController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to check answers" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualIDCardPage" must {
        val page = SettlorIndividualIDCardPage(index)
        "redirect to check answers" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
        }
      }

      "SettlorIndividualAnswerPage" must {
        val page = SettlorIndividualAnswerPage
        "redirect to add-to page" in {
          navigator.nextPage(page, mode, fakeDraftId)(emptyUserAnswers)
            .mustBe(AddASettlorController.onPageLoad(fakeDraftId))
        }
      }
    }

    "5mld enabled" when {

      val is5mldEnabled: Boolean = true

      "SettlorIndividualNamePage" must {
        val page = SettlorIndividualNamePage(index)
        "redirect to date of birth yes/no" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "SettlorIndividualDateOfBirthYesNoPage" when {
        val page = SettlorIndividualDateOfBirthYesNoPage(index)
        "yes" must {
          "redirect to date of birth" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualDateOfBirthController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to country of nationality yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(CountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualDateOfBirthPage" must {
        val page = SettlorIndividualDateOfBirthPage(index)
        "redirect to country of nationality yes/no" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(CountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "CountryOfNationalityYesNoPage" when {
        val page = CountryOfNationalityYesNoPage(index)
        "yes" must {
          "redirect to uk country of nationality yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(UkCountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to NINO yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "UkCountryOfNationalityYesNoPage" when {
        val page = UkCountryOfNationalityYesNoPage(index)
        "yes" must {
          "redirect to NINO yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to country of nationality" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(CountryOfNationalityController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "CountryOfNationalityPage" must {
        val page = CountryOfNationalityPage(index)
        "redirect to NINO yes/no" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "SettlorIndividualNINOYesNoPage" when {
        val page = SettlorIndividualNINOYesNoPage(index)
        "yes" must {
          "redirect to NINO" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualNINOController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to country of residency yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(CountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualNINOPage" must {
        val page = SettlorIndividualNINOPage(index)
        "redirect to country of residency yes/no" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(CountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "CountryOfResidencyYesNoPage" when {
        val page = CountryOfResidencyYesNoPage(index)
        "yes" must {
          "redirect to uk country of residency yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(UkCountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" when {

          "NINO known" must {
            "redirect to legally incapable yes/no" in {
              val userAnswers = emptyUserAnswers
                .set(SettlorIndividualNINOPage(index), nino).success.value
                .set(page, false).success.value

              navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
                .mustBe(MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId))
            }
          }

          "NINO not known" must {
            "redirect to address yes/no" in {
              val userAnswers = emptyUserAnswers.set(page, false).success.value

              navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
                .mustBe(SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId))
            }
          }
        }
      }

      "UkCountryOfResidencyYesNoPage" when {
        val page = UkCountryOfResidencyYesNoPage(index)
        "yes" when {

          "NINO known" must {
            "redirect to legally incapable yes/no" in {
              val userAnswers = emptyUserAnswers
                .set(SettlorIndividualNINOPage(index), nino).success.value
                .set(page, true).success.value

              navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
                .mustBe(MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId))
            }
          }

          "NINO not known" must {
            "redirect to address yes/no" in {
              val userAnswers = emptyUserAnswers.set(page, true).success.value

              navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
                .mustBe(SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId))
            }
          }
        }

        "no" must {
          "redirect to country of residency" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(CountryOfResidencyController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "CountryOfResidencyPage" when {
        val page = CountryOfResidencyPage(index)
        "NINO known" must {
          "redirect to legally incapable yes/no" in {
            val userAnswers = emptyUserAnswers
              .set(SettlorIndividualNINOPage(index), nino).success.value
              .set(page, "FR").success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "NINO not known" must {
          "redirect to address yes/no" in {
            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
              .mustBe(SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "SettlorAddressYesNoPage" when {
        val page = SettlorAddressYesNoPage(index)
        "yes" when {

          "country of residency not known" must {
            "redirect to address UK yes/no" in {
              val userAnswers = emptyUserAnswers.set(page, true).success.value

              navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
                .mustBe(SettlorIndividualAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId))
            }
          }

          "UK country of residency" must {
            "redirect to UK address" in {
              val userAnswers = emptyUserAnswers
                .set(CountryOfResidencyPage(index), "GB").success.value
                .set(page, true).success.value

              navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
                .mustBe(SettlorIndividualAddressUKController.onPageLoad(mode, index, fakeDraftId))
            }
          }

          "non-UK country of residency" must {
            "redirect to non-UK address" in {
              val userAnswers = emptyUserAnswers
                .set(CountryOfResidencyPage(index), "FR").success.value
                .set(page, true).success.value

              navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
                .mustBe(SettlorIndividualAddressInternationalController.onPageLoad(mode, index, fakeDraftId))
            }
          }
        }

        "no" must {
          "redirect to legally incapable yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "SettlorAddressUKYesNoPage" when {
        val page = SettlorAddressUKYesNoPage(index)
        "yes" must {
          "redirect to UK address" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualAddressUKController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to international address" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualAddressInternationalController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "SettlorAddressUKPage" must {
        val page = SettlorAddressUKPage(index)
        "redirect to passport yes/no" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(SettlorIndividualPassportYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "SettlorAddressInternationalPage" must {
        val page = SettlorAddressInternationalPage(index)
        "redirect to passport yes/no" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(SettlorIndividualPassportYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "SettlorIndividualPassportYesNoPage" when {
        val page = SettlorIndividualPassportYesNoPage(index)
        "yes" must {
          "redirect to passport" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualPassportController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to ID card yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualIDCardYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualPassportPage" must {
        val page = SettlorIndividualPassportPage(index)
        "redirect to legally incapable yes/no" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "SettlorIndividualIDCardYesNoPage" when {
        val page = SettlorIndividualIDCardYesNoPage(index)
        "yes" must {
          "redirect to ID card" in {
            val userAnswers = emptyUserAnswers.set(page, true).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(SettlorIndividualIDCardController.onPageLoad(mode, index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to legally incapable yes/no" in {
            val userAnswers = emptyUserAnswers.set(page, false).success.value

            navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(userAnswers)
              .mustBe(MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId))
          }
        }
      }

      "SettlorIndividualIDCardPage" must {
        val page = SettlorIndividualIDCardPage(index)
        "redirect to legally incapable yes/no" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId))
        }
      }

      "LegallyIncapableYesNoPage" must {
        val page = MentalCapacityYesNoPage(index)
        "redirect to check answers" in {
          navigator.nextPage(page, mode, fakeDraftId, is5mldEnabled = is5mldEnabled)(emptyUserAnswers)
            .mustBe(SettlorIndividualAnswerController.onPageLoad(index, fakeDraftId))
        }
      }

      "SettlorIndividualAnswerPage" must {
        val page = SettlorIndividualAnswerPage
        "redirect to add-to page" in {
          navigator.nextPage(page, mode, fakeDraftId)(emptyUserAnswers)
            .mustBe(AddASettlorController.onPageLoad(fakeDraftId))
        }
      }
    }
  }
}
