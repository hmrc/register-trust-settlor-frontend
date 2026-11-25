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

package models

import mapping.TypeOfTrust
import models.pages.{DeedOfVariation, FullName}
import play.api.libs.functional.syntax.*
import play.api.libs.json.*

import java.time.LocalDate

case class Settlors(settlor: Option[List[Settlor]], settlorCompany: Option[List[SettlorCompany]])

object Settlors {
  given settlorsFormat: Format[Settlors] = Json.format[Settlors]
}

case class Settlor(
  aliveAtRegistration: Boolean,
  name: FullName,
  dateOfBirth: Option[LocalDate],
  identification: Option[IdentificationType],
  countryOfResidence: Option[String],
  nationality: Option[String],
  legallyIncapable: Option[Boolean]
)

object Settlor {
  given settlorFormat: Format[Settlor] = Json.format[Settlor]
}

case class SettlorCompany(
  name: String,
  companyType: Option[String],
  companyTime: Option[Boolean],
  identification: Option[IdentificationOrgType],
  countryOfResidence: Option[String]
)

object SettlorCompany {
  given settlorCompanyFormat: Format[SettlorCompany] = Json.format[SettlorCompany]
}

case class IdentificationOrgType(utr: Option[String], address: Option[AddressType])

object IdentificationOrgType {
  given trustBeneficiaryIdentificationFormat: Format[IdentificationOrgType] = Json.format[IdentificationOrgType]
}

case class Identification(nino: Option[String], address: Option[AddressType])

object Identification {
  given identificationFormat: Format[Identification] = Json.format[Identification]
}

case class IdentificationType(nino: Option[String], passport: Option[PassportType], address: Option[AddressType])

object IdentificationType {
  given identificationTypeFormat: Format[IdentificationType] = Json.format[IdentificationType]
}

case class TrustDetailsType(
  typeOfTrust: TypeOfTrust,
  deedOfVariation: Option[DeedOfVariation],
  interVivos: Option[Boolean],
  efrbsStartDate: Option[LocalDate]
)

object TrustDetailsType {

  given trustDetailsTypeFormat: Format[TrustDetailsType] = Json.format[TrustDetailsType]

  val uaReads: Reads[TrustDetailsType] = (
    TypeOfTrust.uaReads and
      DeedOfVariation.uaReads and
      (__ \ Symbol("holdoverReliefYesNo")).readNullable[Boolean] and
      (__ \ Symbol("efrbsStartDate")).readNullable[LocalDate]
  )(TrustDetailsType.apply)

}

case class PassportType(number: String, expirationDate: LocalDate, countryOfIssue: String)

object PassportType {
  given passportTypeFormat: Format[PassportType] = Json.format[PassportType]
}

case class AddressType(
  line1: String,
  line2: String,
  line3: Option[String],
  line4: Option[String],
  postCode: Option[String],
  country: String
)

object AddressType {
  given addressTypeFormat: Format[AddressType] = Json.format[AddressType]
}

case class WillType(
  name: FullName,
  dateOfBirth: Option[LocalDate],
  dateOfDeath: Option[LocalDate],
  identification: Option[Identification],
  countryOfResidence: Option[String],
  nationality: Option[String]
)

object WillType {
  given willTypeFormat: Format[WillType] = Json.format[WillType]
}
