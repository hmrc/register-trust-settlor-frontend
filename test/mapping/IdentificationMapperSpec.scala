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

package mapping

import base.SpecBase
import mapping.IdentificationMapper.buildAddress
import models.AddressType
import models.pages.{InternationalAddress, UKAddress}

class IdentificationMapperSpec extends SpecBase {

  private val ukAddress: UKAddress               =
    UKAddress("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), "AB1 1AB")
  private val nonUkAddress: InternationalAddress =
    InternationalAddress("Line 1", "Line 2", Some("Line 3"), "FR")
  private val ukAddressType: AddressType         =
    AddressType("Line 1", "Line 2", Some("Line 3"), Some("Line 4"), Some("AB1 1AB"), "GB")
  private val nonUkAddressType: AddressType      =
    AddressType("Line 1", "Line 2", Some("Line 3"), None, None, "FR")

  "IdentificationMapper" must {

    "convert Option[Address] to Option[AddressType]" when {

      "UK address" in {

        val result = buildAddress(Some(ukAddress))

        result mustBe Some(ukAddressType)
      }

      "non-UK address" in {

        val result = buildAddress(Some(nonUkAddress))

        result mustBe Some(nonUkAddressType)
      }

      "no address" in {

        val result = buildAddress(None)

        result mustBe None
      }
    }
  }
}
