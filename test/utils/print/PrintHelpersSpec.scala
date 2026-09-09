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

package utils.print

import base.SpecBase
import org.mockito.ArgumentMatchers.{any, eq => eqTo}
import org.mockito.Mockito.{verify, when}
import org.scalatestplus.mockito.MockitoSugar
import viewmodels.AnswerSection

class PrintHelpersSpec extends SpecBase with MockitoSugar {

  private val deceased = mock[DeceasedSettlorPrintHelper]
  private val living   = mock[LivingSettlorPrintHelper]
  private val business = mock[BusinessSettlorPrintHelper]

  private val helpers = new PrintHelpers(deceased, living, business)
  private val section = AnswerSection(None, Nil, None)

  "PrintHelpers" must {

    "delegate the deceased settlor section" in {
      when(deceased.printSection(any(), any(), any(), any())(any())).thenReturn(section)

      helpers.deceasedSettlorSection(emptyUserAnswers, "Name", fakeDraftId) mustBe section

      verify(deceased).printSection(eqTo(emptyUserAnswers), eqTo("Name"), eqTo(fakeDraftId), eqTo(0))(any())
    }

    "delegate the living settlor section" in {
      when(living.printSection(any(), any(), any(), any())(any())).thenReturn(section)

      helpers.livingSettlorSection(emptyUserAnswers, "Name", 0, fakeDraftId) mustBe section

      verify(living).printSection(eqTo(emptyUserAnswers), eqTo("Name"), eqTo(fakeDraftId), eqTo(0))(any())
    }

    "delegate the business settlor section" in {
      when(business.printSection(any(), any(), any(), any())(any())).thenReturn(section)

      helpers.businessSettlorSection(emptyUserAnswers, "Name", 0, fakeDraftId) mustBe section

      verify(business).printSection(eqTo(emptyUserAnswers), eqTo("Name"), eqTo(fakeDraftId), eqTo(0))(any())
    }
  }

}