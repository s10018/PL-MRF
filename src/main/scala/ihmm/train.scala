package scala.ihmm

import scala.io.Source
import scala.annotation.tailrec
import collection.mutable.ListBuffer

object Train {
  val usage = "usage: jave -jar PL-MRF.jar train -test testfile -layer layer_n -state state_n -c cut-off -dump dumpfile"
  val unknow = "##UNKOWN##"

  def parseTrain(opt: Map[String, String], rest :List[String]): Map[String, String] = {
    try {
      rest match {
        case Nil => opt
        case "-test" :: testPath :: rest
            => parseTrain(opt ++ Map("testPath" -> testPath), rest)
        case "-layer" :: layerN :: rest
            => parseTrain(opt ++ Map("layerN"   -> layerN  ), rest)
        case "-state" :: stateN :: rest
            => parseTrain(opt ++ Map("stateN"   -> stateN  ), rest)
        case "-c" :: cutOff :: rest
            => parseTrain(opt ++ Map("cutOff"   -> cutOff  ), rest)
        case "-dump" :: dumpPath :: rest
            => parseTrain(opt ++ Map("dumpPath" -> dumpPath), rest)
        case _
            => Map("hoge" -> "")
      }
    } catch {
      case e: RuntimeException => Map("hoge" -> "")
    }
  }

  def train(opt: Map[String, String]): Unit = {
    val testPath: String = opt("testPath")
    val dumpPath: String = opt("dumpPath")

    val layerN: Int = opt("layerN").toInt
    val stateN: Int = opt("stateN").toInt
    val cutOff: Int = opt("cutOff").toInt

    println(testPath);println(dumpPath);println(layerN);println(stateN);println(cutOff)

    val sentences = readAndSetData(testPath, cutOff)
    val vocabulary = examinVocabulary(sentences)
    sentences.foreach { sent =>
      sent.foreach { word =>
        print(word + " ")
      }
      print("\n")
    }
    vocabulary.foreach(println)
    println(Optimizer.run(sentences, vocabulary, stateN))
  }

  def readAndSetData(testPath: String, cutOff: Int): ListBuffer[Array[String]] = {
    def convert2sentences(testPath: String): ListBuffer[Array[String]] = {
      def split2words(sentence: String): Array[String] = {
        sentence.split(" ")
      }
      val sentences = new ListBuffer[Array[String]]
      for(line <- Source.fromFile(testPath).getLines()) {
        sentences += split2words(line)
      }
      return sentences
    }
    def extractLowFreqWord(sentences: ListBuffer[Array[String]], cutOff: Int): List[String] = {
      def countFreq(sentences: ListBuffer[Array[String]]): collection.mutable.Map[String, Int] = {
        sentences.foldLeft(collection.mutable.Map.empty[String, Int]) { (mapC, sentArr) =>
          sentArr.toList.foldLeft(mapC) { (mapC_, word) =>
            mapC_ + (word -> (mapC_.getOrElse(word, 0) + 1))
          }
        }
      }
      countFreq(sentences).filter { case (word, count) => count < cutOff }.keys.toList
    }
    val sentences   = convert2sentences(testPath)
    val lowFreqWord = extractLowFreqWord(sentences, cutOff)

    sentences.map { sent =>
      sent.map( word => if (lowFreqWord.contains(word)) unknow else word )
    }
  }

  def examinVocabulary(sentences: ListBuffer[Array[String]]): List[String] = {
    sentences.foldLeft(ListBuffer.empty[String]) { (vocab, sentence) =>
      sentence.foldLeft(vocab) { (vocab_, word) => vocab_ += word }
    }
    .distinct.toList
  }
}
