package me.chuwy.otusfp.homework

import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import org.http4s.EntityEncoder
import org.http4s.circe.jsonEncoderOf

case class Counter(value: Int)

object Counter {

  implicit val counterEncoder: Encoder[Counter] = deriveEncoder[Counter]

  //implicit def counterEntityEncoder[F[_] : Concurrent]: EntityEncoder[F, Counter] = jsonEncoderOf[F, Counter]
  implicit def counterEntityEncoder[F[_]]: EntityEncoder[F, Counter] = jsonEncoderOf[F, Counter]

}
