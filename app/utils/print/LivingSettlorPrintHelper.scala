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
import controllers.living_settlor.individual.mld5.routes._
import controllers.living_settlor.individual.routes._
import controllers.living_settlor.routes._
import pages.living_settlor.SettlorIndividualOrBusinessPage
import pages.living_settlor.individual._
import pages.living_settlor.individual.mld5._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class LivingSettlorPrintHelper @Inject() (
  answerRowConverter: AnswerRowConverter,
  trustTypePrintHelper: TrustTypePrintHelper
) extends SettlorPrintHelper(trustTypePrintHelper, answerRowConverter) {

  override def answerRows(index: Int, draftId: String, prefix: Option[String] = None)(
    bound: AnswerRowConverter#Bound
  )(implicit messages: Messages): Seq[Option[AnswerRow]] = {

    val messageKeyPrefix = prefix.getOrElse("")

    Seq(
      bound.enumQuestion(
        SettlorIndividualOrBusinessPage(index),
        "settlorIndividualOrBusiness",
        SettlorIndividualOrBusinessController.onPageLoad(index, draftId).url,
        "settlorIndividualOrBusiness"
      ),
      bound.yesNoQuestion(
        SettlorAliveYesNoPage(index),
        "settlorAliveYesNo",
        SettlorAliveYesNoController.onPageLoad(index, draftId).url
      ),
      bound.nameQuestion(
        SettlorIndividualNamePage(index),
        s"settlorIndividualName$messageKeyPrefix",
        SettlorIndividualNameController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        SettlorIndividualDateOfBirthYesNoPage(index),
        "settlorIndividualDateOfBirthYesNo",
        SettlorIndividualDateOfBirthYesNoController.onPageLoad(index, draftId).url
      ),
      bound.dateQuestion(
        SettlorIndividualDateOfBirthPage(index),
        s"settlorIndividualDateOfBirth",
        SettlorIndividualDateOfBirthController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        CountryOfNationalityYesNoPage(index),
        "settlorIndividualCountryOfNationalityYesNo",
        CountryOfNationalityYesNoController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        UkCountryOfNationalityYesNoPage(index),
        s"settlorIndividualUkCountryOfNationalityYesNo$messageKeyPrefix",
        UkCountryOfNationalityYesNoController.onPageLoad(index, draftId).url
      ),
      bound.countryQuestion(
        CountryOfNationalityPage(index),
        UkCountryOfNationalityYesNoPage(index),
        s"settlorIndividualCountryOfNationality$messageKeyPrefix",
        CountryOfNationalityController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        SettlorIndividualNINOYesNoPage(index),
        "settlorIndividualNINOYesNo",
        SettlorIndividualNINOYesNoController.onPageLoad(index, draftId).url
      ),
      bound.ninoQuestion(
        SettlorIndividualNINOPage(index),
        "settlorIndividualNINO",
        SettlorIndividualNINOController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        CountryOfResidencyYesNoPage(index),
        s"settlorIndividualCountryOfResidencyYesNo$messageKeyPrefix",
        CountryOfResidencyYesNoController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        UkCountryOfResidencyYesNoPage(index),
        s"settlorIndividualUkCountryOfResidencyYesNo$messageKeyPrefix",
        UkCountryOfResidencyYesNoController.onPageLoad(index, draftId).url
      ),
      bound.countryQuestion(
        CountryOfResidencyPage(index),
        UkCountryOfResidencyYesNoPage(index),
        s"settlorIndividualCountryOfResidency$messageKeyPrefix",
        CountryOfResidencyController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        SettlorAddressYesNoPage(index),
        s"settlorIndividualAddressYesNo$messageKeyPrefix",
        SettlorIndividualAddressYesNoController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        SettlorAddressUKYesNoPage(index),
        s"settlorIndividualAddressUKYesNo$messageKeyPrefix",
        SettlorIndividualAddressUKYesNoController.onPageLoad(index, draftId).url
      ),
      bound.addressQuestion(
        SettlorAddressUKPage(index),
        s"settlorIndividualAddressUK$messageKeyPrefix",
        SettlorIndividualAddressUKController.onPageLoad(index, draftId).url
      ),
      bound.addressQuestion(
        SettlorAddressInternationalPage(index),
        s"settlorIndividualAddressInternational$messageKeyPrefix",
        SettlorIndividualAddressInternationalController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        SettlorIndividualPassportYesNoPage(index),
        "settlorIndividualPassportYesNo",
        SettlorIndividualPassportYesNoController.onPageLoad(index, draftId).url
      ),
      bound.passportOrIdCardDetailsQuestion(
        SettlorIndividualPassportPage(index),
        s"settlorIndividualPassport$messageKeyPrefix",
        SettlorIndividualPassportController.onPageLoad(index, draftId).url
      ),
      bound.yesNoQuestion(
        SettlorIndividualIDCardYesNoPage(index),
        "settlorIndividualIDCardYesNo",
        SettlorIndividualIDCardYesNoController.onPageLoad(index, draftId).url
      ),
      bound.passportOrIdCardDetailsQuestion(
        SettlorIndividualIDCardPage(index),
        s"settlorIndividualIDCard$messageKeyPrefix",
        SettlorIndividualIDCardController.onPageLoad(index, draftId).url
      ),
      bound.enumQuestion(
        MentalCapacityYesNoPage(index),
        "settlorIndividualMentalCapacityYesNo",
        MentalCapacityYesNoController.onPageLoad(index, draftId).url,
        "site"
      )
    )
  }

}
