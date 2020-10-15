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

package controllers.actions

import javax.inject.Inject
import models.requests.{OptionalRegistrationDataRequest, RegistrationDataRequest}
import play.api.mvc.Results.Redirect
import play.api.mvc.{ActionRefiner, Result}

import scala.concurrent.{ExecutionContext, Future}

class RegistrationDataRequiredActionImpl @Inject()(implicit val executionContext: ExecutionContext) extends RegistrationDataRequiredAction {

  override protected def refine[A](request: OptionalRegistrationDataRequest[A]): Future[Either[Result, RegistrationDataRequest[A]]] = {

    request.userAnswers match {
      case None =>
        Future.successful(Left(Redirect(controllers.routes.SessionExpiredController.onPageLoad())))
      case Some(data) =>
        Future.successful(Right(RegistrationDataRequest(request.request, request.internalId, data, request.affinityGroup, request.enrolments, request.agentARN)))
    }
  }
}

trait RegistrationDataRequiredAction extends ActionRefiner[OptionalRegistrationDataRequest, RegistrationDataRequest]
