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

package controllers

import controllers.actions._
import forms.{AddASettlorFormProvider, YesNoFormProvider}
import models.Enumerable
import models.pages.AddASettlor
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.trust_type.KindOfTrustPage
import pages.{AddASettlorPage, AddASettlorYesNoPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, Messages, MessagesApi, MessagesProvider}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController
import utils.AddASettlorViewHelper
import views.html.{AddASettlorView, AddASettlorYesNoView}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class AddASettlorController @Inject()(
                                       override val messagesApi: MessagesApi,
                                       registrationsRepository: RegistrationsRepository,
                                       navigator: Navigator,
                                       standardActions: Actions,
                                       yesNoFormProvider: YesNoFormProvider,
                                       addAnotherFormProvider: AddASettlorFormProvider,
                                       val controllerComponents: MessagesControllerComponents,
                                       addAnotherView: AddASettlorView,
                                       yesNoView: AddASettlorYesNoView
                                     )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  private val addAnotherForm: Form[AddASettlor] = addAnotherFormProvider()
  private val yesNoForm: Form[Boolean] = yesNoFormProvider.withPrefix("addASettlorYesNo")

  private def actions(draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActions.authWithData(draftId)

  private def heading(count: Int)(implicit mp: MessagesProvider): String = {
    count match {
      case 0 => Messages("addASettlor.heading")
      case 1 => Messages("addASettlor.singular.heading")
      case size => Messages("addASettlor.count.heading", size)
    }
  }

  private def trustHintText(implicit request: RegistrationDataRequest[AnyContent]): Option[String] = {
    request.userAnswers.get(KindOfTrustPage) map { trust =>
      s"addASettlor.$trust"
    }
  }

  def onPageLoad(draftId: String): Action[AnyContent] = actions(draftId) {
    implicit request =>

      val settlors = new AddASettlorViewHelper(request.userAnswers, draftId).rows

      settlors.count match {
        case 0 =>
          Ok(yesNoView(yesNoForm, draftId, trustHintText))
        case count =>
          Ok(addAnotherView(addAnotherForm, draftId, settlors.inProgress, settlors.complete, heading(count), trustHintText))
      }
  }

  def submitOne(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      yesNoForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {
          Future.successful(BadRequest(yesNoView(formWithErrors, draftId, trustHintText)))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddASettlorYesNoPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddASettlorYesNoPage, draftId)(updatedAnswers))
        }
      )
  }

  def submitAnother(draftId: String): Action[AnyContent] = actions(draftId).async {
    implicit request =>

      addAnotherForm.bindFromRequest().fold(
        (formWithErrors: Form[_]) => {

          val settlors = new AddASettlorViewHelper(request.userAnswers, draftId).rows

          Future.successful(BadRequest(
            addAnotherView(
              formWithErrors,
              draftId,
              settlors.inProgress,
              settlors.complete,
              heading(settlors.count),
              trustHintText
            )
          ))
        },
        value => {
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(AddASettlorPage, value))
            _              <- registrationsRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(AddASettlorPage, draftId)(updatedAnswers))
        }
      )
  }
}
