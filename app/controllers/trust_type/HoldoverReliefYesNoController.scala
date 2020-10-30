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

package controllers.trust_type

import config.annotations.LivingSettlor
import controllers.actions._
import forms.YesNoFormProvider
import javax.inject.Inject
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.trust_type.{HoldoverReliefYesNoPage, SetUpAfterSettlorDiedYesNoPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.trust_type.HoldoverReliefYesNoView

import scala.concurrent.{ExecutionContext, Future}

class HoldoverReliefYesNoController @Inject()(
                                                      override val messagesApi: MessagesApi,
                                                      registrationsRepository: RegistrationsRepository,
                                                      @LivingSettlor navigator: Navigator,
                                                      standardActions: Actions,
                                                      requiredAnswer: RequiredAnswerActionProvider,
                                                      yesNoFormProvider: YesNoFormProvider,
                                                      val controllerComponents: MessagesControllerComponents,
                                                      view: HoldoverReliefYesNoView
                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = yesNoFormProvider.withPrefix("holdoverReliefYesNo")

  private def actions(draftId: String) =
    standardActions.authWithData(draftId) andThen
      requiredAnswer(RequiredAnswer(SetUpAfterSettlorDiedYesNoPage, routes.SetUpAfterSettlorDiedController.onPageLoad(NormalMode, draftId)))

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val preparedForm = request.userAnswers.get(HoldoverReliefYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, mode, draftId))
  }

  def onSubmit(mode: Mode, draftId : String) = actions(draftId).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, mode, draftId))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(HoldoverReliefYesNoPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(HoldoverReliefYesNoPage, mode, draftId)(updatedAnswers))
        }
      )
  }
}
