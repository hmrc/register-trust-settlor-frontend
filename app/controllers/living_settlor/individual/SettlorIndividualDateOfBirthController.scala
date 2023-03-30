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

package controllers.living_settlor.individual

import config.annotations.IndividualSettlor
import controllers.actions._
import controllers.actions.living_settlor.individual.NameRequiredActionProvider
import forms.DateOfBirthFormProvider
import navigation.Navigator
import pages.living_settlor.individual.{SettlorIndividualDateOfBirthPage, SettlorIndividualNamePage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.errors.TechnicalErrorView
import views.html.living_settlor.individual.SettlorIndividualDateOfBirthView

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class SettlorIndividualDateOfBirthController @Inject()(
                                                        override val messagesApi: MessagesApi,
                                                        registrationsRepository: RegistrationsRepository,
                                                        @IndividualSettlor navigator: Navigator,
                                                        actions: Actions,
                                                        requireName: NameRequiredActionProvider,
                                                        formProvider: DateOfBirthFormProvider,
                                                        val controllerComponents: MessagesControllerComponents,
                                                        view: SettlorIndividualDateOfBirthView,
                                                        technicalErrorView: TechnicalErrorView
                                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def form: Form[LocalDate] = formProvider()

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(index, draftId)) {
    implicit request =>

      val name = request.userAnswers.get(SettlorIndividualNamePage(index)).get

      val preparedForm = request.userAnswers.get(SettlorIndividualDateOfBirthPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index, name))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(index, draftId)).async {
    implicit request =>

      val name = request.userAnswers.get(SettlorIndividualNamePage(index)).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, name))),
        value => {
          request.userAnswers.set(SettlorIndividualDateOfBirthPage(index), value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                Redirect(navigator.nextPage(SettlorIndividualDateOfBirthPage(index), draftId)(updatedAnswers))
              }
            case Failure(_) => {
              logger.error("[SettlorIndividualDateOfBirthController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(technicalErrorView()))
            }
          }
        }
      )
  }
}
