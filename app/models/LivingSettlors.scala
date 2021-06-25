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

package models

import models.pages.IndividualOrBusiness
import utils.Constants.MAX
import viewmodels.{RadioOption, SettlorBusinessViewModel, SettlorIndividualViewModel}

case class LivingSettlors(individuals: List[SettlorIndividualViewModel] = Nil,
                          businesses: List[SettlorBusinessViewModel] = Nil) {

  type SettlorOption = (Int, IndividualOrBusiness)
  type SettlorOptions = List[SettlorOption]

  private val options: SettlorOptions = List(
    (individuals.size, IndividualOrBusiness.Individual),
    (businesses.size, IndividualOrBusiness.Business)
  )

  val maxedOutOptions: List[RadioOption] = {
    options.filter(_._1 >= MAX).map {
      x => RadioOption(IndividualOrBusiness.prefix, x._2.toString)
    }
  }

}
