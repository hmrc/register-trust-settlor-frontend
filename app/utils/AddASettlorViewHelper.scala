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

import controllers.deceased_settlor.{routes => deceasedRoutes}
import controllers.living_settlor.business.{routes => businessRoutes}
import controllers.living_settlor.individual.{routes => individualRoutes}
import controllers.routes
import models.{NormalMode, UserAnswers}
import models.pages.Status._
import play.api.i18n.Messages
import sections.{DeceasedSettlor, LivingSettlors}
import viewmodels.{AddRow, AddToRows, SettlorViewModel, _}

class AddASettlorViewHelper(userAnswers: UserAnswers, draftId: String)(implicit messages: Messages) {

  def rows: AddToRows =
    deceasedSettlors match {
      case Some(s: SettlorViewModel) if s.status equals Completed =>
        AddToRows(
          inProgress = livingSettlors._2.flatMap(parseSettlor),
          complete = livingSettlors._1.flatMap(parseSettlor) :+ parseToRows(s, 0)
        )
      case Some(s: SettlorViewModel) if s.status equals InProgress =>
        AddToRows(
          inProgress = livingSettlors._2.flatMap(parseSettlor) :+ parseToRows(s, 0),
          complete = livingSettlors._1.flatMap(parseSettlor)
        )
      case _ =>
        AddToRows(
          inProgress = livingSettlors._2.flatMap(parseSettlor),
          complete = livingSettlors._1.flatMap(parseSettlor)
        )
    }

  val livingSettlors =
    userAnswers.get(LivingSettlors)
      .toList
      .flatten
      .zipWithIndex
      .partition(_._1.status == Completed)

  val deceasedSettlors =
    userAnswers.get(DeceasedSettlor)

  private def parseSettlor(settlor: (SettlorViewModel, Int)): Option[AddRow] = {
    val vm = settlor._1
    val index = settlor._2

    Some(parseToRows(vm, index))
  }

  private def parseToRows(vm: SettlorViewModel, index: Int): AddRow = {

    vm match {

      case SettlorLivingIndividualViewModel(_, name, status) => AddRow(
        name = name,
        typeLabel = messages("entity.settlor.individual"),
        changeUrl = if (status == InProgress) {
          individualRoutes.SettlorIndividualNameController.onPageLoad(NormalMode, index, draftId).url
        } else {
          individualRoutes.SettlorIndividualAnswerController.onPageLoad(index, draftId).url
        },
        removeUrl = routes.RemoveSettlorYesNoController.onPageLoadLiving(index, draftId).url
      )

      case SettlorBusinessTypeViewModel(_, name, status) => AddRow(
        name = name,
        typeLabel = messages("entity.settlor.business"),
        changeUrl = if (status == InProgress) {
          businessRoutes.SettlorBusinessNameController.onPageLoad(NormalMode, index, draftId).url
        } else {
          businessRoutes.SettlorBusinessAnswerController.onPageLoad(index, draftId).url
        },
        removeUrl = routes.RemoveSettlorYesNoController.onPageLoadLiving(index, draftId).url
      )

      case SettlorDeceasedIndividualViewModel(_, name, status) => AddRow(
        name = name,
        typeLabel = messages("entity.settlor.deceased"),
        changeUrl = if (status == InProgress) {
          deceasedRoutes.SettlorsNameController.onPageLoad(NormalMode, draftId).url
        } else {
          deceasedRoutes.DeceasedSettlorAnswerController.onPageLoad(draftId).url
        },
        removeUrl = routes.RemoveSettlorYesNoController.onPageLoadDeceased(draftId).url
      )

      case DefaultSettlorViewModel(_, status) => AddRow(
        name = messages("entities.no.name.added"),
        typeLabel = messages("entity.settlor.individual"),
        changeUrl = if (status == InProgress) {
          individualRoutes.SettlorIndividualNameController.onPageLoad(NormalMode, index, draftId).url
        } else {
          individualRoutes.SettlorIndividualAnswerController.onPageLoad(index, draftId).url
        },
        removeUrl = routes.RemoveSettlorYesNoController.onPageLoadLiving(index, draftId).url
      )

    }

  }

}