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

package forms.behaviours

import forms.mappings.TelephoneNumber
import forms.{UtrFormProvider, Validation}
import org.scalacheck.Gen
import pages.living_settlor.business.SettlorBusinessUtrPage
import play.api.data.{Form, FormError}
import uk.gov.hmrc.domain.Nino
import utils.Constants.MAX
import wolfendale.scalacheck.regexp.RegexpGen

trait StringFieldBehaviours extends FieldBehaviours with OptionalFieldBehaviours {

  def fieldWithMinLength(form: Form[_], fieldName: String, minLength: Int, lengthError: FormError): Unit =
    s"not bind strings shorter than $minLength characters" in {

      val length = if (minLength > 0 && minLength < 2) minLength else minLength - 1

      forAll(stringsWithMaxLength(length) -> "shortString") { string =>
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors mustEqual Seq(lengthError)
      }
    }

  def fieldWithMaxLength(form: Form[_], fieldName: String, maxLength: Int, lengthError: FormError): Unit =
    s"not bind strings longer than $maxLength characters" in
      forAll(stringsLongerThan(maxLength) -> "longString") { string =>
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors mustEqual Seq(lengthError)
      }

  def fieldWithRegexpWithGenerator(
    form: Form[_],
    fieldName: String,
    regexp: String,
    generator: Gen[String],
    error: FormError
  ): Unit =
    s"not bind strings which do not match $regexp" in
      forAll(generator) { string =>
        whenever(!string.matches(regexp) && string.nonEmpty) {
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(error)
        }
      }

  def nonEmptyField(form: Form[_], fieldName: String, requiredError: FormError): Unit =
    "not bind spaces" in {

      val result = form.bind(Map(fieldName -> "    ")).apply(fieldName)
      result.errors mustBe Seq(requiredError)
    }

  def ninoField(form: Form[_], fieldName: String, requiredError: FormError): Unit =
    s"not bind strings which do not match valid nino format " in {
      val generator = RegexpGen.from(Validation.validNinoFormat)
      forAll(generator) { string =>
        whenever(!Nino.isValid(string)) {
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(requiredError)
        }
      }
    }

  def telephoneNumberField(form: Form[_], fieldName: String, requiredError: FormError): Unit =
    "not bind strings which do not match valid telephone number format" in {
      val generator = RegexpGen.from(Validation.telephoneRegex)
      forAll(generator) { string =>
        whenever(!TelephoneNumber.isValid(string)) {
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors mustEqual Seq(requiredError)
        }
      }
    }

  def utrField(
    form: UtrFormProvider,
    prefix: String,
    fieldName: String,
    length: Int,
    notUniqueError: FormError,
    sameAsTrustUtrError: FormError
  ): Unit = {

    val regex        = Validation.utrRegex.replace("*", s"{$length}")
    val utrGenerator = RegexpGen.from(regex)

    "not bind valid UTRs that have been used for other businesses" in {
      val intGenerator = Gen.choose(1, MAX)
      forAll(utrGenerator, intGenerator) { (utr, size) =>
        val updatedUserAnswers =
          (0 until size).foldLeft(emptyUserAnswers)((acc, i) => acc.set(SettlorBusinessUtrPage(i), utr).success.value)
        val result             = form.apply(prefix, updatedUserAnswers, size).bind(Map(fieldName -> utr)).apply(fieldName)
        result.errors mustEqual Seq(notUniqueError)
      }
    }

    "not bind valid UTR if it is the same as the trust UTR" in
      forAll(utrGenerator) { utr =>
        val result = form
          .apply(prefix, emptyUserAnswers.copy(existingTrustUtr = Some(utr)), 0)
          .bind(Map(fieldName -> utr))
          .apply(fieldName)
        result.errors mustEqual Seq(sameAsTrustUtrError)
      }

    "bind valid UTRs when no businesses" in
      forAll(utrGenerator) { utr =>
        val result = form.apply(prefix, emptyUserAnswers, 0).bind(Map(fieldName -> utr)).apply(fieldName)
        result.errors mustEqual Nil
        result.value.value mustBe utr
      }

    "bind valid UTRs when no other businesses have that UTR" in {
      val value: String      = "1234567890"
      val updatedUserAnswers = emptyUserAnswers.set(SettlorBusinessUtrPage(0), value).success.value
      forAll(utrGenerator.suchThat(_ != value)) { utr =>
        val result = form.apply(prefix, updatedUserAnswers, 0).bind(Map(fieldName -> utr)).apply(fieldName)
        result.errors mustEqual Nil
        result.value.value mustBe utr
      }
    }

    "bind valid UTR when business at current index has that UTR" in
      forAll(utrGenerator) { utr =>
        val updatedUserAnswers = emptyUserAnswers.set(SettlorBusinessUtrPage(0), utr).success.value
        val result             = form.apply(prefix, updatedUserAnswers, 0).bind(Map(fieldName -> utr)).apply(fieldName)
        result.errors mustEqual Nil
        result.value.value mustBe utr
      }
  }

}
