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

package controllers.actions.deceased_settlor

import models.requests.{RegistrationDataRequest, SettlorIndividualNameRequest}
import pages.deceased_settlor.SettlorsNamePage
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class NameRequiredAction(draftId: String)(implicit val executionContext: ExecutionContext)
  extends ActionRefiner[RegistrationDataRequest, SettlorIndividualNameRequest] {

      override protected def refine[A](request: RegistrationDataRequest[A]): Future[Either[Result, SettlorIndividualNameRequest[A]]] = {

        Future.successful(
          request.userAnswers.get(SettlorsNamePage) match {
            case None =>
              Left(Redirect(controllers.deceased_settlor.routes.SettlorsNameController.onPageLoad(draftId)))
            case Some(name) =>
              Right(SettlorIndividualNameRequest(request, name))
          }
        )
      }
    }

class NameRequiredActionProvider @Inject()(implicit val executionContext: ExecutionContext) {
  def apply(draftId: String) = new NameRequiredAction(draftId)
}
