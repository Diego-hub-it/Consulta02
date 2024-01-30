package ec.edu.utpl.computacion.c2

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{FileIO, Flow, Framing, Sink, Source}
import akka.util.ByteString

import scala.concurrent.ExecutionContext
import java.nio.file.Paths

object Ejemplo extends App{
  //Se crea los implicts para facilitar el uso
  implicit val actor: ActorSystem = ActorSystem("Ejemplo")//Trabajar con actores
  implicit val mat: ActorMaterializer = ActorMaterializer()//Necesario para trabajar con Akka Streams
  implicit val ec: ExecutionContext = actor.dispatcher//Maneja operaciones asincronas

  //Defino los archivos de entrada y salida
  val entrada = Paths.get("C:/Users/usuario/OneDrive/Escritorio/Texto Minusculas.txt")
  val salida = Paths.get("C:/Users/usuario/OneDrive/Escritorio/Texto Mayusculas.txt")

  val proceso: Flow[ByteString, ByteString, _] =//Se define flujo de Akka Streams
    Flow[ByteString]
      .via(Framing.delimiter(ByteString("\n"), maximumFrameLength = 256, allowTruncation = true))//divide las lineas
      .map(line => transLinea(line.utf8String))//Aplica la funcion para transformar
      .map(line => ByteString(line + "\n"))//Agrega un nuevo caracter a la linea

  val resultado = FileIO.fromPath(entrada)//Lee el contenido de la entrada
    .via(proceso)//Aplica el flujo
    .runWith(FileIO.toPath(salida))//Escribe el resultado

  resultado.onComplete { _ =>//Callback para manejar el resultado
    actor.terminate()//Cierra el sistema de actores
  }

  def transLinea(line: String): String = line.toUpperCase//Funcion que toma una linea y la devuelve en MYS

}
