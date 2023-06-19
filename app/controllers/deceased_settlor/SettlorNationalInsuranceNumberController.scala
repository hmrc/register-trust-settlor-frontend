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

package controllers.deceased_settlor

import config.annotations.DeceasedSettlor
import controllers.actions._
import controllers.actions.deceased_settlor.NameRequiredActionProvider
import forms.deceased_settlor.SettlorNationalInsuranceNumberFormProvider
import models.requests.SettlorIndividualNameRequest
import navigation.Navigator
import pages.deceased_settlor.{SettlorNationalInsuranceNumberPage, SettlorsNamePage}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.DraftRegistrationService
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import views.html.deceased_settlor.SettlorNationalInsuranceNumberView
import views.html.errors.TechnicalErrorView

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class SettlorNationalInsuranceNumberController @Inject() (
  override val messagesApi: MessagesApi,
  registrationsRepository: RegistrationsRepository,
  @DeceasedSettlor navigator: Navigator,
  actions: Actions,
  requireName: NameRequiredActionProvider,
  formProvider: SettlorNationalInsuranceNumberFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: SettlorNationalInsuranceNumberView,
  technicalErrorView: TechnicalErrorView,
  draftRegistrationService: DraftRegistrationService
)(implicit ec: ExecutionContext)
    extends FrontendBaseController
    with I18nSupport
    with Logging {

  private def getForm(draftId: String)(implicit request: SettlorIndividualNameRequest[AnyContent]) =
    for {
      existingTrusteeNinos     <- getTrusteeNinos(draftId)
      existingBeneficiaryNinos <- getBeneficiaryNinos(draftId)
      existingProtectorNinos   <- getProtectorNinos(draftId)

    } yield formProvider(existingTrusteeNinos, existingBeneficiaryNinos, existingProtectorNinos)

  private def getTrusteeNinos(draftId: String)(implicit
    request: SettlorIndividualNameRequest[AnyContent]
  ): Future[collection.IndexedSeq[String]] =
    draftRegistrationService.retrieveTrusteeNinos(draftId)

  private def getBeneficiaryNinos(draftId: String)(implicit
    request: SettlorIndividualNameRequest[AnyContent]
  ): Future[collection.IndexedSeq[String]] =
    draftRegistrationService.retrieveBeneficiaryNinos(draftId)
  private def getProtectorNinos(draftId: String)(implicit
    request: SettlorIndividualNameRequest[AnyContent]
  ): Future[collection.IndexedSeq[String]] =
    draftRegistrationService.retrieveProtectorNinos(draftId)

  def onPageLoad(draftId: String): Action[AnyContent] =
    (actions.authWithData(draftId) andThen requireName(draftId)).async { implicit request =>
      val name = request.userAnswers.get(SettlorsNamePage).get

      getForm(draftId).map { form =>
        val preparedForm = request.userAnswers.get(SettlorNationalInsuranceNumberPage) match {
          case None        => form
          case Some(value) => form.fill(value)
        }

        Ok(view(preparedForm, draftId, name))
      }
    }

  def onSubmit(draftId: String): Action[AnyContent] =
    (actions.authWithData(draftId) andThen requireName(draftId)).async { implicit request =>
      val name = request.userAnswers.get(SettlorsNamePage).get

      getForm(draftId).flatMap { form =>
        form
          .bindFromRequest()
          .fold(
            (formWithErrors: Form[_]) => Future.successful(BadRequest(view(formWithErrors, draftId, name))),
            value =>
              request.userAnswers.set(SettlorNationalInsuranceNumberPage, value) match {
                case Success(updatedAnswers) =>
                  registrationsRepository.set(updatedAnswers).map { _ =>
                    Redirect(navigator.nextPage(SettlorNationalInsuranceNumberPage, draftId)(updatedAnswers))
                  }
                case Failure(_)              =>
                  logger.error("[SettlorNationalInsuranceNumberController][onSubmit] Error while storing user answers")
                  Future.successful(InternalServerError(technicalErrorView()))
              }
          )
      }
    }
}
