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

package controllers.living_settlor.business

import config.annotations.BusinessSettlor
import controllers.actions.Actions
import controllers.actions.living_settlor.business.NameRequiredActionProvider
import forms.YesNoFormProvider
import navigation.Navigator
import pages.living_settlor.business.SettlorBusinessTimeYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.living_settlor.business.SettlorBusinessTimeYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class SettlorBusinessTimeYesNoController @Inject()(
                                                    override val messagesApi: MessagesApi,
                                                    registrationsRepository: RegistrationsRepository,
                                                    @BusinessSettlor navigator: Navigator,
                                                    actions: Actions,
                                                    requireName: NameRequiredActionProvider,
                                                    yesNoFormProvider: YesNoFormProvider,
                                                    val controllerComponents: MessagesControllerComponents,
                                                    view: SettlorBusinessTimeYesNoView
                                                  )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = yesNoFormProvider.withPrefix("settlorBusinessTimeYesNo")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(index, draftId)) {
    implicit request =>

      val preparedForm = request.userAnswers.get(SettlorBusinessTimeYesNoPage(index)) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, index, request.businessName))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(index, draftId)).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, index, request.businessName))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorBusinessTimeYesNoPage(index), value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(SettlorBusinessTimeYesNoPage(index), draftId)(updatedAnswers))
        }
      )
  }
}
