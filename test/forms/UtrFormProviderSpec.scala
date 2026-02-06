/*
 * Copyright 2026 HM Revenue & Customs
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

package forms

import forms.behaviours.StringFieldBehaviours
import play.api.data.{Form, FormError}

class UtrFormProviderSpec extends StringFieldBehaviours {

  val messageKeyPrefix  = "trusteeUtr"
  val requiredKey       = s"$messageKeyPrefix.error.required"
  val lengthKey         = s"$messageKeyPrefix.error.length"
  val notUniqueKey      = s"$messageKeyPrefix.error.notUnique"
  val sameAsTrustUtrKey = s"$messageKeyPrefix.error.sameAsTrustUtr"
  val utrLength         = 10

  val form: Form[String] = new UtrFormProvider().apply(messageKeyPrefix, emptyUserAnswers, 0)

  ".value" must {

    val fieldName = "value"

    behave like fieldThatBindsValidData(
      form = form,
      fieldName = fieldName,
      validDataGenerator = stringsWithMaxLength(utrLength)
    )

    behave like fieldWithMinLength(
      form = form,
      fieldName = fieldName,
      minLength = utrLength,
      lengthError = FormError(fieldName, lengthKey, Seq(utrLength))
    )

    behave like fieldWithMaxLength(
      form = form,
      fieldName = fieldName,
      maxLength = utrLength,
      lengthError = FormError(fieldName, lengthKey, Seq(utrLength))
    )

    behave like mandatoryField(
      form = form,
      fieldName = fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like utrField(
      form = new UtrFormProvider(),
      prefix = messageKeyPrefix,
      fieldName = fieldName,
      length = utrLength,
      notUniqueError = FormError(fieldName, notUniqueKey),
      sameAsTrustUtrError = FormError(fieldName, sameAsTrustUtrKey)
    )

  }

}
