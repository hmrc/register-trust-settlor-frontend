/*
 * Copyright 2021 HM Revenue & Customs
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

package utils

import models.pages._
import org.joda.time.{LocalDate => JodaDate}
import play.api.i18n.Messages
import play.twirl.api.{Html, HtmlFormat}
import uk.gov.hmrc.domain.Nino
import uk.gov.hmrc.play.language.LanguageUtils
import utils.countryOptions.CountryOptions

import java.time.{LocalDate => JavaDate}
import javax.inject.Inject

class CheckAnswersFormatters @Inject()(languageUtils: LanguageUtils) {

  def formatDate(date: JavaDate)(implicit messages: Messages): String = {
    val convertedDate: JodaDate = new JodaDate(date.getYear, date.getMonthValue, date.getDayOfMonth)
    languageUtils.Dates.formatDate(convertedDate)
  }

  def utr(answer: String): Html = {
    escape(answer)
  }

  def yesOrNo(answer: Boolean)(implicit messages: Messages): Html = {
    if (answer) {
      escape(messages("site.yes"))
    } else {
      escape(messages("site.no"))
    }
  }

  def formatNino(nino: String): String = Nino(nino).formatted

  def country(code: String, countryOptions: CountryOptions)(implicit messages: Messages): String =
    countryOptions.options.find(_.value.equals(code)).map(_.label).getOrElse("")

  def answer[T](key: String, answer: T)(implicit messages: Messages): Html =
    escape(messages(s"$key.$answer"))

  def escape(x: String): Html = HtmlFormat.escape(x)

  private def ukAddress(address: UKAddress): Html = {
    val lines =
      Seq(
        Some(escape(address.line1)),
        Some(escape(address.line2)),
        address.line3.map(escape),
        address.line4.map(escape),
        Some(escape(address.postcode))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  private def internationalAddress(address: InternationalAddress, countryOptions: CountryOptions)(implicit messages: Messages): Html = {
    val lines =
      Seq(
        Some(escape(address.line1)),
        Some(escape(address.line2)),
        address.line3.map(escape),
        Some(country(address.country, countryOptions))
      ).flatten

    Html(lines.mkString("<br />"))
  }

  def addressFormatter(address: Address, countryOptions: CountryOptions)(implicit messages: Messages): Html = {
    address match {
      case a:UKAddress => ukAddress(a)
      case a:InternationalAddress => internationalAddress(a, countryOptions)
    }
  }

  def passportOrIDCard(passportOrIdCard: PassportOrIdCardDetails, countryOptions: CountryOptions)(implicit messages: Messages): Html = {
    val lines =
      Seq(
        Some(country(passportOrIdCard.country, countryOptions)),
        Some(escape(passportOrIdCard.cardNumber)),
        Some(escape(formatDate(passportOrIdCard.expiryDate)))
      ).flatten

    Html(lines.mkString("<br />"))
  }

}
