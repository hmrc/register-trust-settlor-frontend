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

package base

import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.scalatestplus.mockito.MockitoSugar
import repositories.RegistrationsRepository
import services.DraftRegistrationService

import java.time.LocalDate
import scala.concurrent.Future

trait Mocked extends MockitoSugar {

  val registrationsRepository: RegistrationsRepository = mock[RegistrationsRepository]

  when(registrationsRepository.get(any())(any())).thenReturn(Future.successful(None))
  when(registrationsRepository.set(any())(any(), any())).thenReturn(Future.successful(true))

  val mockCreateDraftRegistrationService: DraftRegistrationService = mock[DraftRegistrationService]

  val mockedTrustStartDate: LocalDate = LocalDate.parse("2019-02-03")
  when(registrationsRepository.getTrustSetupDate(any())(any()))
    .thenReturn(Future.successful(Some(mockedTrustStartDate)))
}
