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
import controllers.living_settlor.individual.mld5.routes._
import controllers.living_settlor.individual.routes._
import controllers.living_settlor.routes._
import models.NormalMode
import pages.living_settlor.SettlorIndividualOrBusinessPage
import pages.living_settlor.individual._
import pages.living_settlor.individual.mld5._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class LivingSettlorPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                         trustTypePrintHelper: TrustTypePrintHelper)
  extends SettlorPrintHelper(trustTypePrintHelper) {

  override def arc: AnswerRowConverter = answerRowConverter

  override def answerRows(index: Int, draftId: String)
                         (bound: AnswerRowConverter#Bound)
                         (implicit messages: Messages): Seq[Option[AnswerRow]] = Seq(
    bound.enumQuestion(SettlorIndividualOrBusinessPage(index), "settlorIndividualOrBusiness", SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId).url, "settlorIndividualOrBusiness"),
    bound.nameQuestion(SettlorIndividualNamePage(index), "settlorIndividualName", SettlorIndividualNameController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(SettlorIndividualDateOfBirthYesNoPage(index), "settlorIndividualDateOfBirthYesNo", SettlorIndividualDateOfBirthYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.dateQuestion(SettlorIndividualDateOfBirthPage(index), "settlorIndividualDateOfBirth", SettlorIndividualDateOfBirthController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(CountryOfNationalityYesNoPage(index), "settlorIndividualCountryOfNationalityYesNo", CountryOfNationalityYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(UkCountryOfNationalityYesNoPage(index), "settlorIndividualUkCountryOfNationalityYesNo", UkCountryOfNationalityYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.countryQuestion(CountryOfNationalityPage(index), UkCountryOfNationalityYesNoPage(index), "settlorIndividualCountryOfNationality", CountryOfNationalityController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(SettlorIndividualNINOYesNoPage(index), "settlorIndividualNINOYesNo", SettlorIndividualNINOYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.ninoQuestion(SettlorIndividualNINOPage(index), "settlorIndividualNINO", SettlorIndividualNINOController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(CountryOfResidencyYesNoPage(index), "settlorIndividualCountryOfResidencyYesNo", CountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(UkCountryOfResidencyYesNoPage(index), "settlorIndividualUkCountryOfResidencyYesNo", UkCountryOfResidencyYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.countryQuestion(CountryOfResidencyPage(index), UkCountryOfResidencyYesNoPage(index), "settlorIndividualCountryOfResidency", CountryOfResidencyController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(SettlorAddressYesNoPage(index), "settlorIndividualAddressYesNo", SettlorIndividualAddressYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(SettlorAddressUKYesNoPage(index), "settlorIndividualAddressUKYesNo", SettlorIndividualAddressUKYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.addressQuestion(SettlorAddressUKPage(index), "settlorIndividualAddressUK", SettlorIndividualAddressUKController.onPageLoad(NormalMode, index, draftId).url),
    bound.addressQuestion(SettlorAddressInternationalPage(index), "settlorIndividualAddressInternational", SettlorIndividualAddressInternationalController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(SettlorIndividualPassportYesNoPage(index), "settlorIndividualPassportYesNo", SettlorIndividualPassportYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.passportOrIdCardDetailsQuestion(SettlorIndividualPassportPage(index), "settlorIndividualPassport", SettlorIndividualPassportController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(SettlorIndividualIDCardYesNoPage(index), "settlorIndividualIDCardYesNo", SettlorIndividualIDCardYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.passportOrIdCardDetailsQuestion(SettlorIndividualIDCardPage(index), "settlorIndividualIDCard", SettlorIndividualIDCardController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(MentalCapacityYesNoPage(index), "settlorIndividualMentalCapacityYesNo", MentalCapacityYesNoController.onPageLoad(NormalMode, index, draftId).url)
  )

}
