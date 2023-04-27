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

package utils.print

import base.SpecBase
import controllers.deceased_settlor.mld5.routes._
import controllers.deceased_settlor.routes._
import models.UserAnswers
import models.pages._
import org.scalatest.Assertion
import pages.deceased_settlor._
import pages.deceased_settlor.mld5._
import play.twirl.api.Html
import viewmodels.{AnswerRow, AnswerSection}

import java.time.LocalDate

class DeceasedSettlorPrintHelperSpec extends SpecBase {

  private val deceasedSettlorPrintHelper = injector.instanceOf[DeceasedSettlorPrintHelper]

  private val name: FullName                     = FullName("Joe", Some("Joseph"), "Bloggs")
  private val date: LocalDate                    = LocalDate.parse("1996-02-03")
  private val dateOfDeath: LocalDate             = LocalDate.parse("2010-02-16")
  private val nino: String                       = "AA000000A"
  private val ukAddress: UKAddress               = UKAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB11AB")
  private val nonUkAddress: InternationalAddress = InternationalAddress("Line 1", "Line 2", Some("Line 3"), "FR")
  private val nationality                        = "DE"
  private val residence                          = "US"

  "DeceasedSettlorPrintHelper" must {

    "return a deceased settlor answer section" when {

      "no DoD, DoB, nationality, residence, address or NINO" in {

        val userAnswers = emptyUserAnswers
          .set(SettlorsNamePage, name)
          .success
          .value
          .set(SettlorDateOfDeathYesNoPage, false)
          .success
          .value
          .set(SettlorDateOfBirthYesNoPage, false)
          .success
          .value
          .set(CountryOfNationalityYesNoPage, false)
          .success
          .value
          .set(SettlorsNationalInsuranceYesNoPage, false)
          .success
          .value
          .set(SettlorsLastKnownAddressYesNoPage, false)
          .success
          .value
          .set(CountryOfResidenceYesNoPage, false)
          .success
          .value
          .set(SettlorsLastKnownAddressYesNoPage, false)
          .success
          .value

        val expectedAnswerRows = Seq(
          AnswerRow(
            label = "settlorsName.checkYourAnswersLabel",
            answer = Html("Joe Joseph Bloggs"),
            changeUrl = Some(SettlorsNameController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "settlorDateOfDeathYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(SettlorDateOfDeathYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorDateOfBirthYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(SettlorDateOfBirthYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfNationalityYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(CountryOfNationalityYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorsNationalInsuranceYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(SettlorsNINoYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfResidenceYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorsLastKnownAddressYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(SettlorsLastKnownAddressYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          )
        )

        assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
      }

      "DoB and NINO" in {

        val userAnswers = emptyUserAnswers
          .set(SettlorsNamePage, name)
          .success
          .value
          .set(SettlorDateOfDeathYesNoPage, true)
          .success
          .value
          .set(SettlorDateOfDeathPage, dateOfDeath)
          .success
          .value
          .set(SettlorDateOfBirthYesNoPage, true)
          .success
          .value
          .set(SettlorsDateOfBirthPage, date)
          .success
          .value
          .set(CountryOfNationalityYesNoPage, false)
          .success
          .value
          .set(SettlorsNationalInsuranceYesNoPage, true)
          .success
          .value
          .set(SettlorNationalInsuranceNumberPage, nino)
          .success
          .value
          .set(CountryOfResidenceYesNoPage, false)
          .success
          .value

        val expectedAnswerRows = Seq(
          AnswerRow(
            label = "settlorsName.checkYourAnswersLabel",
            answer = Html("Joe Joseph Bloggs"),
            changeUrl = Some(SettlorsNameController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "settlorDateOfDeathYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(SettlorDateOfDeathYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorDateOfDeath.checkYourAnswersLabel",
            answer = Html("16 February 2010"),
            changeUrl = Some(SettlorDateOfDeathController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorDateOfBirthYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(SettlorDateOfBirthYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorsDateOfBirth.checkYourAnswersLabel",
            answer = Html("3 February 1996"),
            changeUrl = Some(SettlorsDateOfBirthController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfNationalityYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(CountryOfNationalityYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorsNationalInsuranceYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(SettlorsNINoYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorNationalInsuranceNumber.checkYourAnswersLabel",
            answer = Html("AA 00 00 00 A"),
            changeUrl = Some(SettlorNationalInsuranceNumberController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfResidenceYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          )
        )

        assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
      }

      "DoB, UK Nationality, UK Residence, UK Address" in {

        val userAnswers = emptyUserAnswers
          .set(SettlorsNamePage, name)
          .success
          .value
          .set(SettlorDateOfDeathYesNoPage, true)
          .success
          .value
          .set(SettlorDateOfDeathPage, dateOfDeath)
          .success
          .value
          .set(SettlorDateOfBirthYesNoPage, true)
          .success
          .value
          .set(SettlorsDateOfBirthPage, date)
          .success
          .value
          .set(CountryOfNationalityYesNoPage, true)
          .success
          .value
          .set(CountryOfNationalityInTheUkYesNoPage, true)
          .success
          .value
          .set(SettlorsNationalInsuranceYesNoPage, false)
          .success
          .value
          .set(CountryOfResidenceYesNoPage, true)
          .success
          .value
          .set(CountryOfResidenceInTheUkYesNoPage, true)
          .success
          .value
          .set(SettlorsLastKnownAddressYesNoPage, true)
          .success
          .value
          .set(WasSettlorsAddressUKYesNoPage, true)
          .success
          .value
          .set(SettlorsUKAddressPage, ukAddress)
          .success
          .value

        val expectedAnswerRows = Seq(
          AnswerRow(
            label = "settlorsName.checkYourAnswersLabel",
            answer = Html("Joe Joseph Bloggs"),
            changeUrl = Some(SettlorsNameController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "settlorDateOfDeathYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(SettlorDateOfDeathYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorDateOfDeath.checkYourAnswersLabel",
            answer = Html("16 February 2010"),
            changeUrl = Some(SettlorDateOfDeathController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorDateOfBirthYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(SettlorDateOfBirthYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorsDateOfBirth.checkYourAnswersLabel",
            answer = Html("3 February 1996"),
            changeUrl = Some(SettlorsDateOfBirthController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfNationalityYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(CountryOfNationalityYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfNationalityInTheUkYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(CountryOfNationalityInTheUkYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorsNationalInsuranceYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(SettlorsNINoYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfResidenceYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(CountryOfResidenceInTheUkYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorsLastKnownAddressYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(SettlorsLastKnownAddressYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "wasSettlorsAddressUKYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(WasSettlorsAddressUKYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorsUKAddress.checkYourAnswersLabel",
            answer = Html("Line 1<br />Line 2<br />Line 3<br />Line 4<br />AB11AB"),
            changeUrl = Some(SettlorsUKAddressController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          )
        )

        assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
      }

      "DoB, non-UK Nationality, non-UK Residence, non-UK Address" in {

        val userAnswers = emptyUserAnswers
          .set(SettlorsNamePage, name)
          .success
          .value
          .set(SettlorDateOfDeathYesNoPage, true)
          .success
          .value
          .set(SettlorDateOfDeathPage, dateOfDeath)
          .success
          .value
          .set(SettlorDateOfBirthYesNoPage, true)
          .success
          .value
          .set(SettlorsDateOfBirthPage, date)
          .success
          .value
          .set(CountryOfNationalityYesNoPage, true)
          .success
          .value
          .set(CountryOfNationalityInTheUkYesNoPage, false)
          .success
          .value
          .set(CountryOfNationalityPage, nationality)
          .success
          .value
          .set(SettlorsNationalInsuranceYesNoPage, false)
          .success
          .value
          .set(CountryOfResidenceYesNoPage, true)
          .success
          .value
          .set(CountryOfResidenceInTheUkYesNoPage, false)
          .success
          .value
          .set(CountryOfResidencePage, residence)
          .success
          .value
          .set(SettlorsLastKnownAddressYesNoPage, true)
          .success
          .value
          .set(WasSettlorsAddressUKYesNoPage, false)
          .success
          .value
          .set(SettlorsInternationalAddressPage, nonUkAddress)
          .success
          .value

        val expectedAnswerRows = Seq(
          AnswerRow(
            label = "settlorsName.checkYourAnswersLabel",
            answer = Html("Joe Joseph Bloggs"),
            changeUrl = Some(SettlorsNameController.onPageLoad(fakeDraftId).url)
          ),
          AnswerRow(
            label = "settlorDateOfDeathYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(SettlorDateOfDeathYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorDateOfDeath.checkYourAnswersLabel",
            answer = Html("16 February 2010"),
            changeUrl = Some(SettlorDateOfDeathController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorDateOfBirthYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(SettlorDateOfBirthYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorsDateOfBirth.checkYourAnswersLabel",
            answer = Html("3 February 1996"),
            changeUrl = Some(SettlorsDateOfBirthController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfNationalityYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(CountryOfNationalityYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfNationalityInTheUkYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(CountryOfNationalityInTheUkYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfNationality.checkYourAnswersLabel",
            answer = Html("Germany"),
            changeUrl = Some(CountryOfNationalityController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorsNationalInsuranceYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(SettlorsNINoYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfResidenceYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(CountryOfResidenceYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfResidenceInTheUkYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(CountryOfResidenceInTheUkYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "5mld.countryOfResidence.checkYourAnswersLabel",
            answer = Html("United States of America"),
            changeUrl = Some(CountryOfResidenceController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorsLastKnownAddressYesNo.checkYourAnswersLabel",
            answer = Html("Yes"),
            changeUrl = Some(SettlorsLastKnownAddressYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "wasSettlorsAddressUKYesNo.checkYourAnswersLabel",
            answer = Html("No"),
            changeUrl = Some(WasSettlorsAddressUKYesNoController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          ),
          AnswerRow(
            label = "settlorsInternationalAddress.checkYourAnswersLabel",
            answer = Html("Line 1<br />Line 2<br />Line 3<br />France"),
            changeUrl = Some(SettlorsInternationalAddressController.onPageLoad(fakeDraftId).url),
            labelArg = name.toString
          )
        )

        assertThatUserAnswersProduceExpectedAnswerRows(userAnswers, expectedAnswerRows)
      }
    }
  }

  private def assertThatUserAnswersProduceExpectedAnswerRows(
    userAnswers: UserAnswers,
    expectedAnswerRows: Seq[AnswerRow]
  ): Assertion = {

    val printSection = deceasedSettlorPrintHelper.printSection(userAnswers, name.toString, fakeDraftId)
    printSection mustEqual AnswerSection(
      headingKey = None,
      rows = expectedAnswerRows,
      sectionKey = Some("answerPage.section.deceasedSettlor.heading"),
      headingArgs = Seq(1)
    )

    val checkDetailsSection = deceasedSettlorPrintHelper.checkDetailsSection(userAnswers, name.toString, fakeDraftId)
    checkDetailsSection mustEqual AnswerSection(
      headingKey = None,
      rows = expectedAnswerRows,
      sectionKey = None,
      headingArgs = Seq(1)
    )
  }
}
