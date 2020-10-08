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

package controllers.deceased_settlor

import config.annotations.DeceasedSettlor
import controllers.actions._
import controllers.actions.deceased_settlor.NameRequiredActionProvider
import forms.deceased_settlor.SettlorNationalInsuranceNumberFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.deceased_settlor.{SettlorNationalInsuranceNumberPage, SettlorsNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.deceased_settlor.SettlorNationalInsuranceNumberView

import scala.concurrent.{ExecutionContext, Future}

class SettlorNationalInsuranceNumberController @Inject()(
                                                          override val messagesApi: MessagesApi,
                                                          registrationsRepository: RegistrationsRepository,
                                                          @DeceasedSettlor navigator: Navigator,
                                                          actions: Actions,
                                                          requireName: NameRequiredActionProvider,
                                                          formProvider: SettlorNationalInsuranceNumberFormProvider,
                                                          val controllerComponents: MessagesControllerComponents,
                                                          view: SettlorNationalInsuranceNumberView
                                    )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form = formProvider()

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(draftId)) {
    implicit request =>

      val name = request.userAnswers.get(SettlorsNamePage).get

      val preparedForm = request.userAnswers.get(SettlorNationalInsuranceNumberPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId, name))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(draftId)).async {
    implicit request =>

      val name = request.userAnswers.get(SettlorsNamePage).get

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId , name))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorNationalInsuranceNumberPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorNationalInsuranceNumberPage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
