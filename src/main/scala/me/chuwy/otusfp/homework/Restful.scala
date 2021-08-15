package me.chuwy.otusfp.homework

import cats.effect._
import me.chuwy.otusfp.homework.Env.{CounterService, SlowStreamService}
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.dsl.io._
import org.http4s.implicits._
import org.http4s.server.Router
import org.http4s.{HttpApp, HttpRoutes}

import scala.concurrent.ExecutionContext.global

object Restful {

  def counterRoute(cs: CounterService[IO]): HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "counter" => Ok(cs.count())
    }

  def slowStreamRoute: HttpRoutes[IO] =
    HttpRoutes.of[IO] {
      case GET -> Root / "slow" / LongVar(total) / IntVar(chunk) / IntVar(time) =>
        Ok(SlowStreamService.slowStream(total, chunk, time))
    }

  def httpApp(cs: CounterService[IO]): HttpApp[IO] = Router(
    "/" -> counterRoute(cs),
    "/" -> slowStreamRoute
  ).orNotFound

  def builder(cs: CounterService[IO]): BlazeServerBuilder[IO] =
    BlazeServerBuilder[IO](global)
      .bindHttp(port = 8080, host = "localhost")
      .withHttpApp(httpApp(cs))

}
