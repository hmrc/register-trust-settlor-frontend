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

package forms

import forms.helpers.WhitespaceHelper._
import forms.mappings.Mappings

import javax.inject.Inject
import models.pages.UKAddress
import play.api.data.Forms._
import play.api.data.Form

class UKAddressFormProvider @Inject() extends Mappings {

  def apply(): Form[UKAddress] = Form(
    mapping(
      "line1"    ->
        text("ukAddress.error.line1.required")
          .verifying(
            firstError(
              isNotEmpty("line1", "ukAddress.error.line1.required"),
              maxLength(35, "ukAddress.error.line1.length"),
              regexp(Validation.addressLineRegex, "ukAddress.error.line1.invalidCharacters")
            )
          ),
      "line2"    ->
        text("ukAddress.error.line2.required")
          .verifying(
            firstError(
              isNotEmpty("line2", "ukAddress.error.line2.required"),
              maxLength(35, "ukAddress.error.line2.length"),
              regexp(Validation.addressLineRegex, "ukAddress.error.line2.invalidCharacters")
            )
          ),
      "line3"    ->
        optional(
          text()
            .verifying(
              firstError(
                maxLength(35, "ukAddress.error.line3.length"),
                regexp(Validation.addressLineRegex, "ukAddress.error.line3.invalidCharacters")
              )
            )
        ).transform(emptyToNone, identity[Option[String]]),
      "line4"    ->
        optional(
          text()
            .verifying(
              firstError(
                maxLength(35, "ukAddress.error.line4.length"),
                regexp(Validation.addressLineRegex, "ukAddress.error.line4.invalidCharacters")
              )
            )
        ).transform(emptyToNone, identity[Option[String]]),
      "postcode" ->
        postcode("ukAddress.error.postcode.required")
          .verifying(
            firstError(
              isNotEmpty("postcode", "ukAddress.error.postcode.required"),
              regexp(Validation.postcodeRegex, "ukAddress.error.postcode.invalidCharacters")
            )
          )
    )(UKAddress.apply)(UKAddress.unapply)
  )
}
