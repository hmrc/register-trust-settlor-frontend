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
import controllers.living_settlor.business.mld5.{routes => mld5Routes}
import controllers.living_settlor.business.routes
import models.UserAnswers
import models.pages.KindOfTrust.{Employees, Intervivos}
import pages.living_settlor.business._
import pages.living_settlor.business.mld5.{
  CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage
}
import pages.trust_type.KindOfTrustPage

class BusinessSettlorNavigatorSpec extends SpecBase {

  private val navigator: BusinessSettlorNavigator = injector.instanceOf[BusinessSettlorNavigator]
  private val index: Int                          = 0

  "BusinessSettlor Navigator" when {

    "taxable" when {

      val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = true)

      "SettlorBusinessNamePage" must {
        "redirect to UTR yes/no" in
          navigator
            .nextPage(SettlorBusinessNamePage(index), fakeDraftId)(baseAnswers)
            .mustBe(routes.SettlorBusinessUtrYesNoController.onPageLoad(index, fakeDraftId))
      }

      "SettlorBusinessUtrYesNoPage" when {

        "yes" must {
          "redirect to UTR" in {
            val userAnswers = baseAnswers.set(SettlorBusinessUtrYesNoPage(index), true).success.value

            navigator
              .nextPage(SettlorBusinessUtrYesNoPage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessUtrController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to address yes/no" in {
            val userAnswers = baseAnswers.set(SettlorBusinessUtrYesNoPage(index), false).success.value

            navigator
              .nextPage(SettlorBusinessUtrYesNoPage(index), fakeDraftId)(userAnswers)
              .mustBe(mld5Routes.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorBusinessUtrPage" when {

        "a trust for the employees of a company" must {
          "redirect to Country of Residence Yes No page" in {
            val userAnswers = baseAnswers
              .set(KindOfTrustPage, Employees)
              .success
              .value
              .set(SettlorBusinessUtrYesNoPage(index), true)
              .success
              .value

            navigator
              .nextPage(SettlorBusinessUtrPage(index), fakeDraftId)(userAnswers)
              .mustBe(mld5Routes.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "not a trust for the employees of a company" must {
          "redirect to Country of Residence Yes No page" in {
            val userAnswers = baseAnswers
              .set(KindOfTrustPage, Intervivos)
              .success
              .value
              .set(SettlorBusinessUtrYesNoPage(index), true)
              .success
              .value

            navigator
              .nextPage(SettlorBusinessUtrPage(index), fakeDraftId)(userAnswers)
              .mustBe(mld5Routes.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "CountryOfResidenceYesNoPage" when {

        "yes" must {
          "redirect to CountryOfResidenceInTheUkYesNoPage" in {
            val userAnswers = baseAnswers
              .set(CountryOfResidenceYesNoPage(index), true)
              .success
              .value

            navigator
              .nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId)(userAnswers)
              .mustBe(mld5Routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" when {
          "for a trust for the employees of a company" must {
            "redirect to address yes/no if no UTR was provided" in {
              val userAnswers = baseAnswers
                .set(KindOfTrustPage, Employees)
                .success
                .value
                .set(SettlorBusinessUtrYesNoPage(index), false)
                .success
                .value
                .set(CountryOfResidenceYesNoPage(index), false)
                .success
                .value

              navigator
                .nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId)(userAnswers)
                .mustBe(routes.SettlorBusinessAddressYesNoController.onPageLoad(index, fakeDraftId))
            }

            "redirect to kind of business if a UTR was provided" in {
              val userAnswers = baseAnswers
                .set(KindOfTrustPage, Employees)
                .success
                .value
                .set(SettlorBusinessUtrYesNoPage(index), true)
                .success
                .value
                .set(CountryOfResidenceYesNoPage(index), false)
                .success
                .value

              navigator
                .nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId)(userAnswers)
                .mustBe(routes.SettlorBusinessTypeController.onPageLoad(index, fakeDraftId))
            }
          }

          "for a trust that is not for the employees of a company" must {
            "redirect to address yes/no if no UTR was provided" in {
              val userAnswers = baseAnswers
                .set(KindOfTrustPage, Intervivos)
                .success
                .value
                .set(SettlorBusinessUtrYesNoPage(index), false)
                .success
                .value
                .set(CountryOfResidenceYesNoPage(index), false)
                .success
                .value

              navigator
                .nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId)(userAnswers)
                .mustBe(routes.SettlorBusinessAddressYesNoController.onPageLoad(index, fakeDraftId))
            }

            "redirect to check answers if a UTR was provided" in {
              val userAnswers = baseAnswers
                .set(KindOfTrustPage, Intervivos)
                .success
                .value
                .set(SettlorBusinessUtrYesNoPage(index), true)
                .success
                .value
                .set(CountryOfResidenceYesNoPage(index), false)
                .success
                .value

              navigator
                .nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId)(userAnswers)
                .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
            }
          }
        }
      }

      "CountryOfResidenceInTheUkYesNoPage" when {

        "no" must {
          "redirect to CountryOfResidencePage" in {
            val userAnswers = baseAnswers
              .set(CountryOfResidenceInTheUkYesNoPage(index), false)
              .success
              .value

            navigator
              .nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId)(userAnswers)
              .mustBe(mld5Routes.CountryOfResidenceController.onPageLoad(index, fakeDraftId))
          }
        }

        "yes" when {
          "for a trust for the employees of a company" must {
            "redirect to address yes/no if no UTR was provided" in {
              val userAnswers = baseAnswers
                .set(KindOfTrustPage, Employees)
                .success
                .value
                .set(SettlorBusinessUtrYesNoPage(index), false)
                .success
                .value
                .set(CountryOfResidenceInTheUkYesNoPage(index), true)
                .success
                .value

              navigator
                .nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId)(userAnswers)
                .mustBe(routes.SettlorBusinessAddressYesNoController.onPageLoad(index, fakeDraftId))
            }

            "redirect to kind of business if a UTR was provided" in {
              val userAnswers = baseAnswers
                .set(KindOfTrustPage, Employees)
                .success
                .value
                .set(SettlorBusinessUtrYesNoPage(index), true)
                .success
                .value
                .set(CountryOfResidenceInTheUkYesNoPage(index), true)
                .success
                .value

              navigator
                .nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId)(userAnswers)
                .mustBe(routes.SettlorBusinessTypeController.onPageLoad(index, fakeDraftId))
            }
          }

          "for a trust that is not for the employees of a company" must {
            "redirect to address yes/no if no UTR was provided" in {
              val userAnswers = baseAnswers
                .set(KindOfTrustPage, Intervivos)
                .success
                .value
                .set(SettlorBusinessUtrYesNoPage(index), false)
                .success
                .value
                .set(CountryOfResidenceInTheUkYesNoPage(index), true)
                .success
                .value

              navigator
                .nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId)(userAnswers)
                .mustBe(routes.SettlorBusinessAddressYesNoController.onPageLoad(index, fakeDraftId))
            }

            "redirect to check answers if a UTR was provided" in {
              val userAnswers = baseAnswers
                .set(KindOfTrustPage, Intervivos)
                .success
                .value
                .set(SettlorBusinessUtrYesNoPage(index), true)
                .success
                .value
                .set(CountryOfResidenceInTheUkYesNoPage(index), true)
                .success
                .value

              navigator
                .nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId)(userAnswers)
                .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
            }
          }
        }
      }

      "CountryOfResidencePage" when {

        "for a trust for the employees of a company" must {
          "redirect to address yes/no if no UTR was provided" in {
            val userAnswers = baseAnswers
              .set(KindOfTrustPage, Employees)
              .success
              .value
              .set(SettlorBusinessUtrYesNoPage(index), false)
              .success
              .value
              .set(CountryOfResidencePage(index), "FR")
              .success
              .value

            navigator
              .nextPage(CountryOfResidencePage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessAddressYesNoController.onPageLoad(index, fakeDraftId))
          }

          "redirect to kind of business if a UTR was provided" in {
            val userAnswers = baseAnswers
              .set(KindOfTrustPage, Employees)
              .success
              .value
              .set(SettlorBusinessUtrYesNoPage(index), true)
              .success
              .value
              .set(CountryOfResidencePage(index), "FR")
              .success
              .value

            navigator
              .nextPage(CountryOfResidencePage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessTypeController.onPageLoad(index, fakeDraftId))
          }
        }

        "for a trust that is not for the employees of a company" must {
          "redirect to address yes/no if no UTR was provided" in {
            val userAnswers = baseAnswers
              .set(KindOfTrustPage, Intervivos)
              .success
              .value
              .set(SettlorBusinessUtrYesNoPage(index), false)
              .success
              .value
              .set(CountryOfResidencePage(index), "FR")
              .success
              .value

            navigator
              .nextPage(CountryOfResidencePage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessAddressYesNoController.onPageLoad(index, fakeDraftId))
          }

          "redirect to check answers if a UTR was provided" in {
            val userAnswers = baseAnswers
              .set(KindOfTrustPage, Intervivos)
              .success
              .value
              .set(SettlorBusinessUtrYesNoPage(index), true)
              .success
              .value
              .set(CountryOfResidencePage(index), "FR")
              .success
              .value

            navigator
              .nextPage(CountryOfResidencePage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorBusinessAddressYesNoPage" when {

        "yes" must {
          "redirect to address UK yes/no" in {
            val userAnswers = baseAnswers.set(SettlorBusinessAddressYesNoPage(index), true).success.value

            navigator
              .nextPage(SettlorBusinessAddressYesNoPage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessAddressUKYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" when {
          "a trust for the employees of a company" must {
            "redirect to kind of business" in {
              val userAnswers = baseAnswers
                .set(KindOfTrustPage, Employees)
                .success
                .value
                .set(SettlorBusinessAddressYesNoPage(index), false)
                .success
                .value

              navigator
                .nextPage(SettlorBusinessAddressYesNoPage(index), fakeDraftId)(userAnswers)
                .mustBe(routes.SettlorBusinessTypeController.onPageLoad(index, fakeDraftId))
            }
          }

          "not a trust for the employees of a company" must {
            "redirect to check answers" in {
              val userAnswers = baseAnswers
                .set(KindOfTrustPage, Intervivos)
                .success
                .value
                .set(SettlorBusinessAddressYesNoPage(index), false)
                .success
                .value

              navigator
                .nextPage(SettlorBusinessAddressYesNoPage(index), fakeDraftId)(userAnswers)
                .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
            }
          }
        }
      }

      "SettlorBusinessAddressUKYesNoPage" when {

        "yes" must {
          "redirect to UK address" in {
            val userAnswers = baseAnswers.set(SettlorBusinessAddressUKYesNoPage(index), true).success.value

            navigator
              .nextPage(SettlorBusinessAddressUKYesNoPage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessAddressUKController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to international address" in {
            val userAnswers = baseAnswers.set(SettlorBusinessAddressUKYesNoPage(index), false).success.value

            navigator
              .nextPage(SettlorBusinessAddressUKYesNoPage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessAddressInternationalController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorBusinessAddressUKPage" when {
        "a trust for the employees of a company" must {
          "redirect to kind of business" in {
            val userAnswers = baseAnswers.set(KindOfTrustPage, Employees).success.value

            navigator
              .nextPage(SettlorBusinessAddressUKPage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessTypeController.onPageLoad(index, fakeDraftId))
          }
        }

        "not a trust for the employees of a company" must {
          "redirect to check answers" in {
            val userAnswers = baseAnswers.set(KindOfTrustPage, Intervivos).success.value

            navigator
              .nextPage(SettlorBusinessAddressUKPage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorBusinessAddressInternationalPage" when {
        "a trust for the employees of a company" must {
          "redirect to kind of business" in {
            val userAnswers = baseAnswers.set(KindOfTrustPage, Employees).success.value

            navigator
              .nextPage(SettlorBusinessAddressInternationalPage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessTypeController.onPageLoad(index, fakeDraftId))
          }
        }

        "not a trust for the employees of a company" must {
          "redirect to check answers" in {
            val userAnswers = baseAnswers.set(KindOfTrustPage, Intervivos).success.value

            navigator
              .nextPage(SettlorBusinessAddressInternationalPage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "SettlorBusinessTypePage" must {
        "redirect to existed for 2 years yes/no" in
          navigator
            .nextPage(SettlorBusinessTypePage(index), fakeDraftId)(baseAnswers)
            .mustBe(routes.SettlorBusinessTimeYesNoController.onPageLoad(index, fakeDraftId))
      }

      "SettlorBusinessTimeYesNoPage" must {
        "redirect to check answers" in
          navigator
            .nextPage(SettlorBusinessTimeYesNoPage(index), fakeDraftId)(baseAnswers)
            .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
      }

      "SettlorBusinessAnswerPage" must {
        "redirect to add-to page" in
          navigator
            .nextPage(SettlorBusinessAnswerPage, fakeDraftId)(baseAnswers)
            .mustBe(controllers.routes.AddASettlorController.onPageLoad(fakeDraftId))
      }
    }

    "non-taxable" when {

      val baseAnswers: UserAnswers = emptyUserAnswers
        .copy(isTaxable = false)
        .set(KindOfTrustPage, Employees)
        .success
        .value // we want to ensure an Employees type of trust does not affect our non-taxable nav

      "SettlorBusinessNamePage" must {
        "redirect to CountryOfResidenceYesNoPage" in
          navigator
            .nextPage(SettlorBusinessNamePage(index), fakeDraftId)(baseAnswers)
            .mustBe(mld5Routes.CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId))
      }

      "CountryOfResidenceYesNoPage" when {

        "yes" must {
          "redirect to CountryOfResidenceInTheUkYesNoPage" in {
            val userAnswers = baseAnswers
              .set(CountryOfResidenceYesNoPage(index), true)
              .success
              .value

            navigator
              .nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId)(userAnswers)
              .mustBe(mld5Routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId))
          }
        }

        "no" must {
          "redirect to check answers" in {
            val userAnswers = baseAnswers
              .set(CountryOfResidenceYesNoPage(index), false)
              .success
              .value

            navigator
              .nextPage(CountryOfResidenceYesNoPage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "CountryOfResidenceInTheUkYesNoPage" when {

        "no" must {
          "redirect to CountryOfResidencePage" in {
            val userAnswers = baseAnswers
              .set(CountryOfResidenceInTheUkYesNoPage(index), false)
              .success
              .value

            navigator
              .nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId)(userAnswers)
              .mustBe(mld5Routes.CountryOfResidenceController.onPageLoad(index, fakeDraftId))
          }
        }

        "yes" must {
          "redirect to check answers" in {
            val userAnswers = baseAnswers
              .set(CountryOfResidenceInTheUkYesNoPage(index), true)
              .success
              .value

            navigator
              .nextPage(CountryOfResidenceInTheUkYesNoPage(index), fakeDraftId)(userAnswers)
              .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
          }
        }
      }

      "CountryOfResidencePage" must {
        "redirect to check answers if a UTR was provided" in {
          val userAnswers = baseAnswers
            .set(CountryOfResidencePage(index), "FR")
            .success
            .value

          navigator
            .nextPage(CountryOfResidencePage(index), fakeDraftId)(userAnswers)
            .mustBe(routes.SettlorBusinessAnswerController.onPageLoad(index, fakeDraftId))
        }
      }

      "SettlorBusinessAnswerPage" must {
        "redirect to add-to page" in
          navigator
            .nextPage(SettlorBusinessAnswerPage, fakeDraftId)(baseAnswers)
            .mustBe(controllers.routes.AddASettlorController.onPageLoad(fakeDraftId))
      }
    }
  }

}
