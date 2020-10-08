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

import java.time.LocalDate

import config.FrontendAppConfig
import config.annotations.DeceasedSettlor
import controllers.actions._
import controllers.actions.deceased_settlor.NameRequiredActionProvider
import forms.deceased_settlor.SettlorDateOfDeathFormProvider
import javax.inject.Inject
import models.requests.{RegistrationDataRequest, SettlorIndividualNameRequest}
import models.{Mode, NormalMode}
import navigation.Navigator
import pages.deceased_settlor.{SettlorDateOfDeathPage, SettlorsDateOfBirthPage, SettlorsNamePage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.deceased_settlor.SettlorDateOfDeathView

import scala.concurrent.{ExecutionContext, Future}

class SettlorDateOfDeathController @Inject()(
                                              override val messagesApi: MessagesApi,
                                              registrationsRepository: RegistrationsRepository,
                                              @DeceasedSettlor navigator: Navigator,
                                              actions: Actions,
                                              requireName: NameRequiredActionProvider,
                                              formProvider: SettlorDateOfDeathFormProvider,
                                              val controllerComponents: MessagesControllerComponents,
                                              view: SettlorDateOfDeathView,
                                              appConfig: FrontendAppConfig
                                            )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def form(maxDate: (LocalDate, String), minDate: (LocalDate, String)): Form[LocalDate] =
    formProvider.withConfig(maxDate, minDate)

  def onPageLoad(mode: Mode, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(draftId)).async {
    implicit request =>

      val name = request.userAnswers.get(SettlorsNamePage).get

      getMaxDate(draftId).map { maxDate =>
        val preparedForm = request.userAnswers.get(SettlorDateOfDeathPage) match {
          case None => form(maxDate, minDate)
          case Some(value) => form(maxDate, minDate).fill(value)
        }

        Ok(view(preparedForm, mode, draftId, name))
      }
  }

  def onSubmit(mode: Mode, draftId: String): Action[AnyContent] = (actions.authWithData(draftId) andThen requireName(draftId)).async {
    implicit request =>

      val name = request.userAnswers.get(SettlorsNamePage).get

      getMaxDate(draftId).flatMap { maxDate =>
        form(maxDate, minDate).bindFromRequest().fold(
          (formWithErrors: Form[_]) =>
            Future.successful(BadRequest(view(formWithErrors, mode, draftId, name))),

          value => {
            for {
              updatedAnswers <- Future.fromTry(request.userAnswers.set(SettlorDateOfDeathPage, value))
              _ <- registrationsRepository.set(updatedAnswers)
            } yield Redirect(navigator.nextPage(SettlorDateOfDeathPage, mode, draftId)(updatedAnswers))
          }
        )
      }
  }

  private def minDate(implicit request: SettlorIndividualNameRequest[AnyContent]): (LocalDate, String) = {
    request.userAnswers.get(SettlorsDateOfBirthPage) match {
      case Some(dateOfBirth) =>
        (dateOfBirth, "beforeDateOfBirth")
      case None =>
        (appConfig.minDate, "past")
    }
  }

  private def getMaxDate(draftId: String)(implicit request: SettlorIndividualNameRequest[AnyContent]): Future[(LocalDate, String)] = {
    registrationsRepository.getTrustSetupDate(draftId).map {
      case Some(startDate) =>
        (startDate, "afterTrustStartDate")
      case None =>
        (LocalDate.now, "future")
    }
  }
}
