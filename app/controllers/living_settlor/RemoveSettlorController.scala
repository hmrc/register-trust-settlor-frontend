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
import forms.RemoveIndexFormProvider
import javax.inject.Inject
import models.requests.RegistrationDataRequest
import pages.living_settlor.SettlorIndividualNamePage
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import play.twirl.api.HtmlFormat
import queries.RemoveSettlorQuery
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.living_settlor.RemoveSettlorView

import scala.concurrent.Future

class RemoveSettlorController @Inject()(override val messagesApi: MessagesApi,
                                        registrationsRepository: RegistrationsRepository,
                                        actions: Actions,
                                        formProvider: RemoveIndexFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        removeView: RemoveSettlorView) extends FrontendBaseController with I18nSupport {

  val messagesPrefix : String = "removeSettlor"

  lazy val form: Form[Boolean] = formProvider.apply(messagesPrefix)

  def content(index: Int)(implicit request: RegistrationDataRequest[AnyContent]) : String =
    request.userAnswers.get(SettlorIndividualNamePage(index)).map(_.toString).getOrElse(Messages(s"$messagesPrefix.default"))

  def view(form: Form[_], index: Int, draftId: String)
          (implicit request: RegistrationDataRequest[AnyContent]): HtmlFormat.Appendable = {
    removeView(messagesPrefix, form, index, draftId, content(index), routes.RemoveSettlorController.onSubmit(index, draftId))
  }

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions.authWithData(draftId) {
    implicit request =>
      Ok(view(form, index, draftId))
  }

  def onSubmit(index: Int, draftId : String) = actions.authWithData(draftId).async {
    implicit request =>

      import scala.concurrent.ExecutionContext.Implicits._

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, index, draftId))),
        value => {
          if (value) {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.remove(RemoveSettlorQuery(index)))
              _              <- registrationsRepository.set(updatedAnswers)
            } yield Redirect(controllers.routes.AddASettlorController.onPageLoad(draftId).url)
          } else {
            Future.successful(Redirect(controllers.routes.AddASettlorController.onPageLoad(draftId).url))
          }
        }
      )
  }

}
