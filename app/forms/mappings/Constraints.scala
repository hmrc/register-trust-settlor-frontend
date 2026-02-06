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

package forms.mappings

import forms.Validation
import models.UserAnswers
import pages.living_settlor.business.SettlorBusinessUtrPage
import pages.living_settlor.individual.{
  SettlorIndividualIDCardPage, SettlorIndividualNINOPage, SettlorIndividualPassportPage
}
import play.api.Logging
import play.api.data.validation.{Constraint, Invalid, Valid}
import play.api.libs.json.{JsArray, JsString, JsSuccess}
import sections.LivingSettlors
import uk.gov.hmrc.domain.Nino

import java.time.LocalDate
import scala.util.matching.Regex

trait Constraints extends Logging {

  protected def firstError[A](constraints: Constraint[A]*): Constraint[A] =
    Constraint { input =>
      constraints
        .map(_.apply(input))
        .find(_ != Valid)
        .getOrElse(Valid)
    }

  protected def minimumValue[A](minimum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint { input =>
      import ev._

      if (input >= minimum) {
        Valid
      } else {
        Invalid(errorKey, minimum)
      }
    }

  protected def maximumValue[A](maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint { input =>
      import ev._

      if (input <= maximum) {
        Valid
      } else {
        Invalid(errorKey, maximum)
      }
    }

  protected def inRange[A](minimum: A, maximum: A, errorKey: String)(implicit ev: Ordering[A]): Constraint[A] =
    Constraint { input =>
      import ev._

      if (input >= minimum && input <= maximum) {
        Valid
      } else {
        Invalid(errorKey, minimum, maximum)
      }
    }

  protected def regexp(regex: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.matches(regex) =>
        Valid
      case _                         =>
        Invalid(errorKey, regex)
    }

  protected def maxLength(maximum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length <= maximum =>
        Valid
      case _                            =>
        Invalid(errorKey, maximum)
    }

  protected def minLength(minimum: Int, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.length >= minimum =>
        Valid
      case _                            =>
        Invalid(errorKey, minimum)
    }

  protected def isNotEmpty(value: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if str.trim.nonEmpty =>
        Valid
      case _                        =>
        Invalid(errorKey, value)
    }

  protected def isNinoValid(value: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if Nino.isValid(str) =>
        Valid
      case _                        =>
        Invalid(errorKey, value)
    }

  protected def maxDate(maximum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isAfter(maximum) =>
        Invalid(errorKey, args: _*)
      case _                             =>
        Valid
    }

  protected def minDate(minimum: LocalDate, errorKey: String, args: Any*): Constraint[LocalDate] =
    Constraint {
      case date if date.isBefore(minimum) =>
        Invalid(errorKey, args: _*)
      case _                              =>
        Valid
    }

  protected def wholeNumber(errorKey: String): Constraint[String] = {

    val regex: Regex = Validation.decimalCheck.r

    Constraint {
      case regex(_*) => Valid
      case _         => Invalid(errorKey)
    }
  }

  protected def isTelephoneNumberValid(value: String, errorKey: String): Constraint[String] =
    Constraint {
      case str if TelephoneNumber.isValid(str) =>
        Valid
      case _                                   =>
        Invalid(errorKey, value)
    }

  protected def uniqueUtr(
    userAnswers: UserAnswers,
    index: Int,
    notUniqueKey: String,
    sameAsTrustUtrKey: String
  ): Constraint[String] =
    Constraint { utr =>
      if (userAnswers.existingTrustUtr.contains(utr)) {
        Invalid(sameAsTrustUtrKey)
      } else {
        userAnswers.data.transform(LivingSettlors.path.json.pick[JsArray]) match {
          case JsSuccess(settlors, _) =>
            val utrIsUnique = settlors.value.zipWithIndex.forall(settlor =>
              !((settlor._1 \\ SettlorBusinessUtrPage.key).contains(JsString(utr)) && settlor._2 != index)
            )

            if (utrIsUnique) {
              Valid
            } else {
              Invalid(notUniqueKey)
            }
          case _                      =>
            Valid
        }
      }
    }

  protected def uniqueNino(
    existingTrusteeNinos: collection.Seq[String],
    existingBeneficiaryNinos: collection.Seq[String],
    existingProtectorNinos: collection.Seq[String]
  ): Constraint[String] =
    Constraint { nino =>
      if (existingTrusteeNinos.contains(nino)) {
        Invalid("settlorIndividualNINO.error.uniqueTrustee")
      } else if (existingBeneficiaryNinos.contains(nino)) {
        Invalid("settlorIndividualNINO.error.uniqueBeneficiary")
      } else if (existingProtectorNinos.contains(nino)) {
        Invalid("settlorIndividualNINO.error.uniqueProtector")
      } else {
        Valid
      }
    }

  protected def isNinoDuplicated(userAnswers: UserAnswers, index: Int, errorKey: String): Constraint[String] =
    Constraint { nino =>
      userAnswers.data.transform(LivingSettlors.path.json.pick[JsArray]) match {
        case JsSuccess(settlors, _) =>
          val uniqueNino = settlors.value.zipWithIndex.forall(settlor =>
            !((settlor._1 \\ SettlorIndividualNINOPage.key).contains(JsString(nino)) && settlor._2 != index)
          )

          if (uniqueNino) {
            Valid
          } else {
            Invalid(errorKey)
          }
        case _                      =>
          Valid
      }
    }

  protected def isPassportNumberDuplicated(userAnswers: UserAnswers, index: Int, errorKey: String): Constraint[String] =
    Constraint { passportNumber =>
      userAnswers.data.transform(LivingSettlors.path.json.pick[JsArray]) match {
        case JsSuccess(settlors, _) =>
          val uniquePassportNumber = settlors.value.zipWithIndex.forall { settlor =>
            !((settlor._1 \ SettlorIndividualPassportPage.key \ SettlorIndividualPassportPage.passportNumberKey).toOption
              .fold(false)(_.as[String] == passportNumber) && settlor._2 != index)
          }

          if (uniquePassportNumber) {
            Valid
          } else {
            Invalid(errorKey)
          }
        case _                      =>
          Valid
      }
    }

  protected def isIDNumberDuplicated(userAnswers: UserAnswers, index: Int, errorKey: String): Constraint[String] =
    Constraint { idNumber =>
      userAnswers.data.transform(LivingSettlors.path.json.pick[JsArray]) match {
        case JsSuccess(settlors, _) =>
          val uniqueIDNumber = settlors.value.zipWithIndex.forall { settlor =>
            !((settlor._1 \ SettlorIndividualIDCardPage.key \ SettlorIndividualIDCardPage.idNumberKey).toOption
              .fold(false)(_.as[String].equals(idNumber)) && settlor._2 != index)
          }

          if (uniqueIDNumber) {
            Valid
          } else {
            Invalid(errorKey)
          }
        case _                      =>
          Valid
      }
    }

}
