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

package mapping.reads

import models.YesNoDontKnow
import models.pages.Address
import play.api.libs.json.{JsSuccess, Reads, __}

trait SettlorReads {

  def readAddress(): Reads[Option[Address]] =
    (__ \ "ukAddress").read[Address].map(Some(_): Option[Address]) orElse
      (__ \ "internationalAddress").read[Address].map(Some(_): Option[Address]) orElse
      Reads(_ => JsSuccess(None: Option[Address]))

  def readMentalCapacity: Reads[Option[YesNoDontKnow]] =
    (__ \ Symbol("mentalCapacityYesNo"))
      .readNullable[Boolean]
      .flatMap[Option[YesNoDontKnow]] { x: Option[Boolean] =>
        Reads(_ => JsSuccess(YesNoDontKnow.fromBoolean(x)))
      }
      .orElse {
        (__ \ Symbol("mentalCapacityYesNo")).readNullable[YesNoDontKnow]
      }
}
