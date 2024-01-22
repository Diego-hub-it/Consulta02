package ec.edu.utpl.computacion.c2

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString

import scala.concurrent.ExecutionContext
import java.nio.file.Paths

object Ejemplo extends App{
  implicit val system: ActorSystem = ActorSystem("Ejemplo")
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = system.dispatcher

  // Define la ruta del archivo de entrada y salida
  val inputFilePath = Paths.get("C://Users//usuario//OneDrive//Escritorio//Texto Minusculas.txt")
  val outputFilePath = Paths.get("C://Users//usuario//OneDrive//Escritorio//Texto Mayusculas.txt")

  // Define un flujo de Akka Streams para procesar el archivo
  val fileProcessingFlow: Flow[ByteString, ByteString, _] =
    Flow[ByteString]
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
      .map(line => transLinea(line.utf8String))
      .map(line => ByteString(line + "\n"))

  // Lee el archivo de entrada, procesa las líneas y escribe el resultado en el archivo de salida
  val processingResult = FileIO.fromPath(inputFilePath)
    .via(fileProcessingFlow)
    .runWith(FileIO.toPath(outputFilePath))

  // Maneja el resultado de la operación
  processingResult.onComplete { _ =>
    system.terminate()
  }

  // Transforma una línea (simplemente convierte a mayúsculas en este caso)
  def transLinea(line: String): String = line.toUpperCase

}
