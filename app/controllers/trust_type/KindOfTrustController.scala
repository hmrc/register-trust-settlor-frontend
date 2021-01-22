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

package controllers.trust_type

import config.annotations.TrustType
import controllers.actions._
import forms.KindOfTrustFormProvider
import models.pages.KindOfTrust
import models.requests.RegistrationDataRequest

import javax.inject.Inject
import models.{Enumerable, Mode, NormalMode}
import navigation.Navigator
import pages.trust_type.{KindOfTrustPage, SetUpAfterSettlorDiedYesNoPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.trust_type.KindOfTrustView

import scala.concurrent.{ExecutionContext, Future}

class KindOfTrustController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: RegistrationsRepository,
                                       @TrustType navigator: Navigator,
                                       standardActions: Actions,
                                       formProvider: KindOfTrustFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       view: KindOfTrustView,
                                       requiredAnswer: RequiredAnswerActionProvider
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActions.authWithData(draftId) andThen
      requiredAnswer(RequiredAnswer(SetUpAfterSettlorDiedYesNoPage, routes.SetUpAfterSettlorDiedController.onPageLoad(NormalMode, draftId)))

  private val form: Form[KindOfTrust] = formProvider()

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(KindOfTrustPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId))
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(KindOfTrustPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(KindOfTrustPage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
