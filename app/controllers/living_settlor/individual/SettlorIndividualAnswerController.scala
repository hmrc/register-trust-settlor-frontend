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

package controllers.living_settlor.individual

import config.annotations.IndividualSettlor
import controllers.actions._
import controllers.living_settlor.routes.SettlorIndividualOrBusinessController
import javax.inject.Inject
import models.NormalMode
import models.pages.KindOfTrust.Employees
import models.pages.Status.Completed
import models.requests.RegistrationDataRequest
import navigation.Navigator
import pages.LivingSettlorStatus
import pages.living_settlor.SettlorIndividualOrBusinessPage
import pages.living_settlor.individual.SettlorIndividualAnswerPage
import pages.trust_type.KindOfTrustPage
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, ActionBuilder, AnyContent, MessagesControllerComponents}
import repositories.RegistrationsRepository
import services.DraftRegistrationService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import utils.CheckYourAnswersHelper
import utils.countryOptions.CountryOptions
import viewmodels.AnswerSection
import views.html.living_settlor.SettlorAnswersView

import scala.concurrent.{ExecutionContext, Future}

class SettlorIndividualAnswerController @Inject()(
                                                   override val messagesApi: MessagesApi,
                                                   registrationsRepository: RegistrationsRepository,
                                                   draftRegistrationService: DraftRegistrationService,
                                                   @IndividualSettlor navigator: Navigator,
                                                   standardActions: Actions,
                                                   requiredAnswer: RequiredAnswerActionProvider,
                                                   view: SettlorAnswersView,
                                                   countryOptions: CountryOptions,
                                                   val controllerComponents: MessagesControllerComponents
                                                 )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  private def actions(index: Int, draftId: String): ActionBuilder[RegistrationDataRequest, AnyContent] =
    standardActions.authWithData(draftId) andThen
      requiredAnswer(RequiredAnswer(SettlorIndividualOrBusinessPage(index), SettlorIndividualOrBusinessController.onPageLoad(NormalMode, index, draftId)))

  def onPageLoad(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId) {
    implicit request =>

      val answers = new CheckYourAnswersHelper(countryOptions)(request.userAnswers, draftId, canEdit = true)

      val sections = Seq(
        AnswerSection(
          None,
          answers.settlorIndividualQuestions(index)
        )
      )

      Ok(view(routes.SettlorIndividualAnswerController.onSubmit(index, draftId), sections))
  }

  def onSubmit(index: Int, draftId: String): Action[AnyContent] = actions(index, draftId).async {
    implicit request =>

      val answers = request.userAnswers.set(LivingSettlorStatus(index), Completed)

      Future.fromTry(answers) flatMap { updatedAnswers =>

        for {
          _ <- {
            if (updatedAnswers.get(KindOfTrustPage).contains(Employees)) {
              draftRegistrationService.setBeneficiaryStatus(draftId)
            } else {
              draftRegistrationService.removeRoleInCompanyAnswers(draftId)
            }
          }
          _ <- registrationsRepository.set(updatedAnswers)
        } yield Redirect(navigator.nextPage(SettlorIndividualAnswerPage, NormalMode, draftId)(updatedAnswers))
      }
  }
}
