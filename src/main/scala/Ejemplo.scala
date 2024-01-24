package ec.edu.utpl.computacion.c2

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString

import scala.concurrent.ExecutionContext
import java.nio.file.Paths

object Ejemplo extends App{
  implicit val actor: ActorSystem = ActorSystem("Ejemplo")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = actor.dispatcher

  val entrada = Paths.get("C:/Users/usuario/OneDrive/Escritorio/Texto Minusculas.txt")
  val salida = Paths.get("C:/Users/usuario/OneDrive/Escritorio/Texto Mayusculas.txt")

  val proceso: Flow[ByteString, ByteString, _] =
    Flow[ByteString]
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
      .map(line => transLinea(line.utf8String))
      .map(line => ByteString(line + "\n"))

  val resultado = FileIO.fromPath(entrada)
    .via(proceso)
    .runWith(FileIO.toPath(salida))

  resultado.onComplete { _ =>
    actor.terminate()
  }

  def transLinea(line: String): String = line.toUpperCase

}
