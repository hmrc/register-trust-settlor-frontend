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

package config

import com.google.inject.{Inject, Singleton}
import controllers.routes
import play.api.Configuration
import play.api.i18n.Lang
import play.api.mvc.Call
import uk.gov.hmrc.hmrcfrontend.config.ContactFrontendConfig

import java.time.LocalDate

@Singleton
class FrontendAppConfig @Inject() (configuration: Configuration,
                                   contactFrontendConfig: ContactFrontendConfig) {

  final val ENGLISH = "en"
  final val WELSH = "cy"

  val repositoryKey: String = "settlors"

  val appName: String = configuration.get[String]("appName")
  val analyticsHost: String = configuration.get[String](s"google-analytics.host")

  val betaFeedbackUrl = s"${contactFrontendConfig.baseUrl.get}/contact/beta-feedback?service=${contactFrontendConfig.serviceId.get}"

  lazy val registrationStartUrl: String = configuration.get[String]("urls.registrationStart")
  lazy val authUrl: String = configuration.get[Service]("auth").baseUrl
  lazy val loginUrl: String = configuration.get[String]("urls.login")
  lazy val loginContinueUrl: String = configuration.get[String]("urls.loginContinue")
  lazy val logoutUrl: String = configuration.get[String]("urls.logout")

  lazy val logoutAudit: Boolean =
    configuration.get[Boolean]("microservice.services.features.auditing.logout")

  lazy val registrationProgressUrlTemplate: String = configuration.get[String]("urls.registrationProgress")
  def registrationProgressUrl(draftId: String): String = registrationProgressUrlTemplate.replace(":draftId", draftId)

  lazy val trustsUrl: String = configuration.get[Service]("microservice.services.trusts").baseUrl

  lazy val trustsStoreUrl: String = configuration.get[Service]("microservice.services.trusts-store").baseUrl

  lazy val maintainATrustFrontendUrl: String = configuration.get[String]("urls.maintainATrust")
  lazy val createAgentServicesAccountUrl: String = configuration.get[String]("urls.createAgentServicesAccount")

  lazy val locationCanonicalList: String = configuration.get[String]("location.canonical.list.all")
  lazy val locationCanonicalListCY: String = configuration.get[String]("location.canonical.list.allCY")

  lazy val countdownLength: Int = configuration.get[Int]("timeout.countdown")
  lazy val timeoutLength: Int = configuration.get[Int]("timeout.length")

  private val day: Int = configuration.get[Int]("minimumDate.day")
  private val month: Int = configuration.get[Int]("minimumDate.month")
  private val year: Int = configuration.get[Int]("minimumDate.year")
  lazy val minDate: LocalDate = LocalDate.of(year, month, day)

  lazy val languageTranslationEnabled: Boolean =
    configuration.get[Boolean]("microservice.services.features.welsh-translation")

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang(ENGLISH),
    "cymraeg" -> Lang(WELSH)
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => routes.LanguageSwitchController.switchToLanguage(lang)

  def registerTrustAsTrusteeUrl: String = configuration.get[String]("urls.registerTrustAsTrustee")

  def countMaxAsCombined: Boolean = configuration.get[Boolean]("microservice.services.features.count-max-as-combined")
}
