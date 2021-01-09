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

package pages

import base.SpecBase
import models.pages.AddASettlor.NoComplete
import models.pages.IndividualOrBusiness._
import models.pages.{FullName, KindOfTrust}
import models.pages.Status._
import pages.living_settlor.{SettlorIndividualOrBusinessPage, business => businessPages, individual => individualPages}
import pages.{deceased_settlor => deceasedPages}
import pages.trust_type._

class RegistrationProgressSpec extends SpecBase {

  private val registrationProgress: RegistrationProgress = injector.instanceOf[RegistrationProgress]

  private val name: FullName = FullName("Joe", None, "Bloggs")
  private val businessName: String = "Business Ltd."

  "Registration progress" must {

    "return None if SetUpAfterSettlorDiedYesNoPage not answered" in {

      val result = registrationProgress.settlorsStatus(emptyUserAnswers)

      result mustBe None
    }

    "return InProgress" when {

      "setup after settlor died and deceased settlor incomplete" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, true).success.value

        val result = registrationProgress.settlorsStatus(userAnswers)

        result mustBe Some(InProgress)
      }

      "setup in addition to will trust and deceased settlor incomplete" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(KindOfTrustPage, KindOfTrust.Deed).success.value
          .set(SetUpInAdditionToWillTrustYesNoPage, true).success.value

        val result = registrationProgress.settlorsStatus(userAnswers)

        result mustBe Some(InProgress)
      }

      "not setup in addition to will trust and no living settlors" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(KindOfTrustPage, KindOfTrust.Deed).success.value
          .set(SetUpInAdditionToWillTrustYesNoPage, false).success.value

        val result = registrationProgress.settlorsStatus(userAnswers)

        result mustBe Some(InProgress)
      }

      "there is an incomplete living settlor" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(KindOfTrustPage, KindOfTrust.HeritageMaintenanceFund).success.value
          .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
          .set(individualPages.SettlorIndividualNamePage(0), name).success.value
          .set(LivingSettlorStatus(0), Completed).success.value
          .set(SettlorIndividualOrBusinessPage(1), Business).success.value
          .set(businessPages.SettlorBusinessNamePage(1), businessName).success.value

        val result = registrationProgress.settlorsStatus(userAnswers)

        result mustBe Some(InProgress)
      }
    }

    "return Completed" when {

      "setup after settlor died and deceased settlor complete" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, true).success.value
          .set(deceasedPages.SettlorsNamePage, name).success.value
          .set(DeceasedSettlorStatus, Completed).success.value

        val result = registrationProgress.settlorsStatus(userAnswers)

        result mustBe Some(Completed)
      }

      "setup in addition to will trust and deceased settlor completed" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(KindOfTrustPage, KindOfTrust.Deed).success.value
          .set(SetUpInAdditionToWillTrustYesNoPage, true).success.value
          .set(deceasedPages.SettlorsNamePage, name).success.value
          .set(DeceasedSettlorStatus, Completed).success.value

        val result = registrationProgress.settlorsStatus(userAnswers)

        result mustBe Some(Completed)
      }

      "not setup in addition to will trust and complete living settlors" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(KindOfTrustPage, KindOfTrust.Deed).success.value
          .set(SetUpInAdditionToWillTrustYesNoPage, false).success.value
          .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
          .set(individualPages.SettlorIndividualNamePage(0), name).success.value
          .set(LivingSettlorStatus(0), Completed).success.value
          .set(AddASettlorPage, NoComplete).success.value

        val result = registrationProgress.settlorsStatus(userAnswers)

        result mustBe Some(Completed)
      }

      "there are no incomplete living settlors" in {

        val userAnswers = emptyUserAnswers
          .set(SetUpAfterSettlorDiedYesNoPage, false).success.value
          .set(KindOfTrustPage, KindOfTrust.HeritageMaintenanceFund).success.value
          .set(SettlorIndividualOrBusinessPage(0), Individual).success.value
          .set(individualPages.SettlorIndividualNamePage(0), name).success.value
          .set(LivingSettlorStatus(0), Completed).success.value
          .set(SettlorIndividualOrBusinessPage(1), Business).success.value
          .set(businessPages.SettlorBusinessNamePage(1), businessName).success.value
          .set(LivingSettlorStatus(1), Completed).success.value
          .set(AddASettlorPage, NoComplete).success.value

        val result = registrationProgress.settlorsStatus(userAnswers)

        result mustBe Some(Completed)
      }
    }
  }
}
