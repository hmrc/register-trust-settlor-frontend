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

package models.requests

import models.UserAnswers
import models.pages.FullName
import pages.living_settlor.individual.SettlorAliveYesNoPage
import play.api.mvc.WrappedRequest

case class SettlorIndividualNameRequest[T](request: RegistrationDataRequest[T], name: FullName)
    extends WrappedRequest[T](request) {
  val userAnswers: UserAnswers = request.userAnswers

  def settlorAliveAtRegistration(index: Int): Boolean =
    userAnswers.get(SettlorAliveYesNoPage(index)) match {
      case Some(value) => value
      case None        => false
    }
}
