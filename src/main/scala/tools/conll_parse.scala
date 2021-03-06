package scala.tools

import scala.io.Source

object ConllParse {

  val usage = "usage: java -jar I-HMM.jar conll file file file ..."

  def onlySurface(lines :String): String = lines.split('\t')(1)

  def surfacePOS(lines :String): String = lines.split('\t')(1) + "/" + lines.split('\t')(4)

  def parse_conll_file(filename:String): List[String] = {
    def nextLine(file_iter: Iterator[String],
      tmplist: List[String], res: List[String]): List[String] = {
      if (!file_iter.hasNext) res
      else
        file_iter.next.stripLineEnd match {
          case "" 
              => nextLine(file_iter, List.empty[String], res ::: List(tmplist.mkString(" ")))
          case line:String
              => nextLine(file_iter, tmplist ::: List(line.split('\t')(1)), res)
        }
    }
    nextLine(Source.fromFile(filename).getLines, List.empty[String], List.empty[String])
  }

  def parse(args: Map[String, String]): Unit = {
    try {
      args("file").split(' ') foreach {arg =>
        arg match {
          case filename: String 
              => parse_conll_file(filename) foreach (println)
          case _ => sys.error("please input file")
        }
      }
    } catch {
      case e: RuntimeException => println(usage)
    }
  }

}
