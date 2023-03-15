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

package controllers.living_settlor.individual.mld5

import config.annotations.IndividualSettlor
import controllers.actions._
import controllers.actions.living_settlor.individual.NameRequiredActionProvider
import forms.CountryFormProvider
import models.requests.SettlorIndividualNameRequest
import navigation.Navigator
import pages.living_settlor.individual.SettlorAliveYesNoPage
import pages.living_settlor.individual.mld5.CountryOfNationalityPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.countryOptions.CountryOptionsNonUK
import views.html.errors.TechnicalErrorView
import views.html.living_settlor.individual.mld5.CountryOfNationalityView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class CountryOfNationalityController @Inject()(
                                                override val messagesApi: MessagesApi,
                                                registrationsRepository: RegistrationsRepository,
                                                @IndividualSettlor navigator: Navigator,
                                                actions: Actions,
                                                requireName: NameRequiredActionProvider,
                                                countryFormProvider: CountryFormProvider,
                                                val controllerComponents: MessagesControllerComponents,
                                                view: CountryOfNationalityView,
                                                countryOptions: CountryOptionsNonUK,
                                                technicalErrorView: TechnicalErrorView
                                              )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private def form(messageKey: String): Form[String] = countryFormProvider.withPrefix(messageKey)

  private def action(index: Int, draftId: String): ActionBuilder[SettlorIndividualNameRequest, AnyContent] = {
    actions.authWithData(draftId) andThen requireName(index, draftId)
  }

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = action(index, draftId) {
    implicit request =>

      val messageKeyPrefix =
        if(settlorAliveAtRegistration(index)) "settlorIndividualCountryOfNationality" else "settlorIndividualCountryOfNationalityPastTense"

      val preparedForm = request.userAnswers.get(CountryOfNationalityPage(index)) match {
        case None => form(messageKeyPrefix)
        case Some(value) => form(messageKeyPrefix).fill(value)
      }

      Ok(view(preparedForm, index, draftId, countryOptions.options(), request.name, settlorAliveAtRegistration(index)))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = action(index, draftId).async {
    implicit request =>

      val messageKeyPrefix =
        if(settlorAliveAtRegistration(index)) "settlorIndividualCountryOfNationality" else "settlorIndividualCountryOfNationalityPastTense"

      form(messageKeyPrefix).bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, index, draftId, countryOptions.options(), request.name, settlorAliveAtRegistration(index)))),
        value => {
          request.userAnswers.set(CountryOfNationalityPage(index), value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                Redirect(navigator.nextPage(CountryOfNationalityPage(index), draftId)(updatedAnswers))
              }
            case Failure(_) =>
              logger.error("[CountryOfNationalityController][onSubmit] Error while storing user answers")
              Future.successful(InternalServerError(technicalErrorView()))
          }
        }
      )
  }

  private def settlorAliveAtRegistration(index: Int)(implicit request: SettlorIndividualNameRequest[AnyContent]): Boolean = {
    request.userAnswers.get(SettlorAliveYesNoPage(index)) match {
      case Some(value) => value
      case None => false
    }
  }
}
