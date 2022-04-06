/*
 * Copyright 2022 HM Revenue & Customs
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

package viewmodels

import base.SpecBase
import models.pages.IndividualOrBusiness._
import models.pages.Status._
import play.api.libs.json.{JsSuccess, Json}

class SettlorViewModelSpec extends SpecBase {

  "SettlorViewModel" must {

    "validate JSON" when {

      "living settlor" when {

        "individual" when {

          "in progress" in {

            val json = Json.parse(
              """
                |{
                | "individualOrBusiness": "individual"
                |}
            """.stripMargin)

            json.validate[SettlorViewModel] mustEqual JsSuccess(
              SettlorIndividualViewModel(`type` = Individual, name = None, status = InProgress)
            )
          }

          "completed" in {

            val json = Json.parse(
              """
                |{
                | "individualOrBusiness": "individual",
                | "name": {
                |   "firstName": "Joe",
                |   "lastName": "Bloggs"
                | },
                | "dateOfBirthYesNo": false,
                | "ninoYesNo": true,
                | "nino": "NH111111A",
                | "status": "completed"
                |}
            """.stripMargin)

            json.validate[SettlorViewModel] mustEqual JsSuccess(
              SettlorIndividualViewModel(`type` = Individual, name = Some("Joe Bloggs"), status = Completed)
            )
          }
        }

        "business" when {

          "in progress" in {

            val json = Json.parse(
              """
                |{
                | "individualOrBusiness": "business"
                |}
            """.stripMargin)

            json.validate[SettlorViewModel] mustEqual JsSuccess(
              SettlorBusinessViewModel(`type` = Business, name = None, status = InProgress)
            )
          }

          "completed" in {

            val json = Json.parse(
              """
                |{
                | "individualOrBusiness": "business",
                | "businessName": "Business Ltd.",
                | "utrYesNo": true,
                | "utr": "1234567890",
                | "status": "completed"
                |}
            """.stripMargin)

            json.validate[SettlorViewModel] mustEqual JsSuccess(
              SettlorBusinessViewModel(`type` = Business, name = Some("Business Ltd."), status = Completed)
            )
          }
        }
      }

      "deceased settlor" when {

        "in progress" in {

          val json = Json.parse(
            """
              |{
              | "name": {
              |   "firstName": "Joe",
              |   "lastName": "Bloggs"
              | }
              |}
            """.stripMargin)

          json.validate[SettlorViewModel] mustEqual JsSuccess(
            SettlorDeceasedViewModel(`type` = Individual, name = "Joe Bloggs", status = InProgress)
          )
        }

        "completed" in {

          val json = Json.parse(
            """
              |{
              | "name": {
              |   "firstName": "Joe",
              |   "lastName": "Bloggs"
              | },
              | "dateOfDeathYesNo": false,
              | "dateOfBirthYesNo": false,
              | "ninoYesNo": true,
              | "nationalInsuranceNumber": "NH111111A",
              | "status": "completed"
              |}
            """.stripMargin)

          json.validate[SettlorViewModel] mustEqual JsSuccess(
            SettlorDeceasedViewModel(`type` = Individual, name = "Joe Bloggs", status = Completed)
          )
        }
      }
    }
  }
}
