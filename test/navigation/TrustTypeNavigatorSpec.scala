/*
 * Copyright 2023 HM Revenue & Customs
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
import models.UserAnswers
import models.pages.KindOfTrust._
import pages.trust_type._

class TrustTypeNavigatorSpec extends SpecBase {

  private val navigator: TrustTypeNavigator = injector.instanceOf[TrustTypeNavigator]

  "TrustType Navigator" when {

    "taxable" when {

      val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = true)

      "SetUpAfterSettlorDiedYesNoPage" when {

        "yes" must {
          "redirect to Deceased Settlor Name" in {
            val userAnswers = baseAnswers.set(SetUpAfterSettlorDiedYesNoPage, true).success.value

            navigator.nextPage(SetUpAfterSettlorDiedYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.deceased_settlor.routes.SettlorsNameController.onPageLoad(fakeDraftId))
          }
        }

        "no" must {
          "redirect to Kind of Trust" in {
            val userAnswers = baseAnswers.set(SetUpAfterSettlorDiedYesNoPage, false).success.value

            navigator.nextPage(SetUpAfterSettlorDiedYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.trust_type.routes.KindOfTrustController.onPageLoad(fakeDraftId))
          }
        }
      }

      "KindOfTrustPage" when {

        "Deed" must {
          "redirect to Set up in addition to will trust" in {
            val userAnswers = baseAnswers.set(KindOfTrustPage, Deed).success.value

            navigator.nextPage(KindOfTrustPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.trust_type.routes.AdditionToWillTrustYesNoController.onPageLoad(fakeDraftId))
          }
        }

        "Intervivos" must {
          "redirect to Holdover relief yes/no" in {
            val userAnswers = baseAnswers.set(KindOfTrustPage, Intervivos).success.value

            navigator.nextPage(KindOfTrustPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.trust_type.routes.HoldoverReliefYesNoController.onPageLoad(fakeDraftId))
          }
        }

        "FlatManagement" must {
          "redirect to Individual or Business" in {
            val userAnswers = baseAnswers.set(KindOfTrustPage, FlatManagement).success.value

            navigator.nextPage(KindOfTrustPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(0, fakeDraftId))
          }
        }

        "HeritageMaintenanceFund" must {
          "redirect to Individual or Business" in {
            val userAnswers = baseAnswers.set(KindOfTrustPage, HeritageMaintenanceFund).success.value

            navigator.nextPage(KindOfTrustPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(0, fakeDraftId))
          }
        }

        "Employees" must {
          "redirect to EFRBS yes/no" in {
            val userAnswers = baseAnswers.set(KindOfTrustPage, Employees).success.value

            navigator.nextPage(KindOfTrustPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.trust_type.routes.EmployerFinancedRbsYesNoController.onPageLoad(fakeDraftId))
          }
        }
      }

      "EfrbsYesNoPage" when {

        "yes" must {
          "redirect to EFRBS start date" in {
            val userAnswers = baseAnswers.set(EfrbsYesNoPage, true).success.value

            navigator.nextPage(EfrbsYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.trust_type.routes.EmployerFinancedRbsStartDateController.onPageLoad(fakeDraftId))
          }
        }

        "no" must {
          "redirect to Individual or Business" in {
            val userAnswers = baseAnswers.set(EfrbsYesNoPage, false).success.value

            navigator.nextPage(EfrbsYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(0, fakeDraftId))
          }
        }
      }

      "SetUpInAdditionToWillTrustYesNoPage" when {

        "yes" must {
          "redirect to Deceased Settlor Name" in {
            val userAnswers = baseAnswers.set(SetUpInAdditionToWillTrustYesNoPage, true).success.value

            navigator.nextPage(SetUpInAdditionToWillTrustYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.deceased_settlor.routes.SettlorsNameController.onPageLoad(fakeDraftId))
          }
        }

        "no" must {
          "redirect to How was deed of variation created" in {
            val userAnswers = baseAnswers.set(SetUpInAdditionToWillTrustYesNoPage, false).success.value

            navigator.nextPage(SetUpInAdditionToWillTrustYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.trust_type.routes.HowDeedOfVariationCreatedController.onPageLoad(fakeDraftId))
          }
        }
      }

      "EfrbsStartDatePage" must {
        "redirect to Individual or Business" in {
          navigator.nextPage(EfrbsStartDatePage, fakeDraftId)(baseAnswers)
            .mustBe(controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(0, fakeDraftId))
        }
      }

      "HowDeedOfVariationCreatedPage" must {
        "redirect to Individual or Business" in {
          navigator.nextPage(HowDeedOfVariationCreatedPage, fakeDraftId)(baseAnswers)
            .mustBe(controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(0, fakeDraftId))
        }
      }

      "HoldoverReliefYesNoPage" must {
        "redirect to Individual or Business" in {
          navigator.nextPage(HoldoverReliefYesNoPage, fakeDraftId)(baseAnswers)
            .mustBe(controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(0, fakeDraftId))
        }
      }
    }

    "non-taxable" when {

      val baseAnswers: UserAnswers = emptyUserAnswers.copy(isTaxable = false)

      "SetUpAfterSettlorDiedYesNoPage" when {

        "yes" must {
          "redirect to Deceased Settlor Name" in {
            val userAnswers = baseAnswers.set(SetUpAfterSettlorDiedYesNoPage, true).success.value

            navigator.nextPage(SetUpAfterSettlorDiedYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.deceased_settlor.routes.SettlorsNameController.onPageLoad(fakeDraftId))
          }
        }

        "no" must {
          "redirect to Individual or Business" in {
            val userAnswers = baseAnswers.set(SetUpAfterSettlorDiedYesNoPage, false).success.value

            navigator.nextPage(SetUpAfterSettlorDiedYesNoPage, fakeDraftId)(userAnswers)
              .mustBe(controllers.living_settlor.routes.SettlorIndividualOrBusinessController.onPageLoad(0, fakeDraftId))
          }
        }
      }
    }
  }
}
