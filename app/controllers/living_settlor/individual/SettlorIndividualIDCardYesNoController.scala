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

package controllers.living_settlor.individual

import config.annotations.IndividualSettlor
import controllers.actions._
import controllers.actions.living_settlor.individual.NameRequiredActionProvider
import forms.YesNoFormProvider
import javax.inject.Inject
import models.Mode
import navigation.Navigator
import pages.living_settlor.individual.{SettlorIndividualIDCardYesNoPage, SettlorIndividualNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.living_settlor.individual.SettlorIndividualIDCardYesNoView

import scala.concurrent.{ExecutionContext, Future}

class SettlorIndividualIDCardYesNoController @Inject()(
                                                        override val messagesApi: MessagesApi,
                                                        registrationsRepository: RegistrationsRepository,
                                                        @IndividualSettlor navigator: Navigator,
                                                        actions: Actions,
                                                        requireName: NameRequiredActionProvider,
                                                        yesNoFormProvider: YesNoFormProvider,
                                                        val controllerComponents: MessagesControllerComponents,
                                                        view: SettlorIndividualIDCardYesNoView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = yesNoFormProvider.withPrefix("settlorIndividualIDCardYesNo")

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(index, draftId)) {
    implicit request =>

      val name = request.userAnswers.get(SettlorIndividualNamePage(index)).get

      val preparedForm = request.userAnswers.get(SettlorIndividualIDCardYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, index, name))
  }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(index, draftId)).async {
    implicit request =>

      val name = request.userAnswers.get(SettlorIndividualNamePage(index)).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId, index, name))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorIndividualIDCardYesNoPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorIndividualIDCardYesNoPage(index), mode, draftId)(updatedAnswers))
        }
      )
  }
}
