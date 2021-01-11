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

package models.pages

import models.{Enumerable, WithName}
import viewmodels.RadioOption

sealed trait IndividualOrBusiness

object IndividualOrBusiness extends Enumerable.Implicits {

  case object Individual extends WithName("individual") with IndividualOrBusiness
  case object Business extends WithName("business") with IndividualOrBusiness

  val values: Set[IndividualOrBusiness] = Set(
    Individual, Business
  )

  val options: Set[RadioOption] = values.map {
    value =>
      RadioOption("individualOrBusiness", value.toString)
  }

  implicit val enumerable: Enumerable[IndividualOrBusiness] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)
}
