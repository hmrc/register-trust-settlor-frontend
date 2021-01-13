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

package controllers.living_settlor.individual.mld5

import config.annotations.IndividualSettlor
import controllers.actions._
import controllers.actions.living_settlor.individual.NameRequiredActionProvider
import forms.YesNoFormProvider
import models.Mode
import models.requests.SettlorIndividualNameRequest
import navigation.Navigator
import pages.living_settlor.individual.mld5.CountryOfNationalityYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.living_settlor.individual.mld5.CountryOfNationalityYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountryOfNationalityYesNoController @Inject()(
                                                     override val messagesApi: MessagesApi,
                                                     registrationsRepository: RegistrationsRepository,
                                                     @IndividualSettlor navigator: Navigator,
                                                     actions: Actions,
                                                     requireName: NameRequiredActionProvider,
                                                     yesNoFormProvider: YesNoFormProvider,
                                                     val controllerComponents: MessagesControllerComponents,
                                                     view: CountryOfNationalityYesNoView
                                                   )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = yesNoFormProvider.withPrefix("settlorIndividualCountryOfNationalityYesNo")

  private def action(index: Int, draftId: String): ActionBuilder[SettlorIndividualNameRequest, AnyContent] = {
    actions.authWithData(draftId) andThen requireName(index, draftId)
  }

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = action(index, draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(CountryOfNationalityYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, index, draftId, request.name))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = action(index, draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, index, draftId, request.name))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CountryOfNationalityYesNoPage(index), value))
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CountryOfNationalityYesNoPage(index), mode, draftId, is5mldEnabled = true)(updatedAnswers))
        }
      )
  }
}
