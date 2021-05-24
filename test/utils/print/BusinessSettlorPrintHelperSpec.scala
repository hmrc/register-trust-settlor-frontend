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

package utils.print

import base.SpecBase
import controllers.living_settlor.business.mld5.routes._
import controllers.living_settlor.business.routes._
import controllers.living_settlor.routes._
import models.UserAnswers
import models.pages.IndividualOrBusiness._
import models.pages.KindOfBusiness.Investment
import models.pages.Status.Completed
import models.pages._
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.{Assertion, BeforeAndAfterEach}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.LivingSettlorStatus
import pages.living_settlor.SettlorIndividualOrBusinessPage
import pages.living_settlor.business._
import pages.living_settlor.business.mld5._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

class BusinessSettlorPrintHelperSpec extends SpecBase with ScalaCheckPropertyChecks with BeforeAndAfterEach {

  private val answerRowConverter = injector.instanceOf[AnswerRowConverter]
  private val mockTrustTypePrintHelper = mock[TrustTypePrintHelper]
  private val businessSettlorPrintHelper = new BusinessSettlorPrintHelper(answerRowConverter, mockTrustTypePrintHelper)

  private val businessName: String = "Business Ltd."
  private val utr: String = "1234567890"
  private val ukAddress: UKAddress = UKAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB11AB")
  private val country: String = "FR"
  private val nonUkAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", Some("Line 3"), country)
  private val index: Int = 0

  override def beforeEach(): Unit = {
    reset(mockTrustTypePrintHelper)
    when(mockTrustTypePrintHelper.answerRows(any(), any())(any())).thenReturn(Nil)
  }

