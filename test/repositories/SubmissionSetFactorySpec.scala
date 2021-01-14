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

package repositories

import base.SpecBase
import mapping.{DeceasedSettlorMapper, Settlor, SettlorCompany, Settlors, SettlorsMapper, TrustDetailsMapper, TrustDetailsType, TypeOfTrust, WillType}
import models.RegistrationSubmission.{AnswerRow, AnswerSection, DataSet, MappedPiece}
import models.UserAnswers
import models.pages.Status._
import models.pages.{FullName, IndividualOrBusiness, KindOfTrust, Status}
import org.mockito.Matchers.any
import org.mockito.Mockito.when
import pages.living_settlor.{SettlorIndividualOrBusinessPage, business => businessPages, individual => individualPages}
import pages.trust_type.KindOfTrustPage
import pages.{DeceasedSettlorStatus, RegistrationProgress, deceased_settlor => deceasedPages, trust_type => trustTypePages}
import play.api.libs.json.Json
import utils.CheckAnswersFormatters
import utils.countryOptions.CountryOptions

class SubmissionSetFactorySpec extends SpecBase {

  private val factory: SubmissionSetFactory = injector.instanceOf[SubmissionSetFactory]

  private val name: FullName = FullName("Joe", Some("Joseph"), "Bloggs")
  private val businessName: String = "Business Ltd."

  private val checkAnswersFormatters = injector.instanceOf[CheckAnswersFormatters]

