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

package models

import play.api.Logging
import play.api.http.Status
import play.api.libs.json._
import uk.gov.hmrc.http.{HttpReads, HttpResponse}

object RolesInCompanies extends Logging {

  final case class RoleInCompany(roleInCompany: models.pages.RoleInCompany)

  implicit val formats: Format[RoleInCompany] = Json.format[RoleInCompany]

  implicit val readSeq: Reads[Seq[RoleInCompany]] = Reads.seq(formats)

  sealed trait RolesInCompaniesAnswered
  final case object AllRolesAnswered extends RolesInCompaniesAnswered
  final case object NotAllRolesAnswered extends RolesInCompaniesAnswered
  final case object NoIndividualBeneficiaries extends RolesInCompaniesAnswered
  final case object CouldNotDetermine extends RolesInCompaniesAnswered

  implicit lazy val httpReads: HttpReads[RolesInCompaniesAnswered] =
    (_: String, _: String, response: HttpResponse) => {
      response.status match {
        case Status.OK =>
          if (response.json.\\("individualBeneficiaries").nonEmpty) {
            val jsonReads =
              (__ \\ Symbol("individualBeneficiaries"))
                .read[Seq[RoleInCompany]]

            response.json.validate[Seq[RoleInCompany]](jsonReads) match {
              case JsSuccess(_, _) => AllRolesAnswered
              case JsError(_)      => NotAllRolesAnswered
            }
          } else {
            NoIndividualBeneficiaries
          }
        case _         =>
          CouldNotDetermine
      }
    }

}
