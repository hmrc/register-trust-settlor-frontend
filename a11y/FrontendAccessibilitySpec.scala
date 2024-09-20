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

import forms._
import forms.deceased_settlor.{SettlorDateOfDeathFormProvider, SettlorIndividualOrBusinessFormProvider, SettlorsNameFormProvider}
import forms.living_settlor.SettlorBusinessTypeFormProvider
import models.{UserAnswers, YesNoDontKnow}
import models.pages.{AddASettlor, DeedOfVariation, FullName, IndividualOrBusiness, InternationalAddress, KindOfBusiness, KindOfTrust, PassportOrIdCardDetails, UKAddress}
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.api.data.Forms.{boolean, text}
import play.api.libs.json.Json
import play.api.mvc.RequestHeader
import play.twirl.api.Html
import uk.gov.hmrc.scalatestaccessibilitylinter.views.AutomaticAccessibilitySpec
import viewmodels.AnswerSection
import views.html._
import views.html.deceased_settlor._
import views.html.deceased_settlor.mld5.{CountryOfNationalityView => DeceasedCountryOfNationalityView, CountryOfResidenceInTheUkYesNoView => DeceasedCountryOfResidenceInTheUkYesNoView, CountryOfResidenceView => DeceasedCountryOfResidenceView, CountryOfResidenceYesNoView => DeceasedCountryOfResidenceYesNoView, _}
import views.html.errors._
import views.html.living_settlor._
import views.html.living_settlor.business._
import views.html.living_settlor.business.mld5.{CountryOfResidenceInTheUkYesNoView => BusinessCountryOfResidenceInTheUkYesNoView, CountryOfResidenceView => BusinessCountryOfResidenceView, CountryOfResidenceYesNoView => BusinessCountryOfResidenceYesNoView}
import views.html.living_settlor.individual._
import views.html.living_settlor.individual.mld5.{CountryOfNationalityView => IndividualCountryOfNationalityView, CountryOfNationalityYesNoView => IndividualCountryOfNationalityYesNoView, _}
import views.html.trust_type._

import java.time.LocalDate

class FrontendAccessibilitySpec extends AutomaticAccessibilitySpec {

  implicit val arbRequestHeader: Arbitrary[RequestHeader] = fixed(fakeRequest)
  implicit val arbSection: Arbitrary[Seq[viewmodels.Section]] = fixed(Seq(AnswerSection()))

  implicit val arbAddASettlorForm: Arbitrary[Form[AddASettlor]] =
    fixed(app.injector.instanceOf[AddASettlorFormProvider].apply())

  implicit val arbSettlorDateOfDeathForm: Arbitrary[Form[LocalDate]] =
    fixed(app.injector.instanceOf[SettlorDateOfDeathFormProvider].withConfig())

  implicit val arbInternationalAddressForm: Arbitrary[Form[InternationalAddress]] =
    fixed(app.injector.instanceOf[InternationalAddressFormProvider].apply())

  implicit val arbSettlorIndividualOrBusinessForm: Arbitrary[Form[IndividualOrBusiness]] =
    fixed(app.injector.instanceOf[SettlorIndividualOrBusinessFormProvider].apply())

  implicit val arbUkAddressForm: Arbitrary[Form[UKAddress]] =
    fixed(app.injector.instanceOf[UKAddressFormProvider].apply())

  implicit val arbKindOfBusinessForm: Arbitrary[Form[KindOfBusiness]] =
    fixed(app.injector.instanceOf[SettlorBusinessTypeFormProvider].apply())

  implicit val arbSettlorsNameForm: Arbitrary[Form[FullName]] =
    fixed(app.injector.instanceOf[SettlorsNameFormProvider].apply())

  implicit val arbYesNoDontKnowForm: Arbitrary[Form[YesNoDontKnow]] =
    fixed(app.injector.instanceOf[YesNoDontKnowFormProvider].withPrefix("123"))

  implicit val arbDeedOfVariationForm: Arbitrary[Form[DeedOfVariation]] =
    fixed(app.injector.instanceOf[DeedOfVariationFormProvider].apply())

  implicit val arbKindOfTrustForm: Arbitrary[Form[KindOfTrust]] =
    fixed(app.injector.instanceOf[KindOfTrustFormProvider].apply())

  implicit val arbPassportOrIdCardDetailsForm: Arbitrary[Form[PassportOrIdCardDetails]] =
    fixed(
      app.injector.instanceOf[PassportOrIdCardFormProvider].apply(
        prefix = "settlorIndividualPassport",
        userAnswers = UserAnswers("draftId", Json.obj(), internalAuthId = "internalId"),
        index = 1
      )
    )

