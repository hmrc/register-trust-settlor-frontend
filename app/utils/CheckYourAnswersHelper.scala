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

import controllers.deceased_settlor.{routes => deceasedRoutes}
import controllers.living_settlor.business.mld5.{routes => businessMld5Routes}
import controllers.living_settlor.business.{routes => businessRoutes}
import controllers.living_settlor.individual.{routes => individualRoutes}
import controllers.living_settlor.individual.mld5.{routes => individual5mldRoutes}
import controllers.living_settlor.routes
import controllers.trust_type.{routes => trustTypeRoutes}
import models.pages.{Address, FullName, PassportOrIdCardDetails}
import models.{NormalMode, UserAnswers}
import pages.deceased_settlor._
import pages.living_settlor._
import pages.living_settlor.business._
import pages.living_settlor.business.mld5.{CountryOfResidenceInTheUkYesNoPage, CountryOfResidencePage, CountryOfResidenceYesNoPage}
import pages.living_settlor.individual._
import pages.living_settlor.individual.{mld5 => individual5mldPages}
import pages.trust_type.{SetUpAfterSettlorDiedYesNoPage, _}
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.twirl.api.HtmlFormat
import queries.Gettable
import sections.LivingSettlors
import utils.CheckAnswersFormatters._
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection, SettlorBusinessViewModel, SettlorLivingViewModel}

