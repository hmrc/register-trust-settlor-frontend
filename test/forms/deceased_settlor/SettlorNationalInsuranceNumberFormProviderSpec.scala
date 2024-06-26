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

package forms.deceased_settlor

import forms.Validation
import forms.behaviours.StringFieldBehaviours
import play.api.data.{Form, FormError}
import wolfendale.scalacheck.regexp.RegexpGen

class SettlorNationalInsuranceNumberFormProviderSpec extends StringFieldBehaviours {

  val requiredKey              = "settlorNationalInsuranceNumber.error.required"
  val invalidFormatKey         = "settlorNationalInsuranceNumber.error.invalid"
  val existingTrusteeNinos     = Seq("")
  val existingBeneficiaryNinos = Seq("")
  val existingProtectorNinos   = Seq("")

  val form: Form[String] = new SettlorNationalInsuranceNumberFormProvider()(
    existingTrusteeNinos,
    existingBeneficiaryNinos,
    existingProtectorNinos
  )

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      RegexpGen.from(Validation.ninoRegex)
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like nonEmptyField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey, Seq(fieldName))
    )

    behave like ninoField(
      form,
      fieldName,
      requiredError = FormError(fieldName, invalidFormatKey, Seq(fieldName))
    )

  }
}
