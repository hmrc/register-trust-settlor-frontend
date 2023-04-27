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

package controllers

import controllers.actions.Actions
import forms.YesNoFormProvider
import models.requests.RegistrationDataRequest
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.libs.json.{JsPath, JsValue}
import play.api.mvc._
import repositories.RegistrationsRepository
import sections.LivingSettlors
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import viewmodels.{SettlorBusinessViewModel, SettlorIndividualViewModel, SettlorViewModel}
import views.html.RemoveSettlorYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class RemoveSettlorYesNoController @Inject() (
  override val messagesApi: MessagesApi,
  registrationsRepository: RegistrationsRepository,
  actions: Actions,
  yesNoFormProvider: YesNoFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: RemoveSettlorYesNoView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport {

  private val form: Form[Boolean] = yesNoFormProvider.withPrefix("settlors.removeYesNo")

  private def path(index: Int): JsPath = LivingSettlors.path \ index

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions.authWithData(draftId) { implicit request =>
    Ok(
      view(
        form,
        index,
        draftId,
        label(request.userAnswers.data, path(index))
      )
    )
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    actions.authWithData(draftId).async { implicit request =>
      form
        .bindFromRequest()
        .fold(
          (formWithErrors: Form[_]) =>
            Future.successful(
              BadRequest(
                view(
                  formWithErrors,
                  index,
                  draftId,
                  label(request.userAnswers.data, path(index))
                )
              )
            ),
          remove =>
            if (remove) {
              for {
                updatedAnswers <- Future.fromTry(
                                    request.userAnswers.deleteAtPath(path(index))
                                  )
                _              <- registrationsRepository.set(updatedAnswers)
              } yield Redirect(controllers.routes.AddASettlorController.onPageLoad(draftId))
            } else {
              Future.successful(Redirect(controllers.routes.AddASettlorController.onPageLoad(draftId)))
            }
        )
    }

  private def label(json: JsValue, path: JsPath)(implicit request: RegistrationDataRequest[AnyContent]): String = {

    val default: String = request.messages(messagesApi)("settlors.defaultText")

    (for {
      pick    <- json.transform(path.json.pick)
      settlor <- pick.validate[SettlorViewModel]
    } yield settlor match {
      case individual: SettlorIndividualViewModel => individual.name.getOrElse(default)
      case business: SettlorBusinessViewModel     => business.name.getOrElse(default)
      case _                                      => default
    }).getOrElse(default)

  }

}
