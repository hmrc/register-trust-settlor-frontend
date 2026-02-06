/*
 * Copyright 2026 HM Revenue & Customs
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
import pages.living_settlor.SettlorIndividualOrBusinessPage
import pages.living_settlor.business._
import pages.living_settlor.business.mld5._
import play.api.i18n.Messages
import viewmodels.AnswerRow

class BusinessSettlorPrintHelper @Inject() (
  answerRowConverter: AnswerRowConverter,
  trustTypePrintHelper: TrustTypePrintHelper
) extends SettlorPrintHelper(trustTypePrintHelper, answerRowConverter) {

  override def answerRows(index: Int, draftId: String, prefix: Option[String] = None)(bound: AnswerRowConverter#Bound)(
    implicit messages: Messages
  ): Seq[Option[AnswerRow]] = Seq(
    bound.enumQuestion(
      SettlorIndividualOrBusinessPage(index),
      "settlorIndividualOrBusiness",
      SettlorIndividualOrBusinessController.onPageLoad(index, draftId).url,
      "settlorIndividualOrBusiness"
    ),
    bound.stringQuestion(
      SettlorBusinessNamePage(index),
      "settlorBusinessName",
      SettlorBusinessNameController.onPageLoad(index, draftId).url
    ),
    bound.yesNoQuestion(
      SettlorBusinessUtrYesNoPage(index),
      "settlorBusinessUtrYesNo",
      SettlorBusinessUtrYesNoController.onPageLoad(index, draftId).url
    ),
    bound.stringQuestion(
      SettlorBusinessUtrPage(index),
      "settlorBusinessUtr",
      SettlorBusinessUtrController.onPageLoad(index, draftId).url
    ),
    bound.yesNoQuestion(
      CountryOfResidenceYesNoPage(index),
      "settlorBusiness.5mld.countryOfResidenceYesNo",
      CountryOfResidenceYesNoController.onPageLoad(index, draftId).url
    ),
    bound.yesNoQuestion(
      CountryOfResidenceInTheUkYesNoPage(index),
      "settlorBusiness.5mld.countryOfResidenceInTheUkYesNo",
      CountryOfResidenceInTheUkYesNoController.onPageLoad(index, draftId).url
    ),
    bound.countryQuestion(
      CountryOfResidencePage(index),
      CountryOfResidenceInTheUkYesNoPage(index),
      "settlorBusiness.5mld.countryOfResidence",
      CountryOfResidenceController.onPageLoad(index, draftId).url
    ),
    bound.yesNoQuestion(
      SettlorBusinessAddressYesNoPage(index),
      "settlorBusinessAddressYesNo",
      SettlorBusinessAddressYesNoController.onPageLoad(index, draftId).url
    ),
    bound.yesNoQuestion(
      SettlorBusinessAddressUKYesNoPage(index),
      "settlorBusinessAddressUKYesNo",
      SettlorBusinessAddressUKYesNoController.onPageLoad(index, draftId).url
    ),
    bound.addressQuestion(
      SettlorBusinessAddressUKPage(index),
      "settlorBusinessAddressUK",
      SettlorBusinessAddressUKController.onPageLoad(index, draftId).url
    ),
    bound.addressQuestion(
      SettlorBusinessAddressInternationalPage(index),
      "settlorBusinessAddressInternational",
      SettlorBusinessAddressInternationalController.onPageLoad(index, draftId).url
    ),
    bound.enumQuestion(
      SettlorBusinessTypePage(index),
      "settlorBusinessType",
      SettlorBusinessTypeController.onPageLoad(index, draftId).url,
      "kindOfBusiness"
    ),
    bound.yesNoQuestion(
      SettlorBusinessTimeYesNoPage(index),
      "settlorBusinessTimeYesNo",
      SettlorBusinessTimeYesNoController.onPageLoad(index, draftId).url
    )
  )

}
