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

package controllers.deceased_settlor.mld5

import config.annotations.DeceasedSettlor
import controllers.actions._
import controllers.actions.deceased_settlor.NameRequiredActionProvider
import forms.YesNoFormProvider
import navigation.Navigator
import pages.deceased_settlor.mld5.CountryOfResidenceInTheUkYesNoPage
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.deceased_settlor.mld5.CountryOfResidenceInTheUkYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountryOfResidenceInTheUkYesNoController @Inject()(
                                                          override val messagesApi: MessagesApi,
                                                          registrationsRepository: RegistrationsRepository,
                                                          @DeceasedSettlor navigator: Navigator,
                                                          actions: Actions,
                                                          requireName: NameRequiredActionProvider,
                                                          yesNoFormProvider: YesNoFormProvider,
                                                          val controllerComponents: MessagesControllerComponents,
                                                          view: CountryOfResidenceInTheUkYesNoView
                                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = yesNoFormProvider.withPrefix("5mld.countryOfResidenceInTheUkYesNo")

  def onPageLoad(draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(draftId)) {
    implicit request =>

      val preparedForm = request.userAnswers.get(CountryOfResidenceInTheUkYesNoPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      Ok(view(preparedForm, draftId, request.name))
  }

  def onSubmit(draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(draftId)).async {
    implicit request =>

      form.bindFromRequest().fold(
        (formWithErrors: Form[_]) =>
          Future.successful(BadRequest(view(formWithErrors, draftId, request.name))),

        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CountryOfResidenceInTheUkYesNoPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CountryOfResidenceInTheUkYesNoPage, draftId)(updatedAnswers))
        }
      )
  }
}
