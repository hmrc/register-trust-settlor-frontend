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

package controllers

import config.FrontendAppConfig
import controllers.actions._
import forms.{AddASettlorFormProvider, YesNoFormProvider}
import models.TaskStatus.TaskStatus
import models.pages.AddASettlor
import models.pages.AddASettlor.NoComplete
import models.pages.Status.Completed
import models.requests.RegistrationDataRequest
import models.{Enumerable, TaskStatus, UserAnswers}
import navigation.Navigator
import pages.trust_type.KindOfTrustPage
import pages.{AddASettlorPage, AddASettlorYesNoPage, RegistrationProgress}
import play.api.Logging
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc._
import repositories.RegistrationsRepository
import services.TrustsStoreService
import uk.gov.hmrc.http.HttpVerbs.GET
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.AddASettlorViewHelper
import views.html.errors.TechnicalErrorView
import views.html.{AddASettlorView, AddASettlorYesNoView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class AddASettlorController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: RegistrationsRepository,
                                       navigator: Navigator,
                                       standardActions: Actions,
                                       yesNoFormProvider: YesNoFormProvider,
                                       addAnotherFormProvider: AddASettlorFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       addAnotherView: AddASettlorView,
                                       yesNoView: AddASettlorYesNoView,
                                       trustsStoreService: TrustsStoreService,
                                       registrationProgress: RegistrationProgress,
                                       technicalErrorView: TechnicalErrorView
                                     )(implicit ec: ExecutionContext, config: FrontendAppConfig)
  extends FrontendBaseController with I18nSupport with Enumerable.Implicits with Logging {

  private val addAnotherForm: Form[AddASettlor] = addAnotherFormProvider()
  private val yesNoForm: Form[Boolean] = yesNoFormProvider.withPrefix("addASettlorYesNo")

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActions.authWithData(draftId)

  private def heading(count: Int)(implicit mp: MessagesProvider): String = {
    count match {
      case x if x <= 1 => Messages("addASettlor.heading")
      case _ => Messages("addASettlor.count.heading", count)
    }
  }

  private def trustHintText(implicit request: RegistrationDataRequest[AnyContent]): Option[String] = {
    request.userAnswers.get(KindOfTrustPage) map { trust =>
      s"addASettlor.$trust"
    }
  }

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val rows = new AddASettlorViewHelper(request.userAnswers, draftId).rows

      rows.count match {
        case 0 =>
          Ok(yesNoView(yesNoForm, draftId, trustHintText))
        case count =>
          val maxedOut = request.userAnswers.settlors.maxedOutOptions.map(_.messageKey)
          Ok(addAnotherView(
            form = addAnotherForm,
            draftId = draftId,
            inProgress = rows.inProgress,
            complete = rows.complete,
            heading = heading(count),
            hint = trustHintText,
            maxedOut = maxedOut
          ))
      }
  }

  def submitOne(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(BadRequest(yesNoView(formWithErrors, draftId, trustHintText)))
        },
        value => {
          request.userAnswers.set(AddASettlorYesNoPage, value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                setTaskStatus(draftId, TaskStatus.InProgress)
                Redirect(navigator.nextPage(AddASettlorYesNoPage, draftId)(updatedAnswers))
              }
            case Failure(_) => {
              logger.error("[AddASettlorController][submitAnother] Error while storing user answers")
              Future.successful(InternalServerError(technicalErrorView()))
            }
          }
        }
      )
  }

  def submitAnother(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      addAnotherForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {

          val rows = new AddASettlorViewHelper(request.userAnswers, draftId).rows
          val maxedOut = request.userAnswers.settlors.maxedOutOptions.map(_.messageKey)

          Future.successful(BadRequest(
            addAnotherView(
              form = formWithErrors,
              draftId = draftId,
              inProgress = rows.inProgress,
              complete = rows.complete,
              heading = heading(rows.count),
              hint = trustHintText,
              maxedOut = maxedOut
            )
          ))
        },
        value => {
          request.userAnswers.set(AddASettlorPage, value) match {
            case Success(updatedAnswers) =>
              registrationsRepository.set(updatedAnswers).map { _ =>
                setTaskStatus(updatedAnswers, draftId, value)
                Redirect(navigator.nextPage(AddASettlorPage, draftId)(updatedAnswers))
              }
            case Failure(_) => {
              logger.error("[AddASettlorController][submitAnother] Error while storing user answers")
              Future.successful(InternalServerError(technicalErrorView()))
            }
          }
        }
      )
  }

  def submitComplete(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      val status = NoComplete

        request.userAnswers.set(AddASettlorPage, status) match {
          case Success(updatedAnswers) =>
            registrationsRepository.set(updatedAnswers).map { _ =>
              setTaskStatus(updatedAnswers, draftId, status)
              Redirect(Call(GET, config.registrationProgressUrl(draftId)))
            }
          case Failure(_) => {
            logger.error("[AddASettlorController][submitComplete] Error while storing user answers")
            Future.successful(InternalServerError(technicalErrorView()))
          }
      }
  }

  private def setTaskStatus(userAnswers: UserAnswers, draftId: String, selection: AddASettlor)
                           (implicit hc: HeaderCarrier): Future[HttpResponse] = {

    val settlorStatus = registrationProgress.settlorsStatus(userAnswers)

    val status = (selection, settlorStatus) match {
      case (NoComplete, Some(Completed)) => TaskStatus.Completed
      case _ => TaskStatus.InProgress
    }

    setTaskStatus(draftId, status)
  }

  private def setTaskStatus(draftId: String, taskStatus: TaskStatus)
                           (implicit hc: HeaderCarrier): Future[HttpResponse] = {
    trustsStoreService.updateTaskStatus(draftId, taskStatus)
  }
}