  "BusinessSettlorPrintHelper" must {

    "return a business settlor answer section" when {

      "no UTR, country of residence or address" in {

        val userAnswers = emptyUserAnswers
          .set(SettlorIndividualOrBusinessPage(index), Business).success.value
          .set(SettlorBusinessNamePage(index), businessName).success.value
          .set(SettlorBusinessUtrYesNoPage(index), false).success.value
          .set(CountryOfResidenceYesNoPage(index), false).success.value
          .set(SettlorBusinessAddressYesNoPage(index), false).success.value
          .set(LivingSettlorStatus(index), Completed).success.value

        val expectedAnswerRows = Seq(
          AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Business"), changeUrl = Some(SettlorIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
          AnswerRow(label = "settlorBusinessName.checkYourAnswersLabel", answer = Html("Business Ltd."), changeUrl = Some(SettlorBusinessNameController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
          AnswerRow(label = "settlorBusinessUtrYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorBusinessUtrYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
          AnswerRow(label = "settlorBusiness.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
          AnswerRow(label = "settlorBusinessAddressYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorBusinessAddressYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName)
        )

        assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
      }

      "UTR and no country of residence with business type and business time" in {

        val userAnswers = emptyUserAnswers
          .set(SettlorIndividualOrBusinessPage(index), Business).success.value
          .set(SettlorBusinessNamePage(index), businessName).success.value
          .set(SettlorBusinessUtrYesNoPage(index), true).success.value
          .set(SettlorBusinessUtrPage(index), utr).success.value
          .set(CountryOfResidenceYesNoPage(index), false).success.value
          .set(SettlorBusinessTypePage(index), Investment).success.value
          .set(SettlorBusinessTimeYesNoPage(index), true).success.value
          .set(LivingSettlorStatus(index), Completed).success.value

        val expectedAnswerRows = Seq(
          AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Business"), changeUrl = Some(SettlorIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
          AnswerRow(label = "settlorBusinessName.checkYourAnswersLabel", answer = Html("Business Ltd."), changeUrl = Some(SettlorBusinessNameController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
          AnswerRow(label = "settlorBusinessUtrYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorBusinessUtrYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
          AnswerRow(label = "settlorBusinessUtr.checkYourAnswersLabel", answer = Html("1234567890"), changeUrl = Some(SettlorBusinessUtrController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
          AnswerRow(label = "settlorBusiness.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
          AnswerRow(label = "settlorBusinessType.checkYourAnswersLabel", answer = Html("Investment"), changeUrl = Some(SettlorBusinessTypeController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
          AnswerRow(label = "settlorBusinessTimeYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorBusinessTimeYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName)
        )

        assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
      }

      "address and country of residence" when {

        "UK" in {

          val userAnswers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), Business).success.value
            .set(SettlorBusinessNamePage(index), businessName).success.value
            .set(SettlorBusinessUtrYesNoPage(index), false).success.value
            .set(CountryOfResidenceYesNoPage(index), true).success.value
            .set(CountryOfResidenceInTheUkYesNoPage(index), true).success.value
            .set(SettlorBusinessAddressYesNoPage(index), true).success.value
            .set(SettlorBusinessAddressUKYesNoPage(index), true).success.value
            .set(SettlorBusinessAddressUKPage(index), ukAddress).success.value
            .set(LivingSettlorStatus(index), Completed).success.value

          val expectedAnswerRows = Seq(
            AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Business"), changeUrl = Some(SettlorIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusinessName.checkYourAnswersLabel", answer = Html("Business Ltd."), changeUrl = Some(SettlorBusinessNameController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusinessUtrYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorBusinessUtrYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusiness.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusiness.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusinessAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorBusinessAddressYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusinessAddressUKYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorBusinessAddressUKYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusinessAddressUK.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"), changeUrl = Some(SettlorBusinessAddressUKController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
          )

          assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
        }

        "international" in {

          val userAnswers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(index), Business).success.value
            .set(SettlorBusinessNamePage(index), businessName).success.value
            .set(SettlorBusinessUtrYesNoPage(index), false).success.value
            .set(CountryOfResidenceYesNoPage(index), true).success.value
            .set(CountryOfResidenceInTheUkYesNoPage(index), false).success.value
            .set(CountryOfResidencePage(index), country).success.value
            .set(SettlorBusinessAddressYesNoPage(index), true).success.value
            .set(SettlorBusinessAddressUKYesNoPage(index), false).success.value
            .set(SettlorBusinessAddressInternationalPage(index), nonUkAddress).success.value
            .set(LivingSettlorStatus(index), Completed).success.value

          val expectedAnswerRows = Seq(
            AnswerRow(label = "settlorIndividualOrBusiness.checkYourAnswersLabel", answer = Html("Business"), changeUrl = Some(SettlorIndividualOrBusinessController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusinessName.checkYourAnswersLabel", answer = Html("Business Ltd."), changeUrl = Some(SettlorBusinessNameController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusinessUtrYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorBusinessUtrYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusiness.5mld.countryOfResidenceYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusiness.5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(CountryOfResidenceInTheUkYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusiness.5mld.countryOfResidence.checkYourAnswersLabel", answer = Html("France"), changeUrl = Some(CountryOfResidenceController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusinessAddressYesNo.checkYourAnswersLabel", answer = Html("Yes"), changeUrl = Some(SettlorBusinessAddressYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusinessAddressUKYesNo.checkYourAnswersLabel", answer = Html("No"), changeUrl = Some(SettlorBusinessAddressUKYesNoController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
            AnswerRow(label = "settlorBusinessAddressInternational.checkYourAnswersLabel", answer = Html("Line 1<br />Line 2<br />Line 3<br />France"), changeUrl = Some(SettlorBusinessAddressInternationalController.onPageLoad(index, fakeDraftId).url), labelArg = businessName),
          )

          assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
        }
      }
    }
  }

  private def assertThatUserAnswersProduceExpectedAnswerRows(userAnswers: UserAnswers,
                                                             expectedAnswerRows: Seq[AnswerRow]): Assertion = {

    val checkDetailsSection = businessSettlorPrintHelper.checkDetailsSection(userAnswers, businessName, fakeDraftId, index)
    checkDetailsSection mustEqual AnswerSection(
      headingKey = None,
      rows = expectedAnswerRows,
      sectionKey = None
    )
    
    val firstPrintSection = businessSettlorPrintHelper.printSection(userAnswers, businessName, fakeDraftId, index)
    firstPrintSection mustEqual AnswerSection(
      headingKey = Some(messages("answerPage.section.settlor.subheading", index + 1)),
      rows = expectedAnswerRows,
      sectionKey = Some(messages("answerPage.section.settlors.heading"))
    )
    
    forAll(arbitrary[Int].suchThat(_ > 0)) { subsequentIndex =>
      val subsequentPrintSection = businessSettlorPrintHelper.printSection(userAnswers, businessName , fakeDraftId, subsequentIndex)
      subsequentPrintSection.headingKey mustBe Some(messages("answerPage.section.settlor.subheading", subsequentIndex + 1))
      subsequentPrintSection.sectionKey mustBe None
    }
  }
}
