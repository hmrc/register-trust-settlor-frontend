/*
 * Copyright 2024 HM Revenue & Customs
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

package controllers.living_settlor.individual.mld5

import config.annotations.IndividualSettlor
import controllers.actions._
import controllers.actions.living_settlor.individual.NameRequiredActionProvider
import forms.YesNoFormProvider
import models.requests.SettlorIndividualNameRequest
import navigation.Navigator
import pages.living_settlor.individual.mld5.UkCountryOfNationalityYesNoPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.errors.TechnicalErrorView
import views.html.living_settlor.individual.mld5.UkCountryOfNationalityYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class UkCountryOfNationalityYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  registrationsRepository: RegistrationsRepository,
  @IndividualSettlor navigator: Navigator,
  actions: Actions,
  requireName: NameRequiredActionProvider,
  yesNoFormProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: UkCountryOfNationalityYesNoView,
  technicalErrorView: TechnicalErrorView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private def form(messageKey: String): Form[Boolean] = yesNoFormProvider.withPrefix(messageKey)

  private def action(index: Int, draftId: String): ActionBuilder[SettlorIndividualNameRequest, AnyContent] =
    actions.authWithData(draftId) andThen requireName(index, draftId)

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = action(index, draftId) { implicit request =>
    val messageKeyPrefix =
      if (request.settlorAliveAtRegistration(index)) {
        "settlorIndividualUkCountryOfNationalityYesNo"
      } else {
        "settlorIndividualUkCountryOfNationalityYesNoPastTense"
      }

    val preparedForm = request.userAnswers.get(UkCountryOfNationalityYesNoPage(index)) match {
      case None        => form(messageKeyPrefix)
      case Some(value) => form(messageKeyPrefix).fill(value)
    }

    Ok(view(preparedForm, index, draftId, request.name, request.settlorAliveAtRegistration(index)))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = action(index, draftId).async { implicit request =>
    val messageKeyPrefix =
      if (request.settlorAliveAtRegistration(index)) {
        "settlorIndividualUkCountryOfNationalityYesNo"
      } else {
        "settlorIndividualUkCountryOfNationalityYesNoPastTense"
      }

    form(messageKeyPrefix)
      .bindFromRequest()
      .fold(
        (formWithErrors: Form[Boolean]) =>
          Future.successful(
            BadRequest(view(formWithErrors, index, draftId, request.name, request.settlorAliveAtRegistration(index)))
          ),
        value =>
          request.userAnswers.set(UkCountryOfNationalityYesNoPage(index), value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                Redirect(navigator.nextPage(UkCountryOfNationalityYesNoPage(index), draftId)(updatedAnswers))
              }
            case Failure(_)              =>
              logger.error("[UkCountryOfNationalityYesNoController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(technicalErrorView()))
          }
      )
  }
}
