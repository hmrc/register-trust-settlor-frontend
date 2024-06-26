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

package pages.living_settlor.individual

import models.pages.PassportOrIdCardDetails
import pages.behaviours.PageBehaviours

class SettlorIndividualPassportPageSpec extends PageBehaviours {

  "SettlorIndividualPassportPage" must {

    beRetrievable[PassportOrIdCardDetails](SettlorIndividualPassportPage(0))

    beSettable[PassportOrIdCardDetails](SettlorIndividualPassportPage(0))

    beRemovable[PassportOrIdCardDetails](SettlorIndividualPassportPage(0))
  }
}
