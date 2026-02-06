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

package mapping

import mapping.reads.DeceasedSettlor
import models.pages.KindOfTrust
import models.{Enumerable, WithName}
import play.api.libs.functional.syntax._
import play.api.libs.json.{JsError, JsSuccess, Reads, __}

sealed trait TypeOfTrust

object TypeOfTrust extends Enumerable.Implicits {

  case object WillTrustOrIntestacyTrust extends WithName("Will Trust or Intestacy Trust") with TypeOfTrust

  case object IntervivosSettlementTrust extends WithName("Inter vivos Settlement") with TypeOfTrust

  case object FlatManagementTrust extends WithName("Flat Management Company or Sinking Fund") with TypeOfTrust

  case object HeritageTrust extends WithName("Heritage Maintenance Fund") with TypeOfTrust

  case object DeedOfVariation extends WithName("Deed of Variation Trust or Family Arrangement") with TypeOfTrust

  case object EmployeeRelated extends WithName("Employment Related") with TypeOfTrust

  val values: Set[TypeOfTrust] = Set(
    WillTrustOrIntestacyTrust,
    IntervivosSettlementTrust,
    HeritageTrust,
    FlatManagementTrust,
    DeedOfVariation,
    EmployeeRelated
  )

  implicit val enumerable: Enumerable[TypeOfTrust] =
    Enumerable(values.toSeq.map(v => v.toString -> v): _*)

  val uaReads: Reads[TypeOfTrust] = (
    (__ \ Symbol("living")).readWithDefault[List[mapping.reads.Settlor]](Nil) and
      (__ \ Symbol("deceased")).readNullable[DeceasedSettlor]
  ).tupled
    .flatMap {
      case (_ :: _, None) | (Nil, Some(_)) =>
        (__ \ Symbol("kindOfTrust")).read[TypeOfTrust](KindOfTrust.typeofTrustReads) orElse
          Reads(_ => JsSuccess(WillTrustOrIntestacyTrust))
      case _                               =>
        Reads(_ => JsError("User answers are in an invalid state."))
    }

}
