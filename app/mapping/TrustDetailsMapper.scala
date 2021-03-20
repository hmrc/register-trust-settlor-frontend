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

package mapping

import mapping.TypeOfTrust.WillTrustOrIntestacyTrust
import models.UserAnswers
import models.pages.KindOfTrust._
import models.pages.{DeedOfVariation, KindOfTrust}
import pages.trust_type._
import play.api.Logger
import sections.{DeceasedSettlor, LivingSettlors}

class TrustDetailsMapper {

  private val logger: Logger = Logger(getClass)

  def build(userAnswers: UserAnswers): Option[TrustDetailsType] = {
    for {
      typeOfTrust <- trustType(userAnswers)
    } yield {
      TrustDetailsType(
        typeOfTrust = typeOfTrust,
        deedOfVariation = deedOfVariation(userAnswers),
        interVivos = userAnswers.get(HoldoverReliefYesNoPage),
        efrbsStartDate = userAnswers.get(EfrbsStartDatePage)
      )
    }
  }

  private def trustType(userAnswers: UserAnswers): Option[TypeOfTrust] = {

    if (userAnswers.isTaxable) {
      val settlors = (
        userAnswers.get(LivingSettlors),
        userAnswers.get(DeceasedSettlor)
      )

      settlors match {
        case (Some(_), None) =>
          userAnswers.get(KindOfTrustPage).map(mapTrustTypeToDes)
        case (None, Some(_)) =>
          Some(WillTrustOrIntestacyTrust)
        case (None, None) =>
          logger.info("[trustType] - Cannot build trust type due to no settlors")
          None
        case (Some(_), Some(_)) =>
          logger.warn("[trustType] - User answers are in an invalid state")
          None
      }
    } else {
      None
    }
  }

  private def deedOfVariation(userAnswers: UserAnswers): Option[DeedOfVariation] =
    userAnswers.get(HowDeedOfVariationCreatedPage) orElse (userAnswers.get(SetUpInAdditionToWillTrustYesNoPage) match {
      case Some(true) => Some(DeedOfVariation.AdditionToWill)
      case _ => None
    })

  private def mapTrustTypeToDes(kind: KindOfTrust): TypeOfTrust = {
    kind match {
      case Intervivos => TypeOfTrust.IntervivosSettlementTrust
      case Deed => TypeOfTrust.DeedOfVariation
      case Employees => TypeOfTrust.EmployeeRelated
      case FlatManagement => TypeOfTrust.FlatManagementTrust
      case HeritageMaintenanceFund => TypeOfTrust.HeritageTrust
    }
  }

}
