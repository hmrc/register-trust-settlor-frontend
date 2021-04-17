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

import com.google.inject.Inject
import controllers.deceased_settlor.mld5.routes._
import controllers.deceased_settlor.routes._
import models.NormalMode
import pages.deceased_settlor._
import pages.deceased_settlor.mld5._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class DeceasedSettlorPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                           trustTypePrintHelper: TrustTypePrintHelper)
  extends SettlorPrintHelper(trustTypePrintHelper) {

  override def arc: AnswerRowConverter = answerRowConverter

  override def answerRows(index: Int, draftId: String)
                         (bound: AnswerRowConverter#Bound)
                         (implicit messages: Messages): Seq[Option[AnswerRow]] = Seq(
    bound.nameQuestion(SettlorsNamePage, "settlorsName", SettlorsNameController.onPageLoad(NormalMode, draftId).url),
    bound.yesNoQuestion(SettlorDateOfDeathYesNoPage, "settlorDateOfDeathYesNo", SettlorDateOfDeathYesNoController.onPageLoad(NormalMode, draftId).url),
    bound.dateQuestion(SettlorDateOfDeathPage, "settlorDateOfDeath", SettlorDateOfDeathController.onPageLoad(NormalMode, draftId).url),
    bound.yesNoQuestion(SettlorDateOfBirthYesNoPage, "settlorDateOfBirthYesNo", SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId).url),
    bound.dateQuestion(SettlorsDateOfBirthPage, "settlorsDateOfBirth", SettlorsDateOfBirthController.onPageLoad(NormalMode, draftId).url),
    bound.yesNoQuestion(CountryOfNationalityYesNoPage, "5mld.countryOfNationalityYesNo", CountryOfNationalityYesNoController.onPageLoad(NormalMode, draftId).url),
    bound.yesNoQuestion(CountryOfNationalityInTheUkYesNoPage, "5mld.countryOfNationalityInTheUkYesNo", CountryOfNationalityInTheUkYesNoController.onPageLoad(NormalMode, draftId).url),
    bound.countryQuestion(CountryOfNationalityPage, CountryOfNationalityInTheUkYesNoPage, "5mld.countryOfNationality", CountryOfNationalityController.onPageLoad(NormalMode, draftId).url),
    bound.yesNoQuestion(SettlorsNationalInsuranceYesNoPage, "settlorsNationalInsuranceYesNo", SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId).url),
    bound.ninoQuestion(SettlorNationalInsuranceNumberPage, "settlorNationalInsuranceNumber", SettlorNationalInsuranceNumberController.onPageLoad(NormalMode, draftId).url),
    bound.yesNoQuestion(CountryOfResidenceYesNoPage, "5mld.countryOfResidenceYesNo", CountryOfResidenceYesNoController.onPageLoad(NormalMode, draftId).url),
    bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage, "5mld.countryOfResidenceInTheUkYesNo", CountryOfResidenceInTheUkYesNoController.onPageLoad(NormalMode, draftId).url),
    bound.countryQuestion(CountryOfResidencePage, CountryOfResidenceInTheUkYesNoPage, "5mld.countryOfResidence", CountryOfResidenceController.onPageLoad(NormalMode, draftId).url),
    bound.yesNoQuestion(SettlorsLastKnownAddressYesNoPage, "settlorsLastKnownAddressYesNo", SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode, draftId).url),
    bound.yesNoQuestion(WasSettlorsAddressUKYesNoPage, "wasSettlorsAddressUKYesNo", WasSettlorsAddressUKYesNoController.onPageLoad(NormalMode, draftId).url),
    bound.addressQuestion(SettlorsUKAddressPage, "settlorsUKAddress", SettlorsUKAddressController.onPageLoad(NormalMode, draftId).url),
    bound.addressQuestion(SettlorsInternationalAddressPage, "settlorsInternationalAddress", SettlorsInternationalAddressController.onPageLoad(NormalMode, draftId).url)
  )

  override def headingKey(index: Int)(implicit messages: Messages): Option[String] =
    None

  override def sectionKey(index: Int)(implicit messages: Messages): Option[String] =
    Some(messages("answerPage.section.deceasedSettlor.heading"))

}