import java.time.LocalDate
import javax.inject.Inject

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions)
                                      (userAnswers: UserAnswers,
                                       draftId: String,
                                       canEdit: Boolean)
                                      (implicit messages: Messages) {

  def deceasedSettlor: Option[Seq[AnswerSection]] = {

    if (deceasedSettlorsName.nonEmpty) {
      Some(Seq(AnswerSection(
        headingKey = None,
        rows = deceasedSettlorQuestions,
        sectionKey = Some(messages("answerPage.section.deceasedSettlor.heading"))
      )))
    } else {
      None
    }
  }

  val deceasedSettlorQuestions: Seq[AnswerRow] = {
    val arg = deceasedSettlorName(userAnswers)

    Seq(
      wasSetUpAfterSettlorDied,
      kindOfTrust,
      wasSetUpInAdditionToWillTrust,

      deceasedSettlorsName,
      yesNoQuestion(SettlorDateOfDeathYesNoPage, "settlorDateOfDeathYesNo", deceasedRoutes.SettlorDateOfDeathYesNoController.onPageLoad(NormalMode, draftId).url, arg),
      dateQuestion(SettlorDateOfDeathPage, "settlorDateOfDeath", deceasedRoutes.SettlorDateOfDeathController.onPageLoad(NormalMode, draftId).url, arg),
      yesNoQuestion(SettlorDateOfBirthYesNoPage, "settlorDateOfBirthYesNo", deceasedRoutes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId).url, arg),
      dateQuestion(SettlorsDateOfBirthPage, "settlorsDateOfBirth", deceasedRoutes.SettlorsDateOfBirthController.onPageLoad(NormalMode, draftId).url, arg),
      yesNoQuestion(SettlorsNationalInsuranceYesNoPage, "settlorsNationalInsuranceYesNo", deceasedRoutes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId).url, arg),
      ninoQuestion(SettlorNationalInsuranceNumberPage, "settlorNationalInsuranceNumber", deceasedRoutes.SettlorNationalInsuranceNumberController.onPageLoad(NormalMode, draftId).url, arg),
      yesNoQuestion(SettlorsLastKnownAddressYesNoPage, "settlorsLastKnownAddressYesNo", deceasedRoutes.SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode, draftId).url, arg),
      yesNoQuestion(WasSettlorsAddressUKYesNoPage, "wasSettlorsAddressUKYesNo", deceasedRoutes.WasSettlorsAddressUKYesNoController.onPageLoad(NormalMode, draftId).url, arg),
      addressQuestion(SettlorsUKAddressPage, "settlorsUKAddress", deceasedRoutes.SettlorsUKAddressController.onPageLoad(NormalMode, draftId).url, arg),
      addressQuestion(SettlorsInternationalAddressPage, "settlorsInternationalAddress", deceasedRoutes.SettlorsInternationalAddressController.onPageLoad(NormalMode, draftId).url, arg)
    ).flatten
  }

  def livingSettlors: Option[Seq[AnswerSection]] = {

    for {
      livingSettlors <- userAnswers.get(LivingSettlors)
      indexed = livingSettlors.zipWithIndex
    } yield indexed.map {
      case (settlor, index) =>

        val questions: Seq[AnswerRow] = settlor match {
          case _: SettlorLivingViewModel => settlorIndividualQuestions(index)
          case _: SettlorBusinessViewModel => settlorBusinessQuestions(index)
          case _ => Nil
        }

        val sectionKey = if (index == 0) Some(messages("answerPage.section.settlors.heading")) else None

        AnswerSection(
          headingKey = Some(messages("answerPage.section.settlor.subheading", index + 1)),
          questions,
          sectionKey = sectionKey
        )
    }
  }

  def trustTypeQuestions(index: Int): Seq[AnswerRow] = {
    Seq(
      wasSetUpAfterSettlorDied,
      kindOfTrust,
      wasSetUpInAdditionToWillTrust,
      enumQuestion(HowDeedOfVariationCreatedPage, "howDeedOfVariationCreated", trustTypeRoutes.HowDeedOfVariationCreatedController.onPageLoad(NormalMode, draftId).url, "howDeedOfVariationCreated"),
      yesNoQuestion(HoldoverReliefYesNoPage, "holdoverReliefYesNo", trustTypeRoutes.HoldoverReliefYesNoController.onPageLoad(NormalMode, draftId).url),
      yesNoQuestion(EfrbsYesNoPage, "employerFinancedRbsYesNo", trustTypeRoutes.EmployerFinancedRbsYesNoController.onPageLoad(NormalMode, draftId).url),
      dateQuestion(EfrbsStartDatePage, "employerFinancedRbsStartDate", trustTypeRoutes.EmployerFinancedRbsStartDateController.onPageLoad(NormalMode, draftId).url),
      enumQuestion(SettlorIndividualOrBusinessPage(index), "settlorIndividualOrBusiness", routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId).url, "settlorIndividualOrBusiness")
    ).flatten
  }

  def settlorIndividualQuestions(index: Int): Seq[AnswerRow] = {
    val arg = livingSettlorName(index, userAnswers)

    trustTypeQuestions(index) ++ Seq(
      nameQuestion(SettlorIndividualNamePage(index), "settlorIndividualName", individualRoutes.SettlorIndividualNameController.onPageLoad(NormalMode, index, draftId).url),
      yesNoQuestion(SettlorIndividualDateOfBirthYesNoPage(index), "settlorIndividualDateOfBirthYesNo", individualRoutes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      dateQuestion(SettlorIndividualDateOfBirthPage(index), "settlorIndividualDateOfBirth", individualRoutes.SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(individual5mldPages.CountryOfNationalityYesNoPage(index), "settlorIndividualCountryOfNationalityYesNo", individual5mldRoutes.CountryOfNationalityYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(individual5mldPages.UkCountryOfNationalityYesNoPage(index), "settlorIndividualUkCountryOfNationalityYesNo", individual5mldRoutes.UkCountryOfNationalityYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      countryQuestion(individual5mldPages.CountryOfNationalityPage(index), individual5mldPages.UkCountryOfNationalityYesNoPage(index), "settlorIndividualCountryOfNationality", individual5mldRoutes.CountryOfNationalityController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(SettlorIndividualNINOYesNoPage(index), "settlorIndividualNINOYesNo", individualRoutes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      ninoQuestion(SettlorIndividualNINOPage(index), "settlorIndividualNINO", individualRoutes.SettlorIndividualNINOController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(individual5mldPages.CountryOfResidencyYesNoPage(index), "settlorIndividualCountryOfResidencyYesNo", individual5mldRoutes.CountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(individual5mldPages.UkCountryOfResidencyYesNoPage(index), "settlorIndividualUkCountryOfResidencyYesNo", individual5mldRoutes.UkCountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      countryQuestion(individual5mldPages.CountryOfResidencyPage(index), individual5mldPages.UkCountryOfResidencyYesNoPage(index), "settlorIndividualCountryOfResidency", individual5mldRoutes.CountryOfResidencyController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(SettlorAddressYesNoPage(index), "settlorIndividualAddressYesNo", individualRoutes.SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(SettlorAddressUKYesNoPage(index), "settlorIndividualAddressUKYesNo", individualRoutes.SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      addressQuestion(SettlorAddressUKPage(index), "settlorIndividualAddressUK", individualRoutes.SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, draftId).url, arg),
      addressQuestion(SettlorAddressInternationalPage(index), "settlorIndividualAddressInternational", individualRoutes.SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(SettlorIndividualPassportYesNoPage(index), "settlorIndividualPassportYesNo", individualRoutes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      passportOrIdCardDetailsQuestion(SettlorIndividualPassportPage(index), "settlorIndividualPassport", individualRoutes.SettlorIndividualPassportController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(SettlorIndividualIDCardYesNoPage(index), "settlorIndividualIDCardYesNo", individualRoutes.SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      passportOrIdCardDetailsQuestion(SettlorIndividualIDCardPage(index), "settlorIndividualIDCard", individualRoutes.SettlorIndividualIDCardController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(individual5mldPages.LegallyIncapableYesNoPage(index), "settlorIndividualLegallyIncapableYesNo", individual5mldRoutes.LegallyIncapableYesNoController.onPageLoad(NormalMode, index, draftId).url, arg)
    ).flatten
  }

  def settlorBusinessQuestions(index: Int): Seq[AnswerRow] = {
    val arg: String = businessSettlorName(index, userAnswers)

    trustTypeQuestions(index) ++ Seq(
      stringQuestion(SettlorBusinessNamePage(index), "settlorBusinessName", businessRoutes.SettlorBusinessNameController.onPageLoad(NormalMode, index, draftId).url),
      yesNoQuestion(SettlorBusinessUtrYesNoPage(index), "settlorBusinessUtrYesNo", businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      stringQuestion(SettlorBusinessUtrPage(index), "settlorBusinessUtr", businessRoutes.SettlorBusinessUtrController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(CountryOfResidenceYesNoPage(index), "settlorBusiness.5mld.countryOfResidenceYesNo", businessMld5Routes.CountryOfResidenceYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(CountryOfResidenceInTheUkYesNoPage(index), "settlorBusiness.5mld.countryOfResidenceInTheUkYesNo", businessMld5Routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      countryQuestion(CountryOfResidencePage(index), CountryOfResidenceInTheUkYesNoPage(index), "settlorBusiness.5mld.countryOfResidence", businessMld5Routes.CountryOfResidenceController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(SettlorBusinessAddressYesNoPage(index), "settlorBusinessAddressYesNo", businessRoutes.SettlorBusinessAddressYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      yesNoQuestion(SettlorBusinessAddressUKYesNoPage(index), "settlorBusinessAddressUKYesNo", businessRoutes.SettlorBusinessAddressUKYesNoController.onPageLoad(NormalMode, index, draftId).url, arg),
      addressQuestion(SettlorBusinessAddressUKPage(index), "settlorBusinessAddressUK", businessRoutes.SettlorBusinessAddressUKController.onPageLoad(NormalMode, index, draftId).url, arg),
      addressQuestion(SettlorBusinessAddressInternationalPage(index), "settlorBusinessAddressInternational", businessRoutes.SettlorBusinessAddressInternationalController.onPageLoad(NormalMode, index, draftId).url, arg),
      enumQuestion(SettlorBusinessTypePage(index), "settlorBusinessType", businessRoutes.SettlorBusinessTypeController.onPageLoad(NormalMode, index, draftId).url, "kindOfBusiness", arg),
      yesNoQuestion(SettlorBusinessTimeYesNoPage(index), "settlorBusinessTimeYesNo", businessRoutes.SettlorBusinessTimeYesNoController.onPageLoad(NormalMode, index, draftId).url, arg)
    ).flatten
  }

  private def stringQuestion(query: Gettable[String],
                     labelKey: String,
                     changeUrl: String,
                     labelArg: String = ""): Option[AnswerRow] = {
    userAnswers.get(query) map { x =>
      AnswerRow(
        label = s"$labelKey.checkYourAnswersLabel",
        answer = HtmlFormat.escape(x),
        changeUrl = Some(changeUrl),
        labelArg = labelArg,
        canEdit = canEdit
      )
    }
  }

  private def nameQuestion(query: Gettable[FullName],
                   labelKey: String,
                   changeUrl: String): Option[AnswerRow] = {
    userAnswers.get(query) map { x =>
      AnswerRow(
        label = s"$labelKey.checkYourAnswersLabel",
        answer = HtmlFormat.escape(x.displayFullName),
        changeUrl = Some(changeUrl),
        canEdit = canEdit
      )
    }
  }

  private def yesNoQuestion(query: Gettable[Boolean],
                    labelKey: String,
                    changeUrl: String,
                    labelArg: String = ""): Option[AnswerRow] = {
    userAnswers.get(query) map { x =>
      AnswerRow(
        label = s"$labelKey.checkYourAnswersLabel",
        answer = yesOrNo(x),
        changeUrl = Some(changeUrl),
        labelArg = labelArg,
        canEdit = canEdit
      )
    }
  }

  private def dateQuestion(query: Gettable[LocalDate],
                   labelKey: String,
                   changeUrl: String,
                   labelArg: String = ""): Option[AnswerRow] = {
    userAnswers.get(query) map { x =>
      AnswerRow(
        label = s"$labelKey.checkYourAnswersLabel",
        answer = HtmlFormat.escape(x.format(dateFormatter)),
        changeUrl = Some(changeUrl),
        labelArg = labelArg,
        canEdit = canEdit
      )
    }
  }

  private def ninoQuestion(query: Gettable[String],
                   labelKey: String,
                   changeUrl: String,
                   labelArg: String): Option[AnswerRow] = {
    userAnswers.get(query) map { x =>
      AnswerRow(
        label = s"$labelKey.checkYourAnswersLabel",
        answer = HtmlFormat.escape(formatNino(x)),
        changeUrl = Some(changeUrl),
        labelArg = labelArg,
        canEdit = canEdit
      )
    }
  }

  private def addressQuestion[T <: Address](query: Gettable[T],
                                    labelKey: String,
                                    changeUrl: String,
                                    labelArg: String)
                                   (implicit reads: Reads[T]): Option[AnswerRow] = {
    userAnswers.get(query) map { x =>
      AnswerRow(
        label = s"$labelKey.checkYourAnswersLabel",
        answer = addressFormatter(x, countryOptions),
        changeUrl = Some(changeUrl),
        labelArg = labelArg,
        canEdit = canEdit
      )
    }
  }

  private def countryQuestion(query: Gettable[String],
                      yesNoQuery: Gettable[Boolean],
                      labelKey: String,
                      changeUrl: String,
                      labelArg: String): Option[AnswerRow] = {
    userAnswers.get(yesNoQuery) flatMap {
      case false =>
        userAnswers.get(query) map { x =>
          AnswerRow(
            label = s"$labelKey.checkYourAnswersLabel",
            answer = HtmlFormat.escape(country(x, countryOptions)),
            changeUrl = Some(changeUrl),
            labelArg = labelArg,
            canEdit = canEdit
          )
        }
      case true =>
        None
    }
  }

  private def enumQuestion[T](query: Gettable[T],
                      labelKey: String,
                      changeUrl: String,
                      enumPrefix: String,
                      labelArg: String = "")
                     (implicit reads: Reads[T]): Option[AnswerRow] = {
    userAnswers.get(query) map { x =>
      AnswerRow(
        label = s"$labelKey.checkYourAnswersLabel",
        answer = HtmlFormat.escape(messages(s"$enumPrefix.$x")),
        changeUrl = Some(changeUrl),
        labelArg = labelArg,
        canEdit = canEdit
      )
    }
  }

  private def passportOrIdCardDetailsQuestion(query: Gettable[PassportOrIdCardDetails],
                                      labelKey: String,
                                      changeUrl: String,
                                      labelArg: String): Option[AnswerRow] = {
    userAnswers.get(query) map { x =>
      AnswerRow(
        label = s"$labelKey.checkYourAnswersLabel",
        answer = passportOrIDCard(x, countryOptions),
        changeUrl = Some(changeUrl),
        labelArg = labelArg,
        canEdit = canEdit
      )
    }
  }

  private def kindOfTrust: Option[AnswerRow] = {
    enumQuestion(KindOfTrustPage, "kindOfTrust", trustTypeRoutes.KindOfTrustController.onPageLoad(NormalMode, draftId).url, "kindOfTrust")
  }

  private def deceasedSettlorsName: Option[AnswerRow] = {
    nameQuestion(SettlorsNamePage, "settlorsName", controllers.deceased_settlor.routes.SettlorsNameController.onPageLoad(NormalMode, draftId).url)
  }

  private def wasSetUpAfterSettlorDied: Option[AnswerRow] = {
    yesNoQuestion(SetUpAfterSettlorDiedYesNoPage, "setUpAfterSettlorDied", trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(NormalMode, draftId).url)
  }

  private def wasSetUpInAdditionToWillTrust: Option[AnswerRow] = {
    yesNoQuestion(SetUpInAdditionToWillTrustYesNoPage, "setUpInAdditionToWillTrustYesNo", trustTypeRoutes.AdditionToWillTrustYesNoController.onPageLoad(NormalMode, draftId).url)
  }

}
