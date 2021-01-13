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
import controllers.living_settlor.individual.mld5.{routes => individual5mldRoutes}
import controllers.living_settlor.individual.{routes => individualRoutes}
import controllers.living_settlor.routes
import controllers.trust_type.{routes => trustTypeRoutes}
import models.pages.{Address, FullName, PassportOrIdCardDetails}
import models.{NormalMode, UserAnswers}
import pages.living_settlor.business.{mld5 => business5mldPages}
import pages.living_settlor.individual.{mld5 => individual5mldPages}
import pages.living_settlor.{business => businessPages, individual => individualPages, _}
import pages.trust_type.{SetUpAfterSettlorDiedYesNoPage, _}
import pages.{deceased_settlor => deceasedPages}
import play.api.i18n.Messages
import play.api.libs.json.Reads
import play.twirl.api.HtmlFormat
import queries.Gettable
import sections.LivingSettlors
import utils.countryOptions.CountryOptions
import viewmodels.{AnswerRow, AnswerSection, SettlorBusinessViewModel, SettlorLivingViewModel}

import java.time.LocalDate
import javax.inject.Inject

class CheckYourAnswersHelper @Inject()(countryOptions: CountryOptions,
                                       checkAnswersFormatters: CheckAnswersFormatters)
                                      (userAnswers: UserAnswers,
                                       draftId: String,
                                       canEdit: Boolean)
                                      (implicit messages: Messages) {

  def deceasedSettlor: Option[Seq[AnswerSection]] = {

    if (deceasedSettlorNameQuestion.nonEmpty) {
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
    val name = settlorName(deceasedPages.SettlorsNamePage)

    Seq(
      wasSetUpAfterSettlorDiedQuestion,
      kindOfTrustQuestion,
      wasSetUpInAdditionToWillTrustQuestion,

      deceasedSettlorNameQuestion,
      yesNoQuestion(deceasedPages.SettlorDateOfDeathYesNoPage, "settlorDateOfDeathYesNo", deceasedRoutes.SettlorDateOfDeathYesNoController.onPageLoad(NormalMode, draftId).url, name),
      dateQuestion(deceasedPages.SettlorDateOfDeathPage, "settlorDateOfDeath", deceasedRoutes.SettlorDateOfDeathController.onPageLoad(NormalMode, draftId).url, name),
      yesNoQuestion(deceasedPages.SettlorDateOfBirthYesNoPage, "settlorDateOfBirthYesNo", deceasedRoutes.SettlorDateOfBirthYesNoController.onPageLoad(NormalMode, draftId).url, name),
      dateQuestion(deceasedPages.SettlorsDateOfBirthPage, "settlorsDateOfBirth", deceasedRoutes.SettlorsDateOfBirthController.onPageLoad(NormalMode, draftId).url, name),
      yesNoQuestion(deceasedPages.SettlorsNationalInsuranceYesNoPage, "settlorsNationalInsuranceYesNo", deceasedRoutes.SettlorsNINoYesNoController.onPageLoad(NormalMode, draftId).url, name),
      ninoQuestion(deceasedPages.SettlorNationalInsuranceNumberPage, "settlorNationalInsuranceNumber", deceasedRoutes.SettlorNationalInsuranceNumberController.onPageLoad(NormalMode, draftId).url, name),
      yesNoQuestion(deceasedPages.SettlorsLastKnownAddressYesNoPage, "settlorsLastKnownAddressYesNo", deceasedRoutes.SettlorsLastKnownAddressYesNoController.onPageLoad(NormalMode, draftId).url, name),
      yesNoQuestion(deceasedPages.WasSettlorsAddressUKYesNoPage, "wasSettlorsAddressUKYesNo", deceasedRoutes.WasSettlorsAddressUKYesNoController.onPageLoad(NormalMode, draftId).url, name),
      addressQuestion(deceasedPages.SettlorsUKAddressPage, "settlorsUKAddress", deceasedRoutes.SettlorsUKAddressController.onPageLoad(NormalMode, draftId).url, name),
      addressQuestion(deceasedPages.SettlorsInternationalAddressPage, "settlorsInternationalAddress", deceasedRoutes.SettlorsInternationalAddressController.onPageLoad(NormalMode, draftId).url, name)
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
      wasSetUpAfterSettlorDiedQuestion,
      kindOfTrustQuestion,
      wasSetUpInAdditionToWillTrustQuestion,
      enumQuestion(HowDeedOfVariationCreatedPage, "howDeedOfVariationCreated", trustTypeRoutes.HowDeedOfVariationCreatedController.onPageLoad(NormalMode, draftId).url, "howDeedOfVariationCreated"),
      yesNoQuestion(HoldoverReliefYesNoPage, "holdoverReliefYesNo", trustTypeRoutes.HoldoverReliefYesNoController.onPageLoad(NormalMode, draftId).url),
      yesNoQuestion(EfrbsYesNoPage, "employerFinancedRbsYesNo", trustTypeRoutes.EmployerFinancedRbsYesNoController.onPageLoad(NormalMode, draftId).url),
      dateQuestion(EfrbsStartDatePage, "employerFinancedRbsStartDate", trustTypeRoutes.EmployerFinancedRbsStartDateController.onPageLoad(NormalMode, draftId).url),
      enumQuestion(SettlorIndividualOrBusinessPage(index), "settlorIndividualOrBusiness", routes.SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId).url, "settlorIndividualOrBusiness")
    ).flatten
  }

  def settlorIndividualQuestions(index: Int): Seq[AnswerRow] = {
    val name = settlorName(individualPages.SettlorIndividualNamePage(index))

    trustTypeQuestions(index) ++ Seq(
      nameQuestion(individualPages.SettlorIndividualNamePage(index), "settlorIndividualName", individualRoutes.SettlorIndividualNameController.onPageLoad(NormalMode, index, draftId).url),
      yesNoQuestion(individualPages.SettlorIndividualDateOfBirthYesNoPage(index), "settlorIndividualDateOfBirthYesNo", individualRoutes.SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      dateQuestion(individualPages.SettlorIndividualDateOfBirthPage(index), "settlorIndividualDateOfBirth", individualRoutes.SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(individual5mldPages.CountryOfNationalityYesNoPage(index), "settlorIndividualCountryOfNationalityYesNo", individual5mldRoutes.CountryOfNationalityYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(individual5mldPages.UkCountryOfNationalityYesNoPage(index), "settlorIndividualUkCountryOfNationalityYesNo", individual5mldRoutes.UkCountryOfNationalityYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      countryQuestion(individual5mldPages.CountryOfNationalityPage(index), individual5mldPages.UkCountryOfNationalityYesNoPage(index), "settlorIndividualCountryOfNationality", individual5mldRoutes.CountryOfNationalityController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(individualPages.SettlorIndividualNINOYesNoPage(index), "settlorIndividualNINOYesNo", individualRoutes.SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      ninoQuestion(individualPages.SettlorIndividualNINOPage(index), "settlorIndividualNINO", individualRoutes.SettlorIndividualNINOController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(individual5mldPages.CountryOfResidencyYesNoPage(index), "settlorIndividualCountryOfResidencyYesNo", individual5mldRoutes.CountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(individual5mldPages.UkCountryOfResidencyYesNoPage(index), "settlorIndividualUkCountryOfResidencyYesNo", individual5mldRoutes.UkCountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      countryQuestion(individual5mldPages.CountryOfResidencyPage(index), individual5mldPages.UkCountryOfResidencyYesNoPage(index), "settlorIndividualCountryOfResidency", individual5mldRoutes.CountryOfResidencyController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(individualPages.SettlorAddressYesNoPage(index), "settlorIndividualAddressYesNo", individualRoutes.SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(individualPages.SettlorAddressUKYesNoPage(index), "settlorIndividualAddressUKYesNo", individualRoutes.SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      addressQuestion(individualPages.SettlorAddressUKPage(index), "settlorIndividualAddressUK", individualRoutes.SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, draftId).url, name),
      addressQuestion(individualPages.SettlorAddressInternationalPage(index), "settlorIndividualAddressInternational", individualRoutes.SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(individualPages.SettlorIndividualPassportYesNoPage(index), "settlorIndividualPassportYesNo", individualRoutes.SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      passportOrIdCardDetailsQuestion(individualPages.SettlorIndividualPassportPage(index), "settlorIndividualPassport", individualRoutes.SettlorIndividualPassportController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(individualPages.SettlorIndividualIDCardYesNoPage(index), "settlorIndividualIDCardYesNo", individualRoutes.SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      passportOrIdCardDetailsQuestion(individualPages.SettlorIndividualIDCardPage(index), "settlorIndividualIDCard", individualRoutes.SettlorIndividualIDCardController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(individual5mldPages.LegallyIncapableYesNoPage(index), "settlorIndividualLegallyIncapableYesNo", individual5mldRoutes.LegallyIncapableYesNoController.onPageLoad(NormalMode, index, draftId).url, name)
    ).flatten
  }

  def settlorBusinessQuestions(index: Int): Seq[AnswerRow] = {
    val name: String = settlorName(businessPages.SettlorBusinessNamePage(index))

    trustTypeQuestions(index) ++ Seq(
      stringQuestion(businessPages.SettlorBusinessNamePage(index), "settlorBusinessName", businessRoutes.SettlorBusinessNameController.onPageLoad(NormalMode, index, draftId).url),
      yesNoQuestion(businessPages.SettlorBusinessUtrYesNoPage(index), "settlorBusinessUtrYesNo", businessRoutes.SettlorBusinessUtrYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      stringQuestion(businessPages.SettlorBusinessUtrPage(index), "settlorBusinessUtr", businessRoutes.SettlorBusinessUtrController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(business5mldPages.CountryOfResidenceYesNoPage(index), "settlorBusiness.5mld.countryOfResidenceYesNo", businessMld5Routes.CountryOfResidenceYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(business5mldPages.CountryOfResidenceInTheUkYesNoPage(index), "settlorBusiness.5mld.countryOfResidenceInTheUkYesNo", businessMld5Routes.CountryOfResidenceInTheUkYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      countryQuestion(business5mldPages.CountryOfResidencePage(index), business5mldPages.CountryOfResidenceInTheUkYesNoPage(index), "settlorBusiness.5mld.countryOfResidence", businessMld5Routes.CountryOfResidenceController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(businessPages.SettlorBusinessAddressYesNoPage(index), "settlorBusinessAddressYesNo", businessRoutes.SettlorBusinessAddressYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      yesNoQuestion(businessPages.SettlorBusinessAddressUKYesNoPage(index), "settlorBusinessAddressUKYesNo", businessRoutes.SettlorBusinessAddressUKYesNoController.onPageLoad(NormalMode, index, draftId).url, name),
      addressQuestion(businessPages.SettlorBusinessAddressUKPage(index), "settlorBusinessAddressUK", businessRoutes.SettlorBusinessAddressUKController.onPageLoad(NormalMode, index, draftId).url, name),
      addressQuestion(businessPages.SettlorBusinessAddressInternationalPage(index), "settlorBusinessAddressInternational", businessRoutes.SettlorBusinessAddressInternationalController.onPageLoad(NormalMode, index, draftId).url, name),
      enumQuestion(businessPages.SettlorBusinessTypePage(index), "settlorBusinessType", businessRoutes.SettlorBusinessTypeController.onPageLoad(NormalMode, index, draftId).url, "kindOfBusiness", name),
      yesNoQuestion(businessPages.SettlorBusinessTimeYesNoPage(index), "settlorBusinessTimeYesNo", businessRoutes.SettlorBusinessTimeYesNoController.onPageLoad(NormalMode, index, draftId).url, name)
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
        answer = checkAnswersFormatters.yesOrNo(x),
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
        answer = HtmlFormat.escape(checkAnswersFormatters.formatDate(x)),
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
        answer = HtmlFormat.escape(checkAnswersFormatters.formatNino(x)),
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
        answer = checkAnswersFormatters.addressFormatter(x, countryOptions),
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
            answer = HtmlFormat.escape(checkAnswersFormatters.country(x, countryOptions)),
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
        answer = checkAnswersFormatters.passportOrIDCard(x, countryOptions),
        changeUrl = Some(changeUrl),
        labelArg = labelArg,
        canEdit = canEdit
      )
    }
  }

  private def settlorName[T](page: Gettable[T])(implicit rds: Reads[T]): String = {
    userAnswers.get(page).map(_.toString).getOrElse("")
  }

  private def wasSetUpAfterSettlorDiedQuestion: Option[AnswerRow] = {
    yesNoQuestion(SetUpAfterSettlorDiedYesNoPage, "setUpAfterSettlorDied", trustTypeRoutes.SetUpAfterSettlorDiedController.onPageLoad(NormalMode, draftId).url)
  }

  private def deceasedSettlorNameQuestion: Option[AnswerRow] = {
    nameQuestion(deceasedPages.SettlorsNamePage, "settlorsName", controllers.deceased_settlor.routes.SettlorsNameController.onPageLoad(NormalMode, draftId).url)
  }

  private def kindOfTrustQuestion: Option[AnswerRow] = {
    enumQuestion(KindOfTrustPage, "kindOfTrust", trustTypeRoutes.KindOfTrustController.onPageLoad(NormalMode, draftId).url, "kindOfTrust")
  }

  private def wasSetUpInAdditionToWillTrustQuestion: Option[AnswerRow] = {
    yesNoQuestion(SetUpInAdditionToWillTrustYesNoPage, "setUpInAdditionToWillTrustYesNo", trustTypeRoutes.AdditionToWillTrustYesNoController.onPageLoad(NormalMode, draftId).url)
  }

}
