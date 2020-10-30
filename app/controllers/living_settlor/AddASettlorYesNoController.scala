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

package controllers.living_settlor

import controllers.actions.Actions
import forms.AddASettlorFormProvider
import javax.inject.Inject
import models.NormalMode
import models.pages.AddASettlor
import navigation.Navigator
import pages.{AddASettlorPage, AddAnotherSettlorYesNoPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.living_settlor.AddAnotherSettlorYesNoView

import scala.concurrent.{ExecutionContext, Future}

class AddASettlorYesNoController @Inject()(
                                            override val messagesApi: MessagesApi,
                                            registrationsRepository: RegistrationsRepository,
                                            navigator: Navigator,
                                            actions: Actions,
                                            formProvider: AddASettlorFormProvider,
                                            val controllerComponents: MessagesControllerComponents,
                                            view: AddAnotherSettlorYesNoView
                                          )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[AddASettlor] = formProvider()

  def onPageLoad(draftId: String): Action[AnyContent] = actions.authWithData(draftId) {
    implicit request =>
      Ok(view(form, draftId))
  }

  def onSubmit(draftId: String) = actions.authWithData(draftId).async {
    implicit request =>
      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddASettlorPage, value))
            _ <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddAnotherSettlorYesNoPage, NormalMode, draftId)(updatedAnswers))
        }
      )
  }

}
