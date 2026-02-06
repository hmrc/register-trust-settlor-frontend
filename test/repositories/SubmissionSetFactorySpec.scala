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

package repositories

import base.SpecBase
import mapping._
import models.RegistrationSubmission.{DataSet, MappedPiece}
import models.pages.FullName
import models.{Settlor, SettlorCompany, Settlors, TrustDetailsType, UserAnswers, WillType}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import play.api.libs.json.Json
import utils.print.PrintHelpers

class SubmissionSetFactorySpec extends SpecBase {

  private val factory: SubmissionSetFactory = injector.instanceOf[SubmissionSetFactory]

  private val name: FullName       = FullName("Joe", Some("Joseph"), "Bloggs")
  private val businessName: String = "Business Ltd."

  "Submission set factory" when {

    ".createFrom" must {

      "return no mapped data if there are no completed settlors" in {

        val result = factory.answerSectionsIfCompleted(emptyUserAnswers)

        result mustBe Nil
      }

      "return mapped data if there are completed settlors" when {

        val trustDetails: TrustDetailsType =
          models.TrustDetailsType(TypeOfTrust.HeritageTrust, None, None, None)

        val deceasedSettlor: WillType = WillType(name, None, None, None, None, None)

        val arbitraryUserAnswers: UserAnswers = emptyUserAnswers

        val mockSettlorsMapper: SettlorsMapper               = mock[SettlorsMapper]
        val mockDeceasedSettlorMapper: DeceasedSettlorMapper = mock[DeceasedSettlorMapper]
        val mockTrustDetailsMapper: TrustDetailsMapper       = mock[TrustDetailsMapper]
        val printHelpers: PrintHelpers                       = injector.instanceOf[PrintHelpers]

        val factory = new SubmissionSetFactory(
          settlorsMapper = mockSettlorsMapper,
          deceasedSettlorMapper = mockDeceasedSettlorMapper,
          trustDetailsMapper = mockTrustDetailsMapper,
          printHelpers = printHelpers
        )

        "trust details" in {

          when(mockTrustDetailsMapper.build(any())).thenReturn(Some(trustDetails))
          when(mockSettlorsMapper.build(any())).thenReturn(None)
          when(mockDeceasedSettlorMapper.build(any())).thenReturn(None)

          val result = factory.createFrom(arbitraryUserAnswers)

          result mustBe DataSet(
            data = Json.toJson(arbitraryUserAnswers),
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
              settlor = Some(
                List(
                  Settlor(aliveAtRegistration = true, name, None, None, None, None, None)
                )
              ),
              settlorCompany = None
            )

            when(mockTrustDetailsMapper.build(any())).thenReturn(None)
            when(mockSettlorsMapper.build(any())).thenReturn(Some(settlors))
            when(mockDeceasedSettlorMapper.build(any())).thenReturn(None)

            val result = factory.createFrom(arbitraryUserAnswers)

            result mustBe DataSet(
              data = Json.toJson(arbitraryUserAnswers),
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
              settlorCompany = Some(
                List(
                  SettlorCompany(businessName, None, None, None, None)
                )
              )
            )

            when(mockTrustDetailsMapper.build(any())).thenReturn(None)
            when(mockSettlorsMapper.build(any())).thenReturn(Some(settlors))
            when(mockDeceasedSettlorMapper.build(any())).thenReturn(None)

            val result = factory.createFrom(arbitraryUserAnswers)

            result mustBe DataSet(
              data = Json.toJson(arbitraryUserAnswers),
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
