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

package models.pages

import models.pages.DeedOfVariation._
import org.scalacheck.Arbitrary.arbitrary
import org.scalatest.OptionValues
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import play.api.libs.json.{JsError, JsString, Json}

class DeedOfVariationSpec extends AnyWordSpec with Matchers with ScalaCheckPropertyChecks with OptionValues {

  "DeedOfVariation" must {

    "deserialise valid values" in {
      JsString("Replaced the will trust").validate[DeedOfVariation].asOpt.value mustEqual ReplacedWill
      JsString("Previously there was only an absolute interest under the will")
        .validate[DeedOfVariation]
        .asOpt
        .value mustEqual ReplaceAbsolute
    }

    "fail to deserialise invalid values" in {

      val gen = arbitrary[String] suchThat (!DeedOfVariation.values.map(_.toString).contains(_))

      forAll(gen) { invalidValue =>
        JsString(invalidValue).validate[DeedOfVariation] mustEqual JsError("error.invalid")
      }
    }

    "serialise" in {
      Json.toJson(ReplacedWill: DeedOfVariation)(DeedOfVariation.writes) mustEqual JsString("Replaced the will trust")
      Json.toJson(AdditionToWill: DeedOfVariation)(DeedOfVariation.writes) mustEqual JsString(
        "Addition to the will trust"
      )
      Json.toJson(ReplaceAbsolute: DeedOfVariation)(DeedOfVariation.writes) mustEqual JsString(
        "Previously there was only an absolute interest under the will"
      )
    }
  }

}
