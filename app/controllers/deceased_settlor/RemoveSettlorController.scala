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

import controllers.actions.Actions
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import pages.deceased_settlor.SettlorsNamePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.HtmlFormat
import queries.RemoveDeceasedSettlorQuery
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.deceased_settlor.RemoveDeceasedSettlorView

import scala.concurrent.{ExecutionContext, Future}

class RemoveSettlorController @Inject()(
                                         override implicit val messagesApi: MessagesApi,
                                         val registrationsRepository: RegistrationsRepository,
                                         actions: Actions,
                                         val formProvider: RemoveIndexFormProvider,
                                         val controllerComponents: MessagesControllerComponents,
                                         val removeView: RemoveDeceasedSettlorView
                                       )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val messagesPrefix : String = "removeSettlor"

  lazy val form: Form[Boolean] = formProvider(messagesPrefix)

  private def view(form: Form[_], draftId: String)
          (implicit request: RegistrationDataRequest[AnyContent]): HtmlFormat.Appendable = {
    removeView(messagesPrefix, form, draftId, content)
  }

  private def content(implicit request: RegistrationDataRequest[AnyContent]) : String =
    request.userAnswers.get(SettlorsNamePage)
      .map(_.toString)
      .getOrElse(Messages(s"$messagesPrefix.default"))


  def onPageLoad(draftId: String): Action[AnyContent] = actions.authWithData(draftId) {
    implicit request =>
      Ok(view(form, draftId))
  }

  def onSubmit(draftId : String) = actions.authWithData(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId))),
        value => {
          if (value) {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.remove(RemoveDeceasedSettlorQuery))
              _              <- registrationsRepository.set(updatedAnswers)
            } yield Redirect(controllers.routes.AddASettlorController.onPageLoad(draftId).url)
          } else {
            Future.successful(Redirect(controllers.routes.AddASettlorController.onPageLoad(draftId).url))
          }
        }
      )
  }


}