package me.chuwy.otusfp.homework

import cats.effect.{IO, IOApp}
import me.chuwy.otusfp.homework.Env.CounterService

object HWMain extends IOApp.Simple {

  def run: IO[Unit] =
    for {
      cs <- CounterService.apply
      fiber <- Restful.builder(cs).resource.use(_ => IO.never).start
      _ <- fiber.join
    } yield ()

}
