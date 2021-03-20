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

package mapping

import mapping.reads.LivingSettlors
import models.UserAnswers

import scala.reflect.ClassTag

abstract class Mapping[A, B <: mapping.reads.Settlor : ClassTag] {

  def build(userAnswers: UserAnswers): Option[List[A]] = {
    settlors(userAnswers) match {
      case Nil => None
      case list => Some(list.map(settlorType))
    }
  }

  private def settlors(userAnswers: UserAnswers): List[B] = {
    val runtimeClass = implicitly[ClassTag[B]].runtimeClass

    userAnswers.get(LivingSettlors).getOrElse(Nil).collect {
      case x: B if runtimeClass.isInstance(x) => x
    }
  }

  def settlorType(settlor: B): A

}
