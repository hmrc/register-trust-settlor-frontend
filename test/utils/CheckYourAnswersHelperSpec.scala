/*
 * Copyright 2022 HM Revenue & Customs
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

import base.SpecBase
import models.pages.IndividualOrBusiness._
import models.pages._
import org.mockito.Matchers.any
import org.mockito.Mockito.{reset, when}
import org.scalatest.BeforeAndAfterEach
import pages.living_settlor.business.SettlorBusinessNamePage
import pages.living_settlor.{SettlorIndividualOrBusinessPage, individual => individualPages}
import pages.{deceased_settlor => deceasedPages}
import utils.print.PrintHelpers
import viewmodels.AnswerSection

class CheckYourAnswersHelperSpec extends SpecBase with BeforeAndAfterEach {

  private val name: FullName = FullName("Joe", Some("Joseph"), "Bloggs")
  private val businessName: String = "Business Ltd."

  private val mockPrintHelpers = mock[PrintHelpers]

  private val fakeAnswerSection = AnswerSection()

  override def beforeEach(): Unit = {
    reset(mockPrintHelpers)
  }

  "CheckYourAnswersHelper" when {

    "deceasedSettlor" when {
      "there is a deceased settlor" must {
        "return deceased settlor section" in {

          when(mockPrintHelpers.deceasedSettlorSection(any(), any(), any())(any()))
            .thenReturn(fakeAnswerSection)

          val userAnswers = emptyUserAnswers
            .set(deceasedPages.SettlorsNamePage, name).success.value

          val helper = new CheckYourAnswersHelper(mockPrintHelpers)(userAnswers, fakeDraftId)

          helper.deceasedSettlor mustBe Seq(fakeAnswerSection)
        }
      }

      "there is no deceased settlor" must {
        "return no answers" in {

          val helper = new CheckYourAnswersHelper(mockPrintHelpers)(emptyUserAnswers, fakeDraftId)

          helper.deceasedSettlor mustBe Nil
        }
      }
    }

    "livingSettlors" when {
      "there are living settlors" must {
        "return living settlor sections" in {

          when(mockPrintHelpers.livingSettlorSection(any(), any(), any(), any())(any()))
            .thenReturn(fakeAnswerSection)

          when(mockPrintHelpers.businessSettlorSection(any(), any(), any(), any())(any()))
            .thenReturn(fakeAnswerSection)

          val userAnswers = emptyUserAnswers
            .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
            .set(individualPages.SettlorIndividualNamePage(0), name).success.value
            .set(SettlorIndividualOrBusinessPage(1), Business).success.value
            .set(SettlorBusinessNamePage(1), businessName).success.value

          val helper = new CheckYourAnswersHelper(mockPrintHelpers)(userAnswers, fakeDraftId)

          helper.livingSettlors mustBe Seq(fakeAnswerSection, fakeAnswerSection)
        }
      }

      "there are no living settlors" must {
        "return no answers" in {

          val helper = new CheckYourAnswersHelper(mockPrintHelpers)(emptyUserAnswers, fakeDraftId)

          helper.livingSettlors mustBe Nil
        }
      }
    }
  }
}
