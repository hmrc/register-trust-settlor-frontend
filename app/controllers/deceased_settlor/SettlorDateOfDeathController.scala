/*
 * Copyright 2024 HM Revenue & Customs
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

import config.FrontendAppConfig
import config.annotations.DeceasedSettlor
import controllers.actions._
import controllers.actions.deceased_settlor.NameRequiredActionProvider
import forms.deceased_settlor.SettlorDateOfDeathFormProvider
import models.requests.SettlorIndividualNameRequest
import navigation.Navigator
import pages.deceased_settlor.{SettlorDateOfDeathPage, SettlorsDateOfBirthPage, SettlorsNamePage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.deceased_settlor.SettlorDateOfDeathView
import views.html.errors.TechnicalErrorView

import java.time.LocalDate
import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class SettlorDateOfDeathController @Inject() (
  override val messagesApi: MessagesApi,
  registrationsRepository: RegistrationsRepository,
  @DeceasedSettlor navigator: Navigator,
  actions: Actions,
  requireName: NameRequiredActionProvider,
  formProvider: SettlorDateOfDeathFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: SettlorDateOfDeathView,
  appConfig: FrontendAppConfig,
  technicalErrorView: TechnicalErrorView
)(implicit ec: ExecutionContext)
    extends FrontendBaseController with I18nSupport with Logging {

  private def form(maxDate: (LocalDate, String), minDate: (LocalDate, String)): Form[LocalDate] =
    formProvider.withConfig(maxDate, minDate)

  def onPageLoad(draftId: String): Action[AnyContent] =
    (actions.authWithData(draftId) andThen requireName(draftId)).async { implicit request =>
      val name = request.userAnswers.get(SettlorsNamePage).get

      getMaxDate(draftId).map { maxDate =>
        val preparedForm = request.userAnswers.get(SettlorDateOfDeathPage) match {
          case None        => form(maxDate, minDate)
          case Some(value) => form(maxDate, minDate).fill(value)
        }

        Ok(view(preparedForm, draftId, name))
      }
    }

  def onSubmit(draftId: String): Action[AnyContent] =
    (actions.authWithData(draftId) andThen requireName(draftId)).async { implicit request =>
      val name = request.userAnswers.get(SettlorsNamePage).get

      getMaxDate(draftId).flatMap { maxDate =>
        form(maxDate, minDate)
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[LocalDate]) => Future.successful(BadRequest(view(formWithErrors, draftId, name))),
            value =>
              request.userAnswers.set(SettlorDateOfDeathPage, value) match {
                case Success(updatedAnswers) =>
                  registrationsRepository.set(updatedAnswers).map { _ =>
                    Redirect(navigator.nextPage(SettlorDateOfDeathPage, draftId)(updatedAnswers))
                  }
                case Failure(_)              =>
                  logger.error("[SettlorDateOfDeathController][onSubmit] Error while storing user answers")
                  Future.successful(InternalServerError(technicalErrorView()))
              }
          )
      }
    }

  private def minDate(implicit request: SettlorIndividualNameRequest[AnyContent]): (LocalDate, String) =
    request.userAnswers.get(SettlorsDateOfBirthPage) match {
      case Some(dateOfBirth) =>
        (dateOfBirth, "beforeDateOfBirth")
      case None              =>
        (appConfig.minDate, "past")
    }

  private def getMaxDate(
    draftId: String
  )(implicit request: SettlorIndividualNameRequest[AnyContent]): Future[(LocalDate, String)] =
    registrationsRepository.getTrustSetupDate(draftId).map {
      case Some(startDate) =>
        (startDate, "afterTrustStartDate")
      case None            =>
        (LocalDate.now, "future")
    }

}