  "Submission set factory" when {

    ".answerSectionsIfCompleted" must {

      "return no answer sections if no completed beneficiaries" in {

        val result = factory.answerSectionsIfCompleted(emptyUserAnswers, Some(InProgress))

        result mustBe Nil
      }

      "return completed answer sections" when {

        "deceased settlor" in {

          val userAnswers = emptyUserAnswers
            .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, true).success.value
            .set(deceasedPages.SettlorsNamePage, name).success.value
            .set(deceasedPages.SettlorDateOfDeathYesNoPage, false).success.value
            .set(deceasedPages.SettlorDateOfBirthYesNoPage, false).success.value
            .set(deceasedPages.SettlorsNationalInsuranceYesNoPage, false).success.value
            .set(deceasedPages.SettlorsLastKnownAddressYesNoPage, false).success.value
            .set(DeceasedSettlorStatus, Completed).success.value

          val result = factory.answerSectionsIfCompleted(userAnswers, Some(Completed))

          result mustBe List(
            AnswerSection(
              None,
              Seq(
                AnswerRow("setUpAfterSettlorDied.checkYourAnswersLabel", "Yes", ""),
                AnswerRow("settlorsName.checkYourAnswersLabel", name.displayFullName, ""),
                AnswerRow("settlorDateOfDeathYesNo.checkYourAnswersLabel", "No", name.toString),
                AnswerRow("settlorDateOfBirthYesNo.checkYourAnswersLabel", "No", name.toString),
                AnswerRow("settlorsNationalInsuranceYesNo.checkYourAnswersLabel", "No", name.toString),
                AnswerRow("settlorsLastKnownAddressYesNo.checkYourAnswersLabel", "No", name.toString)
              ),
              Some(messages("answerPage.section.deceasedSettlor.heading"))
            )
          )
        }

        "living settlor" when {

          val baseAnswers = emptyUserAnswers
            .set(trustTypePages.SetUpAfterSettlorDiedYesNoPage, false).success.value
            .set(KindOfTrustPage, KindOfTrust.HeritageMaintenanceFund).success.value

          "individual settlor" in {

            val userAnswers = baseAnswers
              .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Individual).success.value
              .set(individualPages.SettlorIndividualNamePage(0), name).success.value
              .set(individualPages.SettlorIndividualDateOfBirthYesNoPage(0), false).success.value
              .set(individualPages.SettlorIndividualNINOYesNoPage(0), false).success.value
              .set(individualPages.SettlorAddressYesNoPage(0), false).success.value

            val result = factory.answerSectionsIfCompleted(userAnswers, Some(Completed))

            result mustBe List(
              AnswerSection(
                Some(messages("answerPage.section.settlor.subheading", 1)),
                Seq(
                  AnswerRow("setUpAfterSettlorDied.checkYourAnswersLabel", "No", ""),
                  AnswerRow("kindOfTrust.checkYourAnswersLabel", "A trust for the repair of historic buildings", ""),
                  AnswerRow("settlorIndividualOrBusiness.checkYourAnswersLabel", "Individual", ""),
                  AnswerRow("settlorIndividualName.checkYourAnswersLabel", name.displayFullName, ""),
                  AnswerRow("settlorIndividualDateOfBirthYesNo.checkYourAnswersLabel", "No", name.toString),
                  AnswerRow("settlorIndividualNINOYesNo.checkYourAnswersLabel", "No", name.toString),
                  AnswerRow("settlorIndividualAddressYesNo.checkYourAnswersLabel", "No", name.toString)
                ),
                Some(messages("answerPage.section.settlors.heading"))
              )
            )
          }

          "business settlor" in {

            val userAnswers = baseAnswers
              .set(SettlorIndividualOrBusinessPage(0), IndividualOrBusiness.Business).success.value
              .set(businessPages.SettlorBusinessNamePage(0), businessName).success.value
              .set(businessPages.SettlorBusinessUtrYesNoPage(0), false).success.value
              .set(businessPages.SettlorBusinessAddressYesNoPage(0), false).success.value

            val result = factory.answerSectionsIfCompleted(userAnswers, Some(Completed))

            result mustBe List(
              AnswerSection(
                Some(messages("answerPage.section.settlor.subheading", 1)),
                Seq(
                  AnswerRow("setUpAfterSettlorDied.checkYourAnswersLabel", "No", ""),
                  AnswerRow("kindOfTrust.checkYourAnswersLabel", "A trust for the repair of historic buildings", ""),
                  AnswerRow("settlorIndividualOrBusiness.checkYourAnswersLabel", "Business", ""),
                  AnswerRow("settlorBusinessName.checkYourAnswersLabel", businessName, ""),
                  AnswerRow("settlorBusinessUtrYesNo.checkYourAnswersLabel", "No", businessName),
                  AnswerRow("settlorBusinessAddressYesNo.checkYourAnswersLabel", "No", businessName)
                ),
                Some(messages("answerPage.section.settlors.heading"))
              )
            )
          }
        }
      }
    }

    ".createFrom" must {

      "return no mapped data if there are no completed beneficiaries" in {

        val result = factory.answerSectionsIfCompleted(emptyUserAnswers, Some(InProgress))

        result mustBe Nil
      }

      "return mapped data if there are completed beneficiaries" when {

        val status: Status = Completed
        val trustDetails: TrustDetailsType = TrustDetailsType(TypeOfTrust.HeritageTrust, None, None, None)
        val deceasedSettlor: WillType = WillType(name, None, None, None, None, None)

        val arbitraryUserAnswers: UserAnswers = emptyUserAnswers

        val mockRegistrationProgress: RegistrationProgress = mock[RegistrationProgress]
        val mockSettlorsMapper: SettlorsMapper = mock[SettlorsMapper]
        val mockDeceasedSettlorMapper: DeceasedSettlorMapper = mock[DeceasedSettlorMapper]
        val mockTrustDetailsMapper: TrustDetailsMapper = mock[TrustDetailsMapper]
        val countryOptions: CountryOptions = injector.instanceOf[CountryOptions]

        when(mockRegistrationProgress.settlorsStatus(any())).thenReturn(Some(status))

        val factory = new SubmissionSetFactory(mockRegistrationProgress, checkAnswersFormatters, mockSettlorsMapper, countryOptions, mockDeceasedSettlorMapper, mockTrustDetailsMapper)

        "trust details" in {

          when(mockTrustDetailsMapper.build(any())).thenReturn(Some(trustDetails))
          when(mockSettlorsMapper.build(any())).thenReturn(None)
          when(mockDeceasedSettlorMapper.build(any())).thenReturn(None)

          val result = factory.createFrom(arbitraryUserAnswers)

          result mustBe DataSet(
            data = Json.toJson(arbitraryUserAnswers),
            status = Some(status),
            registrationPieces = List(
              MappedPiece(
                "trust/details/",
                Json.toJson(trustDetails)
              )
            ),
            answerSections = Nil
          )
        }

        "living settlor" when {

          "individual" in {

            val settlors: Settlors = Settlors(
              settlor = Some(List(
                Settlor(name, None, None, None, None, None)
              )),
              settlorCompany = None
            )

            when(mockTrustDetailsMapper.build(any())).thenReturn(None)
            when(mockSettlorsMapper.build(any())).thenReturn(Some(settlors))
            when(mockDeceasedSettlorMapper.build(any())).thenReturn(None)

            val result = factory.createFrom(arbitraryUserAnswers)

            result mustBe DataSet(
              data = Json.toJson(arbitraryUserAnswers),
              status = Some(status),
              registrationPieces = List(
                MappedPiece(
                  "trust/entities/settlors",
                  Json.toJson(settlors)
                )
              ),
              answerSections = Nil
            )
          }

          "business" in {

            val settlors: Settlors = Settlors(
              settlor = None,
              settlorCompany = Some(List(
                SettlorCompany(businessName, None, None, None, None)
              ))
            )

            when(mockTrustDetailsMapper.build(any())).thenReturn(None)
            when(mockSettlorsMapper.build(any())).thenReturn(Some(settlors))
            when(mockDeceasedSettlorMapper.build(any())).thenReturn(None)

            val result = factory.createFrom(arbitraryUserAnswers)

            result mustBe DataSet(
              data = Json.toJson(arbitraryUserAnswers),
              status = Some(status),
              registrationPieces = List(
                MappedPiece(
                  "trust/entities/settlors",
                  Json.toJson(settlors)
                )
              ),
              answerSections = Nil
            )
          }
        }

        "deceased settlor" in {

          when(mockTrustDetailsMapper.build(any())).thenReturn(None)
          when(mockSettlorsMapper.build(any())).thenReturn(None)
          when(mockDeceasedSettlorMapper.build(any())).thenReturn(Some(deceasedSettlor))

          val result = factory.createFrom(arbitraryUserAnswers)

          result mustBe DataSet(
            data = Json.toJson(arbitraryUserAnswers),
            status = Some(status),
            registrationPieces = List(
              MappedPiece(
                "trust/entities/deceased",
                Json.toJson(deceasedSettlor)
              )
            ),
            answerSections = Nil
          )
        }

        "trust details and settlor" in {

          when(mockTrustDetailsMapper.build(any())).thenReturn(Some(trustDetails))
          when(mockSettlorsMapper.build(any())).thenReturn(None)
          when(mockDeceasedSettlorMapper.build(any())).thenReturn(Some(deceasedSettlor))

          val result = factory.createFrom(arbitraryUserAnswers)

          result mustBe DataSet(
            data = Json.toJson(arbitraryUserAnswers),
            status = Some(status),
            registrationPieces = List(
              MappedPiece(
                "trust/details/",
                Json.toJson(trustDetails)
              ),
              MappedPiece(
                "trust/entities/deceased",
                Json.toJson(deceasedSettlor)
              )
            ),
            answerSections = Nil
          )
        }
      }
    }
  }
}