  implicit val arbBooleanForm: Arbitrary[Form[Boolean]] = fixed(Form("value" -> boolean))
  implicit val arbStringForm: Arbitrary[Form[String]]   = fixed(Form("value" -> text))

  override def viewPackageName = "views.html"

  override def layoutClasses: Seq[Class[MainTemplate]] = Seq(classOf[MainTemplate])

  // scalastyle:off cyclomatic.complexity method.length
  override def renderViewByClass: PartialFunction[Any, Html] = {
    case addASettlorView: AddASettlorView                                                     => render(addASettlorView)
    case addASettlorYesNoView: AddASettlorYesNoView                                           => render(addASettlorYesNoView)
    case errorTemplate: ErrorTemplate                                                         => render(errorTemplate)
    case pageNotFoundView: PageNotFoundView                                                   => render(pageNotFoundView)
    case removeSettlorYesNoView: RemoveSettlorYesNoView                                       => render(removeSettlorYesNoView)
    case sessionExpiredView: SessionExpiredView                                               => render(sessionExpiredView)
    case settlorInfoView: SettlorInfoView                                                     => render(settlorInfoView)
    case unauthorisedView: UnauthorisedView                                                   => render(unauthorisedView)
    case deceasedSettlorAnswerView: DeceasedSettlorAnswerView                                 => render(deceasedSettlorAnswerView)
    case settlorDateOfBirthYesNoView: SettlorDateOfBirthYesNoView                             => render(settlorDateOfBirthYesNoView)
    case settlorDateOfDeathView: SettlorDateOfDeathView                                       => render(settlorDateOfDeathView)
    case settlorDateOfDeathYesNoView: SettlorDateOfDeathYesNoView                             => render(settlorDateOfDeathYesNoView)
    case settlorNationalInsuranceNumberView: SettlorNationalInsuranceNumberView               => render(settlorNationalInsuranceNumberView)
    case settlorsDateOfBirthView: SettlorsDateOfBirthView                                     => render(settlorsDateOfBirthView)
    case settlorsInternationalAddressView: SettlorsInternationalAddressView                   => render(settlorsInternationalAddressView)
    case settlorsLastKnownAddressYesNoView: SettlorsLastKnownAddressYesNoView                 => render(settlorsLastKnownAddressYesNoView)
    case settlorsNINoYesNoView: SettlorsNINoYesNoView                                         => render(settlorsNINoYesNoView)
    case settlorsNameView: SettlorsNameView                                                   => render(settlorsNameView)
    case settlorsUKAddressView: SettlorsUKAddressView                                         => render(settlorsUKAddressView)
    case wasSettlorsAddressUKYesNoView: WasSettlorsAddressUKYesNoView                         => render(wasSettlorsAddressUKYesNoView)
    case countryOfResidenceInTheUkYesNoView: DeceasedCountryOfResidenceInTheUkYesNoView       => render(countryOfResidenceInTheUkYesNoView)
    case countryOfResidenceInTheUkYesNoView: BusinessCountryOfResidenceInTheUkYesNoView       => render(countryOfResidenceInTheUkYesNoView)
    case countryOfResidenceView: BusinessCountryOfResidenceView                               => render(countryOfResidenceView)
    case countryOfResidenceView: DeceasedCountryOfResidenceView                               => render(countryOfResidenceView)
    case countryOfResidenceYesNoView: DeceasedCountryOfResidenceYesNoView                     => render(countryOfResidenceYesNoView)
    case countryOfResidenceYesNoView: BusinessCountryOfResidenceYesNoView                     => render(countryOfResidenceYesNoView)
    case countryOfNationalityInTheUkYesNoView: CountryOfNationalityInTheUkYesNoView           => render(countryOfNationalityInTheUkYesNoView)
    case technicalErrorView: TechnicalErrorView                                               => render(technicalErrorView)
    case settlorAnswersView: SettlorAnswersView                                               => render(settlorAnswersView)
    case settlorIndividualOrBusinessView: SettlorIndividualOrBusinessView                     => render(settlorIndividualOrBusinessView)
    case settlorBusinessAddressInternationalView: SettlorBusinessAddressInternationalView     => render(settlorBusinessAddressInternationalView)
    case settlorBusinessAddressUKView: SettlorBusinessAddressUKView                           => render(settlorBusinessAddressUKView)
    case settlorBusinessAddressUKYesNoView: SettlorBusinessAddressUKYesNoView                 => render(settlorBusinessAddressUKYesNoView)
    case settlorBusinessAddressYesNoView: SettlorBusinessAddressYesNoView                     => render(settlorBusinessAddressYesNoView)
    case settlorBusinessNameView: SettlorBusinessNameView                                     => render(settlorBusinessNameView)
    case settlorBusinessTimeYesNoView: SettlorBusinessTimeYesNoView                           => render(settlorBusinessTimeYesNoView)
    case settlorBusinessTypeView: SettlorBusinessTypeView                                     => render(settlorBusinessTypeView)
    case settlorBusinessUtrView: SettlorBusinessUtrView                                       => render(settlorBusinessUtrView)
    case countryOfNationalityYesNoView: CountryOfNationalityYesNoView                         => render(countryOfNationalityYesNoView)
    case settlorBusinessUtrYesNoView: SettlorBusinessUtrYesNoView                             => render(settlorBusinessUtrYesNoView)
    case countryOfNationalityView: IndividualCountryOfNationalityView                         => render(countryOfNationalityView)
    case countryOfNationalityView: DeceasedCountryOfNationalityView                           => render(countryOfNationalityView)
    case countryOfNationalityYesNoView: IndividualCountryOfNationalityYesNoView               => render(countryOfNationalityYesNoView)
    case settlorAliveYesNoView: SettlorAliveYesNoView                                         => render(settlorAliveYesNoView)
    case settlorIndividualAddressInternationalView: SettlorIndividualAddressInternationalView => render(settlorIndividualAddressInternationalView)
    case settlorIndividualAddressUKView: SettlorIndividualAddressUKView                       => render(settlorIndividualAddressUKView)
    case settlorIndividualAddressUKYesNoView: SettlorIndividualAddressUKYesNoView             => render(settlorIndividualAddressUKYesNoView)
    case settlorIndividualAddressYesNoView: SettlorIndividualAddressYesNoView                 => render(settlorIndividualAddressYesNoView)
    case settlorIndividualDateOfBirthView: SettlorIndividualDateOfBirthView                   => render(settlorIndividualDateOfBirthView)
    case settlorIndividualDateOfBirthYesNoView: SettlorIndividualDateOfBirthYesNoView         => render(settlorIndividualDateOfBirthYesNoView)
    case settlorIndividualIDCardView: SettlorIndividualIDCardView                             => render(settlorIndividualIDCardView)
    case settlorIndividualIDCardYesNoView: SettlorIndividualIDCardYesNoView                   => render(settlorIndividualIDCardYesNoView)
    case settlorIndividualNINOView: SettlorIndividualNINOView                                 => render(settlorIndividualNINOView)
    case settlorIndividualNINOYesNoView: SettlorIndividualNINOYesNoView                       => render(settlorIndividualNINOYesNoView)
    case settlorIndividualNameView: SettlorIndividualNameView                                 => render(settlorIndividualNameView)
    case settlorIndividualPassportView: SettlorIndividualPassportView                         => render(settlorIndividualPassportView)
    case settlorIndividualPassportYesNoView: SettlorIndividualPassportYesNoView               => render(settlorIndividualPassportYesNoView)
    case countryOfResidencyView: CountryOfResidencyView                                       => render(countryOfResidencyView)
    case countryOfResidencyYesNoView: CountryOfResidencyYesNoView                             => render(countryOfResidencyYesNoView)
    case mentalCapacityYesNoView: MentalCapacityYesNoView                                     => render(mentalCapacityYesNoView)
    case ukCountryOfNationalityYesNoView: UkCountryOfNationalityYesNoView                     => render(ukCountryOfNationalityYesNoView)
    case ukCountryOfResidencyYesNoView: UkCountryOfResidencyYesNoView                         => render(ukCountryOfResidencyYesNoView)
    case additionToWillTrustYesNoView: AdditionToWillTrustYesNoView                           => render(additionToWillTrustYesNoView)
    case employerFinancedRbsStartDateView: EmployerFinancedRbsStartDateView                   => render(employerFinancedRbsStartDateView)
    case employerFinancedRbsYesNoView: EmployerFinancedRbsYesNoView                           => render(employerFinancedRbsYesNoView)
    case holdoverReliefYesNoView: HoldoverReliefYesNoView                                     => render(holdoverReliefYesNoView)
    case howDeedOfVariationCreatedView: HowDeedOfVariationCreatedView                         => render(howDeedOfVariationCreatedView)
    case kindOfTrustView: KindOfTrustView                                                     => render(kindOfTrustView)
    case setUpByLivingSettlorView: SetUpByLivingSettlorView                                   => render(setUpByLivingSettlorView)
  }

  runAccessibilityTests()
}
