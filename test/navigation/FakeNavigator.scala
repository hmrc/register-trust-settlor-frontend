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

package navigation

import config.FrontendAppConfig
import play.api.mvc.Call
import pages._
import models.{Mode, NormalMode, UserAnswers}
import uk.gov.hmrc.auth.core.AffinityGroup

class FakeNavigator(config: FrontendAppConfig,
                    val desiredRoute: Call = Call("GET", "/foo"),
                    mode: Mode = NormalMode
                   ) extends Navigator(config) {

  override def nextPage(page: Page, mode: Mode, fakeDraftId: String, affinityGroup: AffinityGroup): UserAnswers => Call = _ => desiredRoute
}

