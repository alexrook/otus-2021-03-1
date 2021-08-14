/*
 * Copyright (c) 2012-2020 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package me.chuwy.otusfp.homework

import cats.effect.IO
import cats.effect.testing.specs2.CatsEffect
import io.circe._
import io.circe.syntax._
import me.chuwy.otusfp.homework.Env.CounterService
import org.http4s._
import org.http4s.circe._
import org.http4s.client.Client
import org.http4s.implicits._
import org.specs2.mutable.Specification

class HWSpec extends Specification with CatsEffect {

  import cats.effect.unsafe.IORuntime

  implicit val runtime: IORuntime = cats.effect.unsafe.IORuntime.global

  def httpApp: IO[HttpApp[IO]] = CounterService.apply.map(Restful.httpApp)

  "CounterRoute" should {
    "return counter json" in {

      val request: Request[IO] =
        Request(method = Method.GET, uri = uri"/counter")
      val expectedJson = Json.obj(
        "value" := 1
      )

      val actual: IO[Json] = for {
        httpA <- httpApp
        client <- IO.pure(Client.fromHttpApp(httpA))
        response <- client.expect[Json](request)
      } yield response

      actual.map(_ must beEqualTo(expectedJson))

    }

  }

  "SlowStreamRoute" should {
    "return right json" in {

      val request: Request[IO] =
        Request(method = Method.GET, uri = uri"/slow/100/50/1")

      val expected: String = List.fill(100)("1").mkString

      val actual: IO[String] = for {
        httpA <- httpApp
        client <- IO.pure(Client.fromHttpApp(httpA))
        response <- client.expect[String](request)
      } yield response

      actual.map(_ must beEqualTo(expected))

    }

  }
}
