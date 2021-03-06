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

package mapping

import base.SpecBase
import models.{TrustDetailsType, UserAnswers}
import models.pages.DeedOfVariation._
import models.pages.KindOfTrust._
import models.pages.{FullName, IndividualOrBusiness, KindOfTrust}
import pages.deceased_settlor.SettlorsNamePage
import pages.living_settlor._
import pages.living_settlor.individual.SettlorIndividualNamePage
import pages.trust_type._

import java.time.LocalDate

class TrustDetailsMapperSpec extends SpecBase {

  private val fullName: FullName = FullName("Joe", None, "Bloggs")

  "TrustDetails mapper" when {

    val mapper: TrustDetailsMapper = injector.instanceOf[TrustDetailsMapper]

    "taxable" must {

      val flaggedAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = false, isTaxable = true)

      "not map user answers to trust details model" when {

        "no settlors" in {

          val result = mapper.build(flaggedAnswers)

          result mustBe None
        }

        "invalid user answers due to having living and deceased settlors" in {

          val userAnswers = flaggedAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
            .set(SettlorsNamePage, fullName).success.value
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(0), fullName).success.value

          val result = mapper.build(userAnswers)

          result mustBe None
        }
      }

      "map user answers to trust details model" when {

        val setupAfterSettlorDiedAnswers = flaggedAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, true).success.value

        "setup after settlor died" in {

          val userAnswers = setupAfterSettlorDiedAnswers
            .set(SettlorsNamePage, fullName).success.value

          val result = mapper.build(userAnswers).get

          result mustBe TrustDetailsType(
            typeOfTrust = TypeOfTrust.WillTrustOrIntestacyTrust,
            deedOfVariation = None,
            interVivos = None,
            efrbsStartDate = None
          )
        }

        "not setup after settlor died" when {

          val setupAfterSettlorDiedAnswers = flaggedAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value

          "deed of variation" when {

            val baseAnswers = setupAfterSettlorDiedAnswers
              .set(KindOfTrustPage, KindOfTrust.Deed).success.value

            "set up in addition to will trust" in {

              val userAnswers = baseAnswers
                .set(SetUpInAdditionToWillTrustYesNoPage, true).success.value
                .set(SettlorsNamePage, fullName).success.value

              val result = mapper.build(userAnswers).get

              result mustBe models.TrustDetailsType(
                typeOfTrust = TypeOfTrust.WillTrustOrIntestacyTrust,
                deedOfVariation = Some(AdditionToWill),
                interVivos = None,
                efrbsStartDate = None
              )
            }

            "not set up in addition to will trust" in {

              val userAnswers = baseAnswers
                .set(SetUpInAdditionToWillTrustYesNoPage, false).success.value
                .set(HowDeedOfVariationCreatedPage, ReplacedWill).success.value
                .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
                .set(SettlorIndividualNamePage(0), fullName).success.value

              val result = mapper.build(userAnswers).get

              result mustBe models.TrustDetailsType(
                typeOfTrust = TypeOfTrust.DeedOfVariation,
                deedOfVariation = Some(ReplacedWill),
                interVivos = None,
                efrbsStartDate = None
              )
            }
          }

          "intervivos" in {

            val holdoverReliefYesNo: Boolean = true

            val userAnswers = setupAfterSettlorDiedAnswers
              .set(KindOfTrustPage, KindOfTrust.Intervivos).success.value
              .set(HoldoverReliefYesNoPage, holdoverReliefYesNo).success.value
              .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
              .set(SettlorIndividualNamePage(0), fullName).success.value

            val result = mapper.build(userAnswers).get

            result mustBe models.TrustDetailsType(
              typeOfTrust = TypeOfTrust.IntervivosSettlementTrust,
              deedOfVariation = None,
              interVivos = Some(holdoverReliefYesNo),
              efrbsStartDate = None
            )
          }

          "flat management" in {

            val userAnswers = setupAfterSettlorDiedAnswers
              .set(KindOfTrustPage, KindOfTrust.FlatManagement).success.value
              .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
              .set(SettlorIndividualNamePage(0), fullName).success.value

            val result = mapper.build(userAnswers).get

            result mustBe models.TrustDetailsType(
              typeOfTrust = TypeOfTrust.FlatManagementTrust,
              deedOfVariation = None,
              interVivos = None,
              efrbsStartDate = None
            )
          }

          "heritage maintenance fund" in {

            val userAnswers = setupAfterSettlorDiedAnswers
              .set(KindOfTrustPage, KindOfTrust.HeritageMaintenanceFund).success.value
              .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
              .set(SettlorIndividualNamePage(0), fullName).success.value

            val result = mapper.build(userAnswers).get

            result mustBe models.TrustDetailsType(
              typeOfTrust = TypeOfTrust.HeritageTrust,
              deedOfVariation = None,
              interVivos = None,
              efrbsStartDate = None
            )
          }

          "employees" when {

            "efrbs" in {

              val date: LocalDate = LocalDate.parse("1996-02-03")

              val userAnswers = setupAfterSettlorDiedAnswers
                .set(KindOfTrustPage, KindOfTrust.Employees).success.value
                .set(EfrbsYesNoPage, true).success.value
                .set(EfrbsStartDatePage, date).success.value
                .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
                .set(SettlorIndividualNamePage(0), fullName).success.value

              val result = mapper.build(userAnswers).get

              result mustBe models.TrustDetailsType(
                typeOfTrust = TypeOfTrust.EmployeeRelated,
                deedOfVariation = None,
                interVivos = None,
                efrbsStartDate = Some(date)
              )
            }

            "not efrbs" in {

              val userAnswers = setupAfterSettlorDiedAnswers
                .set(KindOfTrustPage, KindOfTrust.Employees).success.value
                .set(EfrbsYesNoPage, false).success.value
                .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
                .set(SettlorIndividualNamePage(0), fullName).success.value

              val result = mapper.build(userAnswers).get

              result mustBe models.TrustDetailsType(
                typeOfTrust = TypeOfTrust.EmployeeRelated,
                deedOfVariation = None,
                interVivos = None,
                efrbsStartDate = None
              )
            }
          }
        }
      }
    }

    "non-taxable" must {

      val flaggedAnswers: UserAnswers = emptyUserAnswers.copy(is5mldEnabled = true, isTaxable = false)

      "return None" when {

        "set up after settlor died" in {
          val answers: UserAnswers = flaggedAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
            .set(SettlorsNamePage, fullName).success.value

          val result = mapper.build(answers)
          result mustBe None
        }

        "not set up after settlor died" in {
          val answers: UserAnswers = flaggedAnswers
            .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
            .set(SettlorIndividualNamePage(0), fullName).success.value

          val result = mapper.build(answers)
          result mustBe None
        }
      }
    }
  }
}
