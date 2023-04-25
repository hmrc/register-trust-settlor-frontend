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

import com.google.inject.Inject
import controllers.deceased_settlor.mld5.routes._
import controllers.deceased_settlor.routes._
import pages.deceased_settlor._
import pages.deceased_settlor.mld5._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class DeceasedSettlorPrintHelper @Inject() (
  answerRowConverter: AnswerRowConverter,
  trustTypePrintHelper: TrustTypePrintHelper
) extends SettlorPrintHelper(trustTypePrintHelper, answerRowConverter) {

  override def answerRows(index: Int, draftId: String, prefix: Option[String] = None)(bound: AnswerRowConverter#Bound)(
    implicit messages: Messages
  ): Seq[Option[AnswerRow]] = Seq(
    bound.nameQuestion(SettlorsNamePage, "settlorsName", SettlorsNameController.onPageLoad(draftId).url),
    bound.yesNoQuestion(
      SettlorDateOfDeathYesNoPage,
      "settlorDateOfDeathYesNo",
      SettlorDateOfDeathYesNoController.onPageLoad(draftId).url
    ),
    bound
      .dateQuestion(SettlorDateOfDeathPage, "settlorDateOfDeath", SettlorDateOfDeathController.onPageLoad(draftId).url),
    bound.yesNoQuestion(
      SettlorDateOfBirthYesNoPage,
      "settlorDateOfBirthYesNo",
      SettlorDateOfBirthYesNoController.onPageLoad(draftId).url
    ),
    bound.dateQuestion(
      SettlorsDateOfBirthPage,
      "settlorsDateOfBirth",
      SettlorsDateOfBirthController.onPageLoad(draftId).url
    ),
    bound.yesNoQuestion(
      CountryOfNationalityYesNoPage,
      "5mld.countryOfNationalityYesNo",
      CountryOfNationalityYesNoController.onPageLoad(draftId).url
    ),
    bound.yesNoQuestion(
      CountryOfNationalityInTheUkYesNoPage,
      "5mld.countryOfNationalityInTheUkYesNo",
      CountryOfNationalityInTheUkYesNoController.onPageLoad(draftId).url
    ),
    bound.countryQuestion(
      CountryOfNationalityPage,
      CountryOfNationalityInTheUkYesNoPage,
      "5mld.countryOfNationality",
      CountryOfNationalityController.onPageLoad(draftId).url
    ),
    bound.yesNoQuestion(
      SettlorsNationalInsuranceYesNoPage,
      "settlorsNationalInsuranceYesNo",
      SettlorsNINoYesNoController.onPageLoad(draftId).url
    ),
    bound.ninoQuestion(
      SettlorNationalInsuranceNumberPage,
      "settlorNationalInsuranceNumber",
      SettlorNationalInsuranceNumberController.onPageLoad(draftId).url
    ),
    bound.yesNoQuestion(
      CountryOfResidenceYesNoPage,
      "5mld.countryOfResidenceYesNo",
      CountryOfResidenceYesNoController.onPageLoad(draftId).url
    ),
    bound.yesNoQuestion(
      CountryOfResidenceInTheUkYesNoPage,
      "5mld.countryOfResidenceInTheUkYesNo",
      CountryOfResidenceInTheUkYesNoController.onPageLoad(draftId).url
    ),
    bound.countryQuestion(
      CountryOfResidencePage,
      CountryOfResidenceInTheUkYesNoPage,
      "5mld.countryOfResidence",
      CountryOfResidenceController.onPageLoad(draftId).url
    ),
    bound.yesNoQuestion(
      SettlorsLastKnownAddressYesNoPage,
      "settlorsLastKnownAddressYesNo",
      SettlorsLastKnownAddressYesNoController.onPageLoad(draftId).url
    ),
    bound.yesNoQuestion(
      WasSettlorsAddressUKYesNoPage,
      "wasSettlorsAddressUKYesNo",
      WasSettlorsAddressUKYesNoController.onPageLoad(draftId).url
    ),
    bound
      .addressQuestion(SettlorsUKAddressPage, "settlorsUKAddress", SettlorsUKAddressController.onPageLoad(draftId).url),
    bound.addressQuestion(
      SettlorsInternationalAddressPage,
      "settlorsInternationalAddress",
      SettlorsInternationalAddressController.onPageLoad(draftId).url
    )
  )

  override def headingKey: Option[String] =
    None

  override def sectionKey(index: Int): Option[String] =
    Some("answerPage.section.deceasedSettlor.heading")

}
