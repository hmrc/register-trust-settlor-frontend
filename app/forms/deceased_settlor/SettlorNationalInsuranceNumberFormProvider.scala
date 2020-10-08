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

package forms.deceased_settlor

import forms.mappings.Mappings
import javax.inject.Inject
import play.api.data.Form

class SettlorNationalInsuranceNumberFormProvider @Inject() extends Mappings {

  def apply(): Form[String] =
    Form("value" -> nino("settlorNationalInsuranceNumber.error.required")
      .verifying(
        firstError(
          isNotEmpty("value", "settlorNationalInsuranceNumber.error.required"),
          isNinoValid("value", "settlorNationalInsuranceNumber.error.invalid")
    )))
}
