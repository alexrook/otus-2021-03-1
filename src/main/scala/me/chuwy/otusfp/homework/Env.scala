package me.chuwy.otusfp.homework

import cats.Functor
import cats.effect.{IO, IOApp, Ref}
import cats.implicits._
import fs2.Chunk

import java.util.concurrent.TimeUnit
import scala.concurrent.duration._

object Env extends IOApp.Simple {

  case class CounterService[F[_] : Functor](ref: Ref[F, Int]) {
    def count(): F[Counter] = ref.updateAndGet(_ + 1).map(Counter.apply)
  }

  object CounterService {
    def apply: IO[CounterService[IO]] = {
      println("Creation")
      Ref[IO].of(0).map(v => CounterService(v))
    }
  }

  object SlowStreamService {

    import fs2.Stream

    def slowStream(total: Long, chunk: Int, time: Int): Stream[IO, String] = {
      println(time)
      Stream.emit(1)
        .repeatN(total)
        .chunkN(chunk)
        .metered[IO](FiniteDuration(time,TimeUnit.SECONDS))
        .evalMapChunk{
          chunk:Chunk[Int]=>
            IO.pure(chunk.toList.mkString)
        }

    }

  }


  def run: IO[Unit] = {
    for {
      s <- CounterService.apply
      c1 <- s.count()
      _ <- IO.println(c1)
      c2 <- s.count()
      _ <- IO.println(c2)
    } yield ()
  }

}
