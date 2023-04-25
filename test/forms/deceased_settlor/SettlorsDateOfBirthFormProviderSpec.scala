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

package forms.deceased_settlor

import base.FakeTrustsApp
import forms.behaviours.DateBehaviours
import play.api.data.FormError

import java.time.{LocalDate, ZoneOffset}

class SettlorsDateOfBirthFormProviderSpec extends DateBehaviours with FakeTrustsApp {

  private val min = LocalDate.of(1500, 1, 1)
  private val max = LocalDate.now(ZoneOffset.UTC)

  val form = new SettlorsDateOfBirthFormProvider(frontendAppConfig).withConfig()

  ".value" should {

    val validData = datesBetween(
      min = min,
      max = max
    )

    behave like dateField(form, "value", validData)

    behave like mandatoryDateField(form, "value", "settlorsDateOfBirth.error.required.all")

    behave like dateFieldWithMax(
      form,
      "value",
      max = max,
      FormError("value", s"settlorsDateOfBirth.error.future", List("day", "month", "year"))
    )

    behave like dateFieldWithMin(
      form,
      "value",
      min = min,
      FormError("value", s"settlorsDateOfBirth.error.past", List("day", "month", "year"))
    )

  }
}
