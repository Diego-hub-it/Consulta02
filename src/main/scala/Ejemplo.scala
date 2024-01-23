package ec.edu.utpl.computacion.c2

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString

import scala.concurrent.ExecutionContext
import java.nio.file.Paths

object Ejemplo extends App{
  implicit val actor: ActorSystem = ActorSystem("Ejemplo")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = actor.dispatcher

  // Define la ruta del archivo de entrada y salida
  val entrada = Paths.get("C://Users//usuario//OneDrive//Escritorio//Texto Minusculas.txt")
  val salida = Paths.get("C://Users//usuario//OneDrive//Escritorio//Texto Mayusculas.txt")

  // Define un flujo de Akka Streams para procesar el archivo
  val proceso: Flow[ByteString, ByteString, _] =
    Flow[ByteString]
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
      .map(line => transLinea(line.utf8String))
      .map(line => ByteString(line + "\n"))

  // Lee el archivo de entrada, procesa las líneas y escribe el resultado en el archivo de salida
  val resultado = FileIO.fromPath(entrada)
    .via(proceso)
    .runWith(FileIO.toPath(salida))

  // Maneja el resultado de la operación
  resultado.onComplete { _ =>
    actor.terminate()
  }

  // Transforma una línea (simplemente convierte a mayúsculas en este caso)
  def transLinea(line: String): String = line.toUpperCase

}
