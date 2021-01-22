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

package controllers.deceased_settlor

import config.annotations.DeceasedSettlor
import controllers.actions._
import controllers.actions.deceased_settlor.NameRequiredActionProvider
import forms.deceased_settlor.SettlorsDateOfBirthFormProvider
import models.Mode
import models.requests.SettlorIndividualNameRequest
import navigation.Navigator
import pages.deceased_settlor.{SettlorDateOfDeathPage, SettlorsDateOfBirthPage, SettlorsNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.deceased_settlor.SettlorsDateOfBirthView

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SettlorsDateOfBirthController @Inject()(
                                               override val messagesApi: MessagesApi,
                                               registrationsRepository: RegistrationsRepository,
                                               @DeceasedSettlor navigator: Navigator,
                                               actions: Actions,
                                               requireName: NameRequiredActionProvider,
                                               formProvider: SettlorsDateOfBirthFormProvider,
                                               val controllerComponents: MessagesControllerComponents,
                                               view: SettlorsDateOfBirthView
                                             )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form(maxDate: (LocalDate, String)): Form[LocalDate] =
    formProvider.withConfig(maxDate)

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(draftId)) {
    implicit request =>

      val name = request.userAnswers.get(SettlorsNamePage).get

      val preparedForm = request.userAnswers.get(SettlorsDateOfBirthPage) match {
        case None => form(maxDate)
        case Some(value) => form(maxDate).fill(value)
      }

      Ok(view(preparedForm, mode, draftId, name))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(draftId)).async {
    implicit request =>

      val name = request.userAnswers.get(SettlorsNamePage).get

      form(maxDate).bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, name))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorsDateOfBirthPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorsDateOfBirthPage, mode, draftId)(updatedAnswers))
        }
      )
  }

  private def maxDate(implicit request: SettlorIndividualNameRequest[AnyContent]): (LocalDate, String) = {
    request.userAnswers.get(SettlorDateOfDeathPage) match {
      case Some(dateOfDeath) =>
        (dateOfDeath, "afterDateOfDeath")
      case None =>
        (LocalDate.now, "future")
    }
  }
}
