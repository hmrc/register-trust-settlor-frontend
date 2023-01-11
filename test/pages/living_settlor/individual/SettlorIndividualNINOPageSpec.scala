/*
 * Copyright 2023 HM Revenue & Customs
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

import pages.behaviours.PageBehaviours


class SettlorIndividualNINOPageSpec extends PageBehaviours {

  "SettlorIndividualNINOPage" must {

    beRetrievable[String](SettlorIndividualNINOPage(0))

    beSettable[String](SettlorIndividualNINOPage(0))

    beRemovable[String](SettlorIndividualNINOPage(0))
  }
}
