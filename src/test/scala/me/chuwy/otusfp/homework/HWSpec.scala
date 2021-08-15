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
