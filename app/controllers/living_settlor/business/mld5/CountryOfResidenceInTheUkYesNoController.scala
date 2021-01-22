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

package controllers.living_settlor.business.mld5

import config.annotations.BusinessSettlor
import controllers.actions.Actions
import controllers.actions.living_settlor.business.NameRequiredActionProvider
import forms.YesNoFormProvider
import models.Mode
import navigation.Navigator
import pages.living_settlor.business.mld5.CountryOfResidenceInTheUkYesNoPage
import play.api.data.Form
import play.api.i18n._
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.living_settlor.business.mld5.CountryOfResidenceInTheUkYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CountryOfResidenceInTheUkYesNoController @Inject()(
                                                          val controllerComponents: MessagesControllerComponents,
                                                          @BusinessSettlor navigator: Navigator,
                                                          actions: Actions,
                                                          formProvider: YesNoFormProvider,
                                                          view: CountryOfResidenceInTheUkYesNoView,
                                                          repository: RegistrationsRepository,
                                                          requireName: NameRequiredActionProvider
                                                        )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private val form: Form[Boolean] = formProvider.withPrefix("settlorBusiness.5mld.countryOfResidenceInTheUkYesNo")

  def onPageLoad(mode: Mode, index: Int, draftId: String): Action[AnyContent] =
    actions.authWithData(draftId).andThen(requireName(index, draftId)) {
      implicit request =>

        val preparedForm = request.userAnswers.get(CountryOfResidenceInTheUkYesNoPage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, mode, draftId , index, request.businessName))
    }

  def onSubmit(mode: Mode, index: Int, draftId: String): Action[AnyContent] =
    actions.authWithData(draftId).andThen(requireName(index, draftId)).async {
      implicit request =>

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, mode, draftId , index, request.businessName))),

          value =>
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(CountryOfResidenceInTheUkYesNoPage(index), value))
              _              <- repository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(CountryOfResidenceInTheUkYesNoPage(index), mode, draftId)(updatedAnswers))
        )
    }
}
