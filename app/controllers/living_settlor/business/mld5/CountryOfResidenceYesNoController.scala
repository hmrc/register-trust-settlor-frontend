/*
 * Copyright 2022 HM Revenue & Customs
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
import navigation.Navigator
import pages.living_settlor.business.mld5.CountryOfResidenceYesNoPage
import play.api.Logging
import play.api.data.Form
import play.api.i18n._
import play.api.mvc._
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.errors.TechnicalErrorView
import views.html.living_settlor.business.mld5.CountryOfResidenceYesNoView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class CountryOfResidenceYesNoController @Inject()(
                                                   val controllerComponents: MessagesControllerComponents,
                                                   repository: RegistrationsRepository,
                                                   @BusinessSettlor navigator: Navigator,
                                                   actions: Actions,
                                                   requireName: NameRequiredActionProvider,
                                                   formProvider: YesNoFormProvider,
                                                   view: CountryOfResidenceYesNoView,
                                                   technicalErrorView: TechnicalErrorView
                                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Logging {

  private val form: Form[Boolean] = formProvider.withPrefix("settlorBusiness.5mld.countryOfResidenceYesNo")

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] =
    actions.authWithData(draftId).andThen(requireName(index, draftId)) {
      implicit request =>

        val preparedForm = request.userAnswers.get(CountryOfResidenceYesNoPage(index)) match {
          case None => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, draftId, index, request.businessName))
    }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] =
    actions.authWithData(draftId).andThen(requireName(index, draftId)).async {
      implicit request =>

        form.bindFromRequest().fold(
          formWithErrors =>
            Future.successful(BadRequest(view(formWithErrors, draftId, index, request.businessName))),
          value => {
            request.userAnswers.set(CountryOfResidenceYesNoPage(index), value) match {
              case Success(updatedAnswers) =>
                repository.set(updatedAnswers).map { _ =>
                  Redirect(navigator.nextPage(CountryOfResidenceYesNoPage(index), draftId)(updatedAnswers))
                }
              case Failure(_) => {
                logger.error("[CountryOfResidenceYesNoController][onSubmit] Error while storing user answers")
                Future.successful(InternalServerError(technicalErrorView()))
              }
            }
          }
        )
    }
}
