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

package utils

import base.SpecBase
import controllers.deceased_settlor.{routes => deceasedRoutes}
import controllers.living_settlor.business.{routes => businessRoutes}
import controllers.living_settlor.individual.{routes => individualRoutes}
import controllers.living_settlor.individual.mld5.{routes => individual5mldRoutes}
import controllers.living_settlor.{routes => livingRoutes}
import controllers.trust_type.{routes => trustTypeRoutes}
import models.pages.IndividualOrBusiness._
import models.pages.Status.Completed
import models.pages._
import models.{Mode, NormalMode, UserAnswers}
import pages.living_settlor.individual.{mld5 => individual5mldPages}
import pages.living_settlor.{SettlorIndividualOrBusinessPage, business => businessPages, individual => individualPages}
import pages.{LivingSettlorStatus, deceased_settlor => deceasedPages, trust_type => trustTypePages}
import play.twirl.api.Html
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class CheckYourAnswersHelperSpec extends SpecBase {

  private val countryOptions: CountryOptions = injector.instanceOf[CountryOptions]
  private val checkAnswersFormatters = injector.instanceOf[CheckAnswersFormatters]

  private val mode: Mode = NormalMode

  private val name: FullName = FullName("Joe", Some("Joseph"), "Bloggs")
  private val businessName: String = "Business Ltd."
  private val date: LocalDate = LocalDate.parse("1996-02-03")
  private val dateOfDeath: LocalDate = LocalDate.parse("2010-02-16")
  private val nino: String = "AA000000A"
  private val utr: String = "1234567890"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB11AB")
  private val nonUkAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", Some("Line 3"), "FR")
  private val passportOrIdCardDetails: PassportOrIdCardDetails = PassportOrIdCardDetails("GB", "0987654321", date)
  private val canEdit: Boolean = true

  "Check your answers helper" must {

    "return a deceased settlor answer section" when {

      "trust set up after settlor died" when {

        val baseAnswers: UserAnswers = emptyUserAnswers
          .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, true).success.value

        "no DoD, DoB, address or NINO" in {

          val userAnswers = baseAnswers
            .set(deceasedPages.SettlorsNamePage, name).success.value
            .set(deceasedPages.SettlorDateOfDeathYesNoPage, false).success.value
            .set(deceasedPages.SettlorDateOfBirthYesNoPage, false).success.value
            .set(deceasedPages.SettlorsNationalInsuranceYesNoPage, false).success.value
            .set(deceasedPages.SettlorsLastKnownAddressYesNoPage, false).success.value

          val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

          val result = helper.deceasedSettlor

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = None,
              rows = Seq(
                AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "settlorsName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(deceasedRoutes.SettlorsNameController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "settlorDateOfDeathYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorDateOfDeathYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                AnswerRow(label = "settlorDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorDateOfBirthYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                AnswerRow(label = "settlorsNationalInsuranceYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorsNINoYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                AnswerRow(label = "settlorsLastKnownAddressYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorsLastKnownAddressYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
              ),
              sectionKey = Some(messages("answerPage.section.deceasedSettlor.heading"))
            )
          ))
        }

        "DoB and NINO" in {

          val userAnswers = baseAnswers
            .set(deceasedPages.SettlorsNamePage, name).success.value
            .set(deceasedPages.SettlorDateOfDeathYesNoPage, true).success.value
            .set(deceasedPages.SettlorDateOfDeathPage, dateOfDeath).success.value
            .set(deceasedPages.SettlorDateOfBirthYesNoPage, true).success.value
            .set(deceasedPages.SettlorsDateOfBirthPage, date).success.value
            .set(deceasedPages.SettlorsNationalInsuranceYesNoPage, true).success.value
            .set(deceasedPages.SettlorNationalInsuranceNumberPage, nino).success.value

          val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

          val result = helper.deceasedSettlor

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = None,
              rows = Seq(
                AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "settlorsName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(deceasedRoutes.SettlorsNameController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "settlorDateOfDeathYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(deceasedRoutes.SettlorDateOfDeathYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                AnswerRow(label = "settlorDateOfDeath.checkYourAnswersLabel", answer = Html("16 February 2010"), changeUrl = Some(deceasedRoutes.SettlorDateOfDeathController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                AnswerRow(label = "settlorDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(deceasedRoutes.SettlorDateOfBirthYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                AnswerRow(label = "settlorsDateOfBirth.checkYourAnswersLabel", answer = Html("3 February 1996"), changeUrl = Some(deceasedRoutes.SettlorsDateOfBirthController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                AnswerRow(label = "settlorsNationalInsuranceYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(deceasedRoutes.SettlorsNINoYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                AnswerRow(label = "settlorNationalInsuranceNumber.checkYourAnswersLabel", answer = Html("AA 00 00 00 A"), changeUrl = Some(deceasedRoutes.SettlorNationalInsuranceNumberController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
              ),
              sectionKey = Some(messages("answerPage.section.deceasedSettlor.heading"))
            )
          ))
        }

        "address" when {

          val addressAnswers = baseAnswers
            .set(deceasedPages.SettlorsNamePage, name).success.value
            .set(deceasedPages.SettlorDateOfDeathYesNoPage, false).success.value
            .set(deceasedPages.SettlorDateOfBirthYesNoPage, false).success.value
            .set(deceasedPages.SettlorsNationalInsuranceYesNoPage, false).success.value
            .set(deceasedPages.SettlorsLastKnownAddressYesNoPage, true).success.value

          "UK" in {

            val userAnswers = addressAnswers
              .set(deceasedPages.WasSettlorsAddressUKYesNoPage, true).success.value
              .set(deceasedPages.SettlorsUKAddressPage, ukAddress).success.value

            val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

            val result = helper.deceasedSettlor

            result mustBe Some(Seq(
              AnswerSection(
                headingKey = None,
                rows = Seq(
                  AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorsName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(deceasedRoutes.SettlorsNameController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorDateOfDeathYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorDateOfDeathYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorDateOfBirthYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorsNationalInsuranceYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorsNINoYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorsLastKnownAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(deceasedRoutes.SettlorsLastKnownAddressYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "wasSettlorsAddressUKYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(deceasedRoutes.WasSettlorsAddressUKYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorsUKAddress.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), changeUrl = Some(deceasedRoutes.SettlorsUKAddressController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
                ),
                sectionKey = Some(messages("answerPage.section.deceasedSettlor.heading"))
              )
            ))
          }

          "international" in {

            val userAnswers = addressAnswers
              .set(deceasedPages.WasSettlorsAddressUKYesNoPage, false).success.value
              .set(deceasedPages.SettlorsInternationalAddressPage, nonUkAddress).success.value

            val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

            val result = helper.deceasedSettlor

            result mustBe Some(Seq(
              AnswerSection(
                headingKey = None,
                rows = Seq(
                  AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorsName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(deceasedRoutes.SettlorsNameController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorDateOfDeathYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorDateOfDeathYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorDateOfBirthYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorsNationalInsuranceYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorsNINoYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorsLastKnownAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(deceasedRoutes.SettlorsLastKnownAddressYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "wasSettlorsAddressUKYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.WasSettlorsAddressUKYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorsInternationalAddress.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />France"), changeUrl = Some(deceasedRoutes.SettlorsInternationalAddressController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
                ),
                sectionKey = Some(messages("answerPage.section.deceasedSettlor.heading"))
              )
            ))
          }
        }
      }

      "deed of variation set up in addition to will trust" in {

        val userAnswers = emptyUserAnswers
          .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(trustTypePages.KindOfTrustPage, KindOfTrust.Deed).success.value
          .set(trustTypePages.SetUpInAdditionToWillTrustYesNoPage, true).success.value
          .set(deceasedPages.SettlorsNamePage, name).success.value
          .set(deceasedPages.SettlorDateOfDeathYesNoPage, false).success.value
          .set(deceasedPages.SettlorDateOfBirthYesNoPage, false).success.value
          .set(deceasedPages.SettlorsNationalInsuranceYesNoPage, false).success.value
          .set(deceasedPages.SettlorsLastKnownAddressYesNoPage, false).success.value

        val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

        val result = helper.deceasedSettlor

        result mustBe Some(Seq(
          AnswerSection(
            headingKey = None,
            rows = Seq(
              AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
              AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust through a Deed of Variation or family agreement"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
              AnswerRow(label = "setUpInAdditionToWillTrustYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(trustTypeRoutes.AdditionToWillTrustYesNoController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
              AnswerRow(label = "settlorsName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(deceasedRoutes.SettlorsNameController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
              AnswerRow(label = "settlorDateOfDeathYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorDateOfDeathYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
              AnswerRow(label = "settlorDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorDateOfBirthYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
              AnswerRow(label = "settlorsNationalInsuranceYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorsNINoYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
              AnswerRow(label = "settlorsLastKnownAddressYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(deceasedRoutes.SettlorsLastKnownAddressYesNoController.onPageLoad(mode, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
            ),
            sectionKey = Some(messages("answerPage.section.deceasedSettlor.heading"))
          )
        ))
      }
    }

    "return a living settlor answer section" when {

      val index: Int = 0

      "individual" when {

        "4mld" when {

          "deed of variation set up to replace will trust with no DOB, NINO or address" in {

            val userAnswers = emptyUserAnswers
              .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, false).success.value
              .set(trustTypePages.KindOfTrustPage, KindOfTrust.Deed).success.value
              .set(trustTypePages.SetUpInAdditionToWillTrustYesNoPage, false).success.value
              .set(trustTypePages.HowDeedOfVariationCreatedPage, DeedOfVariation.ReplacedWill)(DeedOfVariation.writes).success.value
              .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
              .set(individualPages.SettlorIndividualNamePage(index), name).success.value
              .set(individualPages.SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
              .set(individualPages.SettlorIndividualNINOYesNoPage(index), false).success.value
              .set(individualPages.SettlorAddressYesNoPage(index), false).success.value
              .set(LivingSettlorStatus(index), Completed).success.value

            val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

            val result = helper.livingSettlors

            result mustBe Some(Seq(
              AnswerSection(
                headingKey = Some(messages("answerPage.section.settlor.subheading", 1)),
                rows = Seq(
                  AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust through a Deed of Variation or family agreement"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "setUpInAdditionToWillTrustYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.AdditionToWillTrustYesNoController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "howDeedOfVariationCreated.checkYourAnswersLabel", answer = Html("to replace a will trust"), changeUrl = Some(trustTypeRoutes.HowDeedOfVariationCreatedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(livingRoutes.SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(individualRoutes.SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualAddressYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
                ),
                sectionKey = Some(messages("answerPage.section.settlors.heading"))
              )
            ))
          }

          "intervivos with DoB and NINO" in {

            val userAnswers = emptyUserAnswers
              .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, false).success.value
              .set(trustTypePages.KindOfTrustPage, KindOfTrust.Intervivos).success.value
              .set(trustTypePages.HoldoverReliefYesNoPage, true).success.value
              .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
              .set(individualPages.SettlorIndividualNamePage(index), name).success.value
              .set(individualPages.SettlorIndividualDateOfBirthYesNoPage(index), true).success.value
              .set(individualPages.SettlorIndividualDateOfBirthPage(index), date).success.value
              .set(individualPages.SettlorIndividualNINOYesNoPage(index), true).success.value
              .set(individualPages.SettlorIndividualNINOPage(index), nino).success.value
              .set(LivingSettlorStatus(index), Completed).success.value

            val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

            val result = helper.livingSettlors

            result mustBe Some(Seq(
              AnswerSection(
                headingKey = Some(messages("answerPage.section.settlor.subheading", 1)),
                rows = Seq(
                  AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust created during their lifetime to gift or transfer assets"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "holdoverReliefYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(trustTypeRoutes.HoldoverReliefYesNoController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(livingRoutes.SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(individualRoutes.SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individualRoutes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualDateOfBirth.checkYourAnswersLabel", answer = Html("3 February 1996"), changeUrl = Some(individualRoutes.SettlorIndividualDateOfBirthController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individualRoutes.SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualNINO.checkYourAnswersLabel", answer = Html("AA 00 00 00 A"), changeUrl = Some(individualRoutes.SettlorIndividualNINOController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
                ),
                sectionKey = Some(messages("answerPage.section.settlors.heading"))
              )
            ))
          }

          "flat management with UK address and no passport/ID card" in {

            val userAnswers = emptyUserAnswers
              .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, false).success.value
              .set(trustTypePages.KindOfTrustPage, KindOfTrust.FlatManagement).success.value
              .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
              .set(individualPages.SettlorIndividualNamePage(index), name).success.value
              .set(individualPages.SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
              .set(individualPages.SettlorIndividualNINOYesNoPage(index), false).success.value
              .set(individualPages.SettlorAddressYesNoPage(index), true).success.value
              .set(individualPages.SettlorAddressUKYesNoPage(index), true).success.value
              .set(individualPages.SettlorAddressUKPage(index), ukAddress).success.value
              .set(individualPages.SettlorIndividualPassportYesNoPage(index), false).success.value
              .set(individualPages.SettlorIndividualIDCardYesNoPage(index), false).success.value
              .set(LivingSettlorStatus(index), Completed).success.value

            val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

            val result = helper.livingSettlors

            result mustBe Some(Seq(
              AnswerSection(
                headingKey = Some(messages("answerPage.section.settlor.subheading", 1)),
                rows = Seq(
                  AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust for a building or building with tenants"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(livingRoutes.SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(individualRoutes.SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individualRoutes.SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualAddressUKYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individualRoutes.SettlorIndividualAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualAddressUK.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), changeUrl = Some(individualRoutes.SettlorIndividualAddressUKController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualPassportYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualPassportYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualIDCardYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualIDCardYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
                ),
                sectionKey = Some(messages("answerPage.section.settlors.heading"))
              )
            ))
          }

          "heritage maintenance fund with UK address and passport" in {

            val userAnswers = emptyUserAnswers
              .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, false).success.value
              .set(trustTypePages.KindOfTrustPage, KindOfTrust.HeritageMaintenanceFund).success.value
              .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
              .set(individualPages.SettlorIndividualNamePage(index), name).success.value
              .set(individualPages.SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
              .set(individualPages.SettlorIndividualNINOYesNoPage(index), false).success.value
              .set(individualPages.SettlorAddressYesNoPage(index), true).success.value
              .set(individualPages.SettlorAddressUKYesNoPage(index), true).success.value
              .set(individualPages.SettlorAddressUKPage(index), ukAddress).success.value
              .set(individualPages.SettlorIndividualPassportYesNoPage(index), true).success.value
              .set(individualPages.SettlorIndividualPassportPage(index), passportOrIdCardDetails).success.value
              .set(LivingSettlorStatus(index), Completed).success.value

            val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

            val result = helper.livingSettlors

            result mustBe Some(Seq(
              AnswerSection(
                headingKey = Some(messages("answerPage.section.settlor.subheading", 1)),
                rows = Seq(
                  AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust for the repair of historic buildings"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(livingRoutes.SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(individualRoutes.SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individualRoutes.SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualAddressUKYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individualRoutes.SettlorIndividualAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualAddressUK.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), changeUrl = Some(individualRoutes.SettlorIndividualAddressUKController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualPassportYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individualRoutes.SettlorIndividualPassportYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualPassport.checkYourAnswersLabel", answer = Html("United Kingdom<br />0987654321<br />3 February 1996"), changeUrl = Some(individualRoutes.SettlorIndividualPassportController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
                ),
                sectionKey = Some(messages("answerPage.section.settlors.heading"))
              )
            ))
          }

          "trust for employees of a company (EFRBS) with international address and ID card" in {

            val userAnswers = emptyUserAnswers
              .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, false).success.value
              .set(trustTypePages.KindOfTrustPage, KindOfTrust.Employees).success.value
              .set(trustTypePages.EfrbsYesNoPage, true).success.value
              .set(trustTypePages.EfrbsStartDatePage, date).success.value
              .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
              .set(individualPages.SettlorIndividualNamePage(index), name).success.value
              .set(individualPages.SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
              .set(individualPages.SettlorIndividualNINOYesNoPage(index), false).success.value
              .set(individualPages.SettlorAddressYesNoPage(index), true).success.value
              .set(individualPages.SettlorAddressUKYesNoPage(index), false).success.value
              .set(individualPages.SettlorAddressInternationalPage(index), nonUkAddress).success.value
              .set(individualPages.SettlorIndividualPassportYesNoPage(index), false).success.value
              .set(individualPages.SettlorIndividualIDCardYesNoPage(index), true).success.value
              .set(individualPages.SettlorIndividualIDCardPage(index), passportOrIdCardDetails).success.value
              .set(LivingSettlorStatus(index), Completed).success.value

            val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

            val result = helper.livingSettlors

            result mustBe Some(Seq(
              AnswerSection(
                headingKey = Some(messages("answerPage.section.settlor.subheading", 1)),
                rows = Seq(
                  AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust for the employees of a company"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "employerFinancedRbsYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(trustTypeRoutes.EmployerFinancedRbsYesNoController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "employerFinancedRbsStartDate.checkYourAnswersLabel", answer = Html("3 February 1996"), changeUrl = Some(trustTypeRoutes.EmployerFinancedRbsStartDateController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(livingRoutes.SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(individualRoutes.SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individualRoutes.SettlorIndividualAddressYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualAddressUKYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualAddressInternational.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />France"), changeUrl = Some(individualRoutes.SettlorIndividualAddressInternationalController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualPassportYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualPassportYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualIDCardYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individualRoutes.SettlorIndividualIDCardYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualIDCard.checkYourAnswersLabel", answer = Html("United Kingdom<br />0987654321<br />3 February 1996"), changeUrl = Some(individualRoutes.SettlorIndividualIDCardController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
                ),
                sectionKey = Some(messages("answerPage.section.settlors.heading"))
              )
            ))
          }
        }

        "5mld" when {

          val baseAnswers: UserAnswers = emptyUserAnswers
            .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(trustTypePages.KindOfTrustPage, KindOfTrust.HeritageMaintenanceFund).success.value
            .set(SettlorIndividualOrBusinessPage(index), Individual).success.value
            .set(individualPages.SettlorIndividualNamePage(index), name).success.value
            .set(individualPages.SettlorIndividualDateOfBirthYesNoPage(index), false).success.value

          "nationality and residency both unknown" in {

            val userAnswers = baseAnswers
              .set(individual5mldPages.CountryOfNationalityYesNoPage(index), false).success.value
              .set(individualPages.SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
              .set(individualPages.SettlorIndividualNINOYesNoPage(index), true).success.value
              .set(individualPages.SettlorIndividualNINOPage(index), nino).success.value
              .set(individual5mldPages.CountryOfResidencyYesNoPage(index), false).success.value
              .set(individual5mldPages.MentalCapacityYesNoPage(index), false).success.value
              .set(LivingSettlorStatus(index), Completed).success.value

            val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

            val result = helper.livingSettlors

            result mustBe Some(Seq(
              AnswerSection(
                headingKey = Some(messages("answerPage.section.settlor.subheading", 1)),
                rows = Seq(
                  AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust for the repair of historic buildings"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(livingRoutes.SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(individualRoutes.SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualCountryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individual5mldRoutes.CountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individualRoutes.SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualNINO.checkYourAnswersLabel", answer = Html("AA 00 00 00 A"), changeUrl = Some(individualRoutes.SettlorIndividualNINOController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualCountryOfResidencyYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individual5mldRoutes.CountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualMentalCapacityYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individual5mldRoutes.MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
                ),
                sectionKey = Some(messages("answerPage.section.settlors.heading"))
              )
            ))
          }

          "UK nationality and residency" in {

            val userAnswers = baseAnswers
              .set(individual5mldPages.CountryOfNationalityYesNoPage(index), true).success.value
              .set(individual5mldPages.UkCountryOfNationalityYesNoPage(index), true).success.value
              .set(individualPages.SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
              .set(individualPages.SettlorIndividualNINOYesNoPage(index), true).success.value
              .set(individualPages.SettlorIndividualNINOPage(index), nino).success.value
              .set(individual5mldPages.CountryOfResidencyYesNoPage(index), true).success.value
              .set(individual5mldPages.UkCountryOfResidencyYesNoPage(index), true).success.value
              .set(individual5mldPages.MentalCapacityYesNoPage(index), false).success.value
              .set(LivingSettlorStatus(index), Completed).success.value

            val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

            val result = helper.livingSettlors

            result mustBe Some(Seq(
              AnswerSection(
                headingKey = Some(messages("answerPage.section.settlor.subheading", 1)),
                rows = Seq(
                  AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust for the repair of historic buildings"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(livingRoutes.SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(individualRoutes.SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualCountryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individual5mldRoutes.CountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualUkCountryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individual5mldRoutes.UkCountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individualRoutes.SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualNINO.checkYourAnswersLabel", answer = Html("AA 00 00 00 A"), changeUrl = Some(individualRoutes.SettlorIndividualNINOController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualCountryOfResidencyYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individual5mldRoutes.CountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualUkCountryOfResidencyYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individual5mldRoutes.UkCountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualMentalCapacityYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individual5mldRoutes.MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
                ),
                sectionKey = Some(messages("answerPage.section.settlors.heading"))
              )
            ))
          }

          "non-UK nationality and residency" in {

            val userAnswers = baseAnswers
              .set(individual5mldPages.CountryOfNationalityYesNoPage(index), true).success.value
              .set(individual5mldPages.UkCountryOfNationalityYesNoPage(index), false).success.value
              .set(individual5mldPages.CountryOfNationalityPage(index), "FR").success.value
              .set(individualPages.SettlorIndividualDateOfBirthYesNoPage(index), false).success.value
              .set(individualPages.SettlorIndividualNINOYesNoPage(index), true).success.value
              .set(individualPages.SettlorIndividualNINOPage(index), nino).success.value
              .set(individual5mldPages.CountryOfResidencyYesNoPage(index), true).success.value
              .set(individual5mldPages.UkCountryOfResidencyYesNoPage(index), false).success.value
              .set(individual5mldPages.CountryOfResidencyPage(index), "FR").success.value
              .set(individual5mldPages.MentalCapacityYesNoPage(index), false).success.value
              .set(LivingSettlorStatus(index), Completed).success.value

            val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

            val result = helper.livingSettlors

            result mustBe Some(Seq(
              AnswerSection(
                headingKey = Some(messages("answerPage.section.settlor.subheading", 1)),
                rows = Seq(
                  AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust for the repair of historic buildings"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Individual"), changeUrl = Some(livingRoutes.SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualName.checkYourAnswersLabel", answer = Html("Joe Joseph Bloggs"), changeUrl = Some(individualRoutes.SettlorIndividualNameController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individualRoutes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualCountryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individual5mldRoutes.CountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualUkCountryOfNationalityYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individual5mldRoutes.UkCountryOfNationalityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualCountryOfNationality.checkYourAnswersLabel", answer = Html("France"), changeUrl = Some(individual5mldRoutes.CountryOfNationalityController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualNINOYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individualRoutes.SettlorIndividualNINOYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualNINO.checkYourAnswersLabel", answer = Html("AA 00 00 00 A"), changeUrl = Some(individualRoutes.SettlorIndividualNINOController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualCountryOfResidencyYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(individual5mldRoutes.CountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualUkCountryOfResidencyYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individual5mldRoutes.UkCountryOfResidencyYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualCountryOfResidency.checkYourAnswersLabel", answer = Html("France"), changeUrl = Some(individual5mldRoutes.CountryOfResidencyController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualMentalCapacityYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(individual5mldRoutes.MentalCapacityYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = "Joe Bloggs", canEdit = canEdit)
                ),
                sectionKey = Some(messages("answerPage.section.settlors.heading"))
              )
            ))
          }
        }
      }

      "business" when {

        "trust for employees of a company (not EFRBS) with no UTR or address" in {

          val userAnswers = emptyUserAnswers
            .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(trustTypePages.KindOfTrustPage, KindOfTrust.Employees).success.value
            .set(trustTypePages.EfrbsYesNoPage, false).success.value
            .set(SettlorIndividualOrBusinessPage(index), Business).success.value
            .set(businessPages.SettlorBusinessNamePage(index), businessName).success.value
            .set(businessPages.SettlorBusinessUtrYesNoPage(index), false).success.value
            .set(businessPages.SettlorBusinessAddressYesNoPage(index), false).success.value
            .set(LivingSettlorStatus(index), Completed).success.value

          val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

          val result = helper.livingSettlors

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some(messages("answerPage.section.settlor.subheading", 1)),
              rows = Seq(
                AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust for the employees of a company"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "employerFinancedRbsYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.EmployerFinancedRbsYesNoController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Business"), changeUrl = Some(livingRoutes.SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "settlorBusinessName.checkYourAnswersLabel", answer = Html("Business Ltd."), changeUrl = Some(businessRoutes.SettlorBusinessNameController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "settlorBusinessUtrYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = businessName, canEdit = canEdit),
                AnswerRow(label = "settlorBusinessAddressYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(businessRoutes.SettlorBusinessAddressYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = businessName, canEdit = canEdit)
              ),
              sectionKey = Some(messages("answerPage.section.settlors.heading"))
            )
          ))
        }

        "UTR" in {

          val userAnswers = emptyUserAnswers
            .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(trustTypePages.KindOfTrustPage, KindOfTrust.HeritageMaintenanceFund).success.value
            .set(SettlorIndividualOrBusinessPage(index), Business).success.value
            .set(businessPages.SettlorBusinessNamePage(index), businessName).success.value
            .set(businessPages.SettlorBusinessUtrYesNoPage(index), true).success.value
            .set(businessPages.SettlorBusinessUtrPage(index), utr).success.value
            .set(LivingSettlorStatus(index), Completed).success.value

          val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

          val result = helper.livingSettlors

          result mustBe Some(Seq(
            AnswerSection(
              headingKey = Some(messages("answerPage.section.settlor.subheading", 1)),
              rows = Seq(
                AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust for the repair of historic buildings"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Business"), changeUrl = Some(livingRoutes.SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "settlorBusinessName.checkYourAnswersLabel", answer = Html("Business Ltd."), changeUrl = Some(businessRoutes.SettlorBusinessNameController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                AnswerRow(label = "settlorBusinessUtrYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = businessName, canEdit = canEdit),
                AnswerRow(label = "settlorBusinessUtr.checkYourAnswersLabel", answer = Html("1234567890"), changeUrl = Some(businessRoutes.SettlorBusinessUtrController.onPageLoad(mode, index, fakeDraftId).url), labelArg = businessName, canEdit = canEdit)
              ),
              sectionKey = Some(messages("answerPage.section.settlors.heading"))
            )
          ))
        }

        "address" when {

          "UK" in {

            val userAnswers = emptyUserAnswers
              .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, false).success.value
              .set(trustTypePages.KindOfTrustPage, KindOfTrust.HeritageMaintenanceFund).success.value
              .set(SettlorIndividualOrBusinessPage(index), Business).success.value
              .set(businessPages.SettlorBusinessNamePage(index), businessName).success.value
              .set(businessPages.SettlorBusinessUtrYesNoPage(index), false).success.value
              .set(businessPages.SettlorBusinessAddressYesNoPage(index), true).success.value
              .set(businessPages.SettlorBusinessAddressUKYesNoPage(index), true).success.value
              .set(businessPages.SettlorBusinessAddressUKPage(index), ukAddress).success.value
              .set(LivingSettlorStatus(index), Completed).success.value

            val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

            val result = helper.livingSettlors

            result mustBe Some(Seq(
              AnswerSection(
                headingKey = Some(messages("answerPage.section.settlor.subheading", 1)),
                rows = Seq(
                  AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust for the repair of historic buildings"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Business"), changeUrl = Some(livingRoutes.SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorBusinessName.checkYourAnswersLabel", answer = Html("Business Ltd."), changeUrl = Some(businessRoutes.SettlorBusinessNameController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorBusinessUtrYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = businessName, canEdit = canEdit),
                  AnswerRow(label = "settlorBusinessAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(businessRoutes.SettlorBusinessAddressYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = businessName, canEdit = canEdit),
                  AnswerRow(label = "settlorBusinessAddressUKYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(businessRoutes.SettlorBusinessAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = businessName, canEdit = canEdit),
                  AnswerRow(label = "settlorBusinessAddressUK.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), changeUrl = Some(businessRoutes.SettlorBusinessAddressUKController.onPageLoad(mode, index, fakeDraftId).url), labelArg = businessName, canEdit = canEdit),
                ),
                sectionKey = Some(messages("answerPage.section.settlors.heading"))
              )
            ))
          }

          "international" in {

            val userAnswers = emptyUserAnswers
              .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, false).success.value
              .set(trustTypePages.KindOfTrustPage, KindOfTrust.HeritageMaintenanceFund).success.value
              .set(SettlorIndividualOrBusinessPage(index), Business).success.value
              .set(businessPages.SettlorBusinessNamePage(index), businessName).success.value
              .set(businessPages.SettlorBusinessUtrYesNoPage(index), false).success.value
              .set(businessPages.SettlorBusinessAddressYesNoPage(index), true).success.value
              .set(businessPages.SettlorBusinessAddressUKYesNoPage(index), false).success.value
              .set(businessPages.SettlorBusinessAddressInternationalPage(index), nonUkAddress).success.value
              .set(LivingSettlorStatus(index), Completed).success.value

            val helper: CheckYourAnswersHelper = new CheckYourAnswersHelper(countryOptions, checkAnswersFormatters)(userAnswers, fakeDraftId, canEdit)

            val result = helper.livingSettlors

            result mustBe Some(Seq(
              AnswerSection(
                headingKey = Some(messages("answerPage.section.settlor.subheading", 1)),
                rows = Seq(
                  AnswerRow(label = "setUpAfterSettlorDied.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "kindOfTrust.checkYourAnswersLabel", answer = Html("A trust for the repair of historic buildings"), changeUrl = Some(trustTypeRoutes.KindOfTrustController.onPageLoad(mode, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Business"), changeUrl = Some(livingRoutes.SettlorIndividualOrBusinessController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorBusinessName.checkYourAnswersLabel", answer = Html("Business Ltd."), changeUrl = Some(businessRoutes.SettlorBusinessNameController.onPageLoad(mode, index, fakeDraftId).url), canEdit = canEdit),
                  AnswerRow(label = "settlorBusinessUtrYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = businessName, canEdit = canEdit),
                  AnswerRow(label = "settlorBusinessAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(businessRoutes.SettlorBusinessAddressYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = businessName, canEdit = canEdit),
                  AnswerRow(label = "settlorBusinessAddressUKYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(businessRoutes.SettlorBusinessAddressUKYesNoController.onPageLoad(mode, index, fakeDraftId).url), labelArg = businessName, canEdit = canEdit),
                  AnswerRow(label = "settlorBusinessAddressInternational.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />France"), changeUrl = Some(businessRoutes.SettlorBusinessAddressInternationalController.onPageLoad(mode, index, fakeDraftId).url), labelArg = businessName, canEdit = canEdit),
                ),
                sectionKey = Some(messages("answerPage.section.settlors.heading"))
              )
            ))
          }
        }
      }
    }
  }
}
