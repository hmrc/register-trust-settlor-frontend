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
import controllers.living_settlor.business.mld5.routes._
import controllers.living_settlor.business.routes._
import controllers.living_settlor.routes._
import models.NormalMode
import pages.living_settlor.SettlorIndividualOrBusinessPage
import pages.living_settlor.business._
import pages.living_settlor.business.mld5._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class BusinessSettlorPrintHelper @Inject()(answerRowConverter: AnswerRowConverter,
                                           trustTypePrintHelper: TrustTypePrintHelper)
  extends SettlorPrintHelper(trustTypePrintHelper) {

  override def arc: AnswerRowConverter = answerRowConverter

  override def answerRows(index: Int, draftId: String)
                         (bound: AnswerRowConverter#Bound)
                         (implicit messages: Messages): Seq[Option[AnswerRow]] = Seq(
    bound.enumQuestion(SettlorIndividualOrBusinessPage(index), "settlorIndividualOrBusiness", SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId).url, "settlorIndividualOrBusiness"),
    bound.stringQuestion(SettlorBusinessNamePage(index), "settlorBusinessName", SettlorBusinessNameController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(SettlorBusinessUtrYesNoPage(index), "settlorBusinessUtrYesNo", SettlorBusinessUtrYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.stringQuestion(SettlorBusinessUtrPage(index), "settlorBusinessUtr", SettlorBusinessUtrController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(CountryOfResidenceYesNoPage(index), "settlorBusiness.5mld.countryOfResidenceYesNo", CountryOfResidenceYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(CountryOfResidenceInTheUkYesNoPage(index), "settlorBusiness.5mld.countryOfResidenceInTheUkYesNo", CountryOfResidenceInTheUkYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.countryQuestion(CountryOfResidencePage(index), CountryOfResidenceInTheUkYesNoPage(index), "settlorBusiness.5mld.countryOfResidence", CountryOfResidenceController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(SettlorBusinessAddressYesNoPage(index), "settlorBusinessAddressYesNo", SettlorBusinessAddressYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.yesNoQuestion(SettlorBusinessAddressUKYesNoPage(index), "settlorBusinessAddressUKYesNo", SettlorBusinessAddressUKYesNoController.onPageLoad(NormalMode, index, draftId).url),
    bound.addressQuestion(SettlorBusinessAddressUKPage(index), "settlorBusinessAddressUK", SettlorBusinessAddressUKController.onPageLoad(NormalMode, index, draftId).url),
    bound.addressQuestion(SettlorBusinessAddressInternationalPage(index), "settlorBusinessAddressInternational", SettlorBusinessAddressInternationalController.onPageLoad(NormalMode, index, draftId).url),
    bound.enumQuestion(SettlorBusinessTypePage(index), "settlorBusinessType", SettlorBusinessTypeController.onPageLoad(NormalMode, index, draftId).url, "kindOfBusiness"),
    bound.yesNoQuestion(SettlorBusinessTimeYesNoPage(index), "settlorBusinessTimeYesNo", SettlorBusinessTimeYesNoController.onPageLoad(NormalMode, index, draftId).url)
  )

}
