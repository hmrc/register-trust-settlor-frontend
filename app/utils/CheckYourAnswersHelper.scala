/*
 * Copyright 2020 HM Revenue & Customs
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

import controllers.living_settlor.business.{routes => businessRoutes}
import controllers.trust_type.{routes => trustTypeRoutes}
import controllers.living_settlor.routes
import javax.inject.Inject
import models.{NormalMode, UserAnswers}
import pages.deceased_settlor._
import pages.living_settlor._
import pages.living_settlor.business._
import pages.trust_type.{SetUpAfterSettlorDiedYesNoPage, _}
import play.api.i18n.Messages
import play.twirl.api.HtmlFormat
import sections.LivingSettlors
import utils.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection}

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions)
                                      (userAnswers: UserAnswers,
                                       draftId: String,
                                       canEdit: Boolean)
                                      (implicit messages: Messages) {

  def settlorBusinessName(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessNamePage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessName.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(businessRoutes.SettlorBusinessNameController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def settlorBusinessUtrYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessUtrYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessUtrYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessUtr(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessUtrPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessUtr.checkYourAnswersLabel",
        HtmlFormat.escape(x),
        Some(businessRoutes.SettlorBusinessUtrController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessAddressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessAddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(businessRoutes.SettlorBusinessAddressYesNoController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessAddressUkYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessAddressUKYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(businessRoutes.SettlorBusinessAddressUKYesNoController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessAddressUk(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessAddressUKPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessAddressUK.checkYourAnswersLabel",
        ukAddress(x),
        Some(businessRoutes.SettlorBusinessAddressUKController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessAddressInternational(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessAddressInternationalPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessAddressInternational.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(businessRoutes.SettlorBusinessAddressInternationalController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessType(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessTypePage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessType.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"kindOfBusiness.$x")),
        Some(businessRoutes.SettlorBusinessTypeController.onPageLoad(NormalMode, index, draftId).url),
        businessSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorBusinessTimeYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorBusinessTimeYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorBusinessTimeYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(businessRoutes.SettlorBusinessTimeYesNoController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def kindOfTrust: Option[AnswerRow] = userAnswers.get(KindOfTrustPage) map {
    x =>
      AnswerRow(
        "kindOfTrust.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"kindOfTrust.$x")),
        Some(trustTypeRoutes.KindOfTrustController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def efrbsYesNo: Option[AnswerRow] = userAnswers.get(EfrbsYesNoPage) map {
    x =>
      AnswerRow(
        "employerFinancedRbsYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(trustTypeRoutes.EmployerFinancedRbsYesNoController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def efrbsStartDate: Option[AnswerRow] = userAnswers.get(EfrbsStartDatePage) map {
    x =>
      AnswerRow(
        "employerFinancedRbsStartDate.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(trustTypeRoutes.EmployerFinancedRbsStartDateController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def deedOfVariation: Option[AnswerRow] = userAnswers.get(HowDeedOfVariationCreatedPage) map {
    x =>
      AnswerRow(
        "howDeedOfVariationCreated.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"howDeedOfVariationCreated.$x")),
        Some(trustTypeRoutes.HowDeedOfVariationCreatedController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def holdoverReliefYesNo: Option[AnswerRow] = userAnswers.get(HoldoverReliefYesNoPage) map {
    x =>
      AnswerRow(
        "holdoverReliefYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(trustTypeRoutes.HoldoverReliefYesNoController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def settlorIndividualPassportYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualPassportYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualPassportYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualPassport(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualPassportPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualPassport.checkYourAnswersLabel",
        passportOrIDCard(x, countryOptions),
        Some(routes.SettlorIndividualPassportController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualIDCardYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualIDCardYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualIDCardYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualIDCard(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualIDCardPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualIDCard.checkYourAnswersLabel",
        passportOrIDCard(x, countryOptions),
        Some(routes.SettlorIndividualIDCardController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualAddressUKYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorAddressUKYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualAddressUK(index: Int): Option[AnswerRow] = userAnswers.get(SettlorAddressUKPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualAddressUK.checkYourAnswersLabel",
        ukAddress(x),
        Some(routes.SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualAddressInternational(index: Int): Option[AnswerRow] = userAnswers.get(SettlorAddressInternationalPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualAddressInternational.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(routes.SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualNINOYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualNINOYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualNINOYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualNINO(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualNINOPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualNINO.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        Some(routes.SettlorIndividualNINOController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualAddressYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorAddressYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualDateOfBirth(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualDateOfBirthPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(routes.SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualDateOfBirthYesNo(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualDateOfBirthYesNoPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(routes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId).url),
        livingSettlorName(index, userAnswers),
        canEdit = canEdit
      )
  }

  def settlorIndividualName(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualNamePage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        Some(routes.SettlorIndividualNameController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def settlorIndividualOrBusiness(index: Int): Option[AnswerRow] = userAnswers.get(SettlorIndividualOrBusinessPage(index)) map {
    x =>
      AnswerRow(
        "settlorIndividualOrBusiness.checkYourAnswersLabel",
        HtmlFormat.escape(messages(s"settlorIndividualOrBusiness.$x")),
        Some(routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId).url),
        canEdit = canEdit
      )
  }

  def deceasedSettlor: Option[Seq[AnswerSection]] = {

    val questions = Seq(
      setUpAfterSettlorDied,
      setUpInAddition,
      deedOfVariation,
      deceasedSettlorsName,
      deceasedSettlorDateOfDeathYesNo,
      deceasedSettlorDateOfDeath,
      deceasedSettlorDateOfBirthYesNo,
      deceasedSettlorsDateOfBirth,
      deceasedSettlorsNINoYesNo,
      deceasedSettlorNationalInsuranceNumber,
      deceasedSettlorsLastKnownAddressYesNo,
      wasSettlorsAddressUKYesNo,
      deceasedSettlorsUKAddress,
      deceasedSettlorsInternationalAddress
    ).flatten

    if (deceasedSettlorsName.nonEmpty)
      Some(Seq(AnswerSection(
        headingKey = None,
        questions,
        sectionKey = Some(messages("answerPage.section.deceasedSettlor.heading"))
      )))
    else None
  }

  def livingSettlors: Option[Seq[AnswerSection]] = {

    for {
      livingSettlors <- userAnswers.get(LivingSettlors)
      indexed = livingSettlors.zipWithIndex
    } yield indexed.map {
      case (_, index) =>

        val questions = Seq(
          setUpAfterSettlorDied,
          kindOfTrust,
          deedOfVariation,
          setUpInAddition,
          holdoverReliefYesNo,
          settlorIndividualOrBusiness(index),
          settlorIndividualName(index),
          settlorIndividualDateOfBirthYesNo(index),
          settlorIndividualDateOfBirth(index),
          settlorIndividualNINOYesNo(index),
          settlorIndividualNINO(index),
          settlorIndividualAddressYesNo(index),
          settlorIndividualAddressUKYesNo(index),
          settlorIndividualAddressUK(index),
          settlorIndividualAddressInternational(index),
          settlorIndividualPassportYesNo(index),
          settlorIndividualPassport(index),
          settlorIndividualIDCardYesNo(index),
          settlorIndividualIDCard(index),
          settlorBusinessName(index),
          settlorBusinessUtrYesNo(index),
          settlorBusinessUtr(index),
          settlorBusinessAddressYesNo(index),
          settlorBusinessAddressUkYesNo(index),
          settlorBusinessAddressUk(index),
          settlorBusinessAddressInternational(index),
          settlorBusinessType(index),
          settlorBusinessTimeYesNo(index)
        ).flatten

        val sectionKey = if (index == 0) Some(messages("answerPage.section.settlors.heading")) else None

        AnswerSection(
          headingKey = Some(messages("answerPage.section.settlor.subheading", index + 1)),
          questions,
          sectionKey = sectionKey
        )
    }
  }

  def wasSettlorsAddressUKYesNo: Option[AnswerRow] = userAnswers.get(WasSettlorsAddressUKYesNoPage) map {
    x =>
      AnswerRow(
        "wasSettlorsAddressUKYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.deceased_settlor.routes.WasSettlorsAddressUKYesNoController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def setUpAfterSettlorDied: Option[AnswerRow] = userAnswers.get(SetUpAfterSettlorDiedYesNoPage) map {
    x =>
      AnswerRow(
        "setUpAfterSettlorDied.checkYourAnswersLabel",
        yesOrNo(x),
        Some(trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def setUpInAddition: Option[AnswerRow] = userAnswers.get(SetUpInAdditionToWillTrustYesNoPage) map {
    x =>
      AnswerRow(
        "setUpInAdditionToWillTrustYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(trustTypeRoutes.AdditionToWillTrustYesNoController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def deceasedSettlorsUKAddress: Option[AnswerRow] = userAnswers.get(SettlorsUKAddressPage) map {
    x =>
      AnswerRow(
        "settlorsUKAddress.checkYourAnswersLabel",
        ukAddress(x),
        Some(controllers.deceased_settlor.routes.SettlorsUKAddressController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorsNINoYesNo: Option[AnswerRow] = userAnswers.get(SettlorsNationalInsuranceYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsNationalInsuranceYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.deceased_settlor.routes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorsName: Option[AnswerRow] = userAnswers.get(SettlorsNamePage) map {
    x =>
      AnswerRow(
        "settlorsName.checkYourAnswersLabel",
        HtmlFormat.escape(s"${x.firstName} ${x.middleName.getOrElse("")} ${x.lastName}"),
        Some(controllers.deceased_settlor.routes.SettlorsNameController.onPageLoad(NormalMode, draftId).url),
        canEdit = canEdit
      )
  }

  def deceasedSettlorsLastKnownAddressYesNo: Option[AnswerRow] = userAnswers.get(SettlorsLastKnownAddressYesNoPage) map {
    x =>
      AnswerRow(
        "settlorsLastKnownAddressYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.deceased_settlor.routes.SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorsInternationalAddress: Option[AnswerRow] = userAnswers.get(SettlorsInternationalAddressPage) map {
    x =>
      AnswerRow(
        "settlorsInternationalAddress.checkYourAnswersLabel",
        internationalAddress(x, countryOptions),
        Some(controllers.deceased_settlor.routes.SettlorsInternationalAddressController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorsDateOfBirth: Option[AnswerRow] = userAnswers.get(SettlorsDateOfBirthPage) map {
    x =>
      AnswerRow(
        "settlorsDateOfBirth.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(controllers.deceased_settlor.routes.SettlorsDateOfBirthController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorNationalInsuranceNumber: Option[AnswerRow] = userAnswers.get(SettlorNationalInsuranceNumberPage) map {
    x =>
      AnswerRow(
        "settlorNationalInsuranceNumber.checkYourAnswersLabel",
        HtmlFormat.escape(formatNino(x)),
        Some(controllers.deceased_settlor.routes.SettlorNationalInsuranceNumberController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorDateOfDeathYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeathYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.deceased_settlor.routes.SettlorDateOfDeathYesNoController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorDateOfDeath: Option[AnswerRow] = userAnswers.get(SettlorDateOfDeathPage) map {
    x =>
      AnswerRow(
        "settlorDateOfDeath.checkYourAnswersLabel",
        HtmlFormat.escape(x.format(dateFormatter)),
        Some(controllers.deceased_settlor.routes.SettlorDateOfDeathController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }

  def deceasedSettlorDateOfBirthYesNo: Option[AnswerRow] = userAnswers.get(SettlorDateOfBirthYesNoPage) map {
    x =>
      AnswerRow(
        "settlorDateOfBirthYesNo.checkYourAnswersLabel",
        yesOrNo(x),
        Some(controllers.deceased_settlor.routes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId).url),
        deceasedSettlorName(userAnswers),
        canEdit = canEdit
      )
  }
}
