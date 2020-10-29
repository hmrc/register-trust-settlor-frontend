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
import models.pages.KindOfBusiness._
import models.pages.{InternationalAddress, UKAddress}
import pages.living_settlor.business._

class BusinessSettlorsMapperSpec extends SpecBase {

  private val name: String = "Name"
  private val utr: String = "1234567890"

  private val addressLine: String = "Line"
  private val postcode: String = "AB1 1AB"
  private val country: String = "FR"
  private val ukAddress: UKAddress = UKAddress(addressLine, addressLine, None, None, postcode)
  private val nonUkAddress: InternationalAddress = InternationalAddress(addressLine, addressLine, None, country)
  private val ukAddressType: AddressType = AddressType(addressLine, addressLine, None, None, Some(postcode), "GB")
  private val nonUkAddressType: AddressType = AddressType(addressLine, addressLine, None, None, None, country)

  "BusinessSettlors mapper" must {

    val mapper: BusinessSettlorsMapper = injector.instanceOf[BusinessSettlorsMapper]

    "map user answers to business settlor model" when {
      
      "one settlor" when {

        val index: Int = 0

        "not a trust for the employees of a company" when {

          "UTR" in {

            val userAnswers: UserAnswers = emptyUserAnswers
              .set(SettlorBusinessNamePage(index), name).success.value
              .set(SettlorBusinessUtrYesNoPage(index), true).success.value
              .set(SettlorBusinessUtrPage(index), utr).success.value

            val result = mapper.build(userAnswers).get

            result mustBe List(SettlorCompany(
              name = name,
              companyType = None,
              companyTime = None,
              identification = Some(IdentificationOrgType(
                utr = Some(utr),
                address = None
              ))
            ))
          }

          "UK address" in {

            val userAnswers: UserAnswers = emptyUserAnswers
              .set(SettlorBusinessNamePage(index), name).success.value
              .set(SettlorBusinessUtrYesNoPage(index), false).success.value
              .set(SettlorBusinessAddressYesNoPage(index), true).success.value
              .set(SettlorBusinessAddressUKYesNoPage(index), true).success.value
              .set(SettlorBusinessAddressUKPage(index), ukAddress).success.value

            val result = mapper.build(userAnswers).get

            result mustBe List(SettlorCompany(
              name = name,
              companyType = None,
              companyTime = None,
              identification = Some(IdentificationOrgType(
                utr = None,
                address = Some(ukAddressType)
              ))
            ))
          }

          "non-UK address" in {

            val userAnswers: UserAnswers = emptyUserAnswers
              .set(SettlorBusinessNamePage(index), name).success.value
              .set(SettlorBusinessUtrYesNoPage(index), false).success.value
              .set(SettlorBusinessAddressYesNoPage(index), true).success.value
              .set(SettlorBusinessAddressUKYesNoPage(index), false).success.value
              .set(SettlorBusinessAddressInternationalPage(index), nonUkAddress).success.value

            val result = mapper.build(userAnswers).get

            result mustBe List(SettlorCompany(
              name = name,
              companyType = None,
              companyTime = None,
              identification = Some(IdentificationOrgType(
                utr = None,
                address = Some(nonUkAddressType)
              ))
            ))
          }

          "no identification" in {

            val userAnswers: UserAnswers = emptyUserAnswers
              .set(SettlorBusinessNamePage(index), name).success.value
              .set(SettlorBusinessUtrYesNoPage(index), false).success.value
              .set(SettlorBusinessAddressYesNoPage(index), false).success.value

            val result = mapper.build(userAnswers).get

            result mustBe List(SettlorCompany(
              name = name,
              companyType = None,
              companyTime = None,
              identification = None
            ))
          }
        }

        "a trust for the employees of a company" when {

          "Investment type and existed less than 2 years" in {

            val userAnswers: UserAnswers = emptyUserAnswers
              .set(SettlorBusinessNamePage(index), name).success.value
              .set(SettlorBusinessUtrYesNoPage(index), true).success.value
              .set(SettlorBusinessUtrPage(index), utr).success.value
              .set(SettlorBusinessTypePage(index), Investment).success.value
              .set(SettlorBusinessTimeYesNoPage(index), false).success.value

            val result = mapper.build(userAnswers).get

            result mustBe List(SettlorCompany(
              name = name,
              companyType = Some(Investment.toString),
              companyTime = Some(false),
              identification = Some(IdentificationOrgType(
                utr = Some(utr),
                address = None
              ))
            ))
          }

          "Trading type and existed more than 2 years" in {

            val userAnswers: UserAnswers = emptyUserAnswers
              .set(SettlorBusinessNamePage(index), name).success.value
              .set(SettlorBusinessUtrYesNoPage(index), true).success.value
              .set(SettlorBusinessUtrPage(index), utr).success.value
              .set(SettlorBusinessTypePage(index), Trading).success.value
              .set(SettlorBusinessTimeYesNoPage(index), true).success.value

            val result = mapper.build(userAnswers).get

            result mustBe List(SettlorCompany(
              name = name,
              companyType = Some(Trading.toString),
              companyTime = Some(true),
              identification = Some(IdentificationOrgType(
                utr = Some(utr),
                address = None
              ))
            ))
          }
        }
      }

      "more than one settlor" in {

        def name(index: Int) = s"Name $index"

        val userAnswers: UserAnswers = emptyUserAnswers
          .set(SettlorBusinessNamePage(0), name(0)).success.value
          .set(SettlorBusinessUtrYesNoPage(0), false).success.value
          .set(SettlorBusinessAddressYesNoPage(0), false).success.value

          .set(SettlorBusinessNamePage(1), name(1)).success.value
          .set(SettlorBusinessUtrYesNoPage(1), false).success.value
          .set(SettlorBusinessAddressYesNoPage(1), false).success.value

        val result = mapper.build(userAnswers).get

        result mustBe List(
          SettlorCompany(
            name = name(0),
            companyType = None,
            companyTime = None,
            identification = None
          ),
          SettlorCompany(
            name = name(1),
            companyType = None,
            companyTime = None,
            identification = None
          )
        )
      }
    }
  }
}
