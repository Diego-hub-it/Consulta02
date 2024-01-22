package ec.edu.utpl.computacion.c2

import akka.actor.ActorSystem
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString

import java.nio.file.Paths

object Ejemplo extends App{
  implicit val system = ActorSystem("Ejemplo")

  // Define la ruta del archivo de entrada y salida
  val inputFilePath = Paths.get("input.txt")
  val outputFilePath = Paths.get("output.txt")

  // Define un flujo de Akka Streams para procesar el archivo
  val fileProcessingFlow: Flow[ByteString, ByteString, _] =
    Flow[ByteString]
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))
      .map(line => transformLine(line.utf8String))
      .map(line => ByteString(line + "\n"))

  // Lee el archivo de entrada, procesa las líneas y escribe el resultado en el archivo de salida
  val processingResult = FileIO.fromPath(inputFilePath)
    .via(fileProcessingFlow)
    .runWith(FileIO.toPath(outputFilePath))

}
