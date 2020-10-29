/*
 * Copyright 2020 HM Revenue & Customs
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

import base.SpecBase
import models.UserAnswers
import models.pages.FullName
import org.mockito.Matchers.any
import org.mockito.Mockito.when

class SettlorsMapperSpec extends SpecBase {

  private val mockIndividualMapper: IndividualSettlorsMapper = mock[IndividualSettlorsMapper]
  private val mockBusinessMapper: BusinessSettlorsMapper = mock[BusinessSettlorsMapper]

  private val individualSettlor: Settlor = Settlor(
    name = FullName("Joe", None, "Bloggs"),
    dateOfBirth = None,
    identification = None
  )

  private val businessSettlor: SettlorCompany = SettlorCompany(
    name = "Name",
    companyType = None,
    companyTime = None,
    identification = None
  )

  "Settlors mapper" must {

    val mapper: SettlorsMapper = new SettlorsMapper(mockIndividualMapper, mockBusinessMapper)

    val arbitraryUserAnswers: UserAnswers = emptyUserAnswers

    "map user answers to settlors model" when {

      "no settlors" in {

        when(mockIndividualMapper.build(any())).thenReturn(None)
        when(mockBusinessMapper.build(any())).thenReturn(None)

        val result = mapper.build(arbitraryUserAnswers)

        result mustBe None
      }

      "settlors of one type" when {

        "individual" in {

          when(mockIndividualMapper.build(any())).thenReturn(Some(List(individualSettlor)))
          when(mockBusinessMapper.build(any())).thenReturn(None)

          val result = mapper.build(arbitraryUserAnswers)

          result mustBe Some(Settlors(
            settlor = Some(List(individualSettlor)),
            settlorCompany = None
          ))
        }

        "business" in {

          when(mockIndividualMapper.build(any())).thenReturn(None)
          when(mockBusinessMapper.build(any())).thenReturn(Some(List(businessSettlor)))

          val result = mapper.build(arbitraryUserAnswers)

          result mustBe Some(Settlors(
            settlor = None,
            settlorCompany = Some(List(businessSettlor))
          ))
        }
      }

      "settlors of both type" in {

        when(mockIndividualMapper.build(any())).thenReturn(Some(List(individualSettlor)))
        when(mockBusinessMapper.build(any())).thenReturn(Some(List(businessSettlor)))

        val result = mapper.build(arbitraryUserAnswers)

        result mustBe Some(Settlors(
          settlor = Some(List(individualSettlor)),
          settlorCompany = Some(List(businessSettlor))
        ))
      }
    }
  }
}
