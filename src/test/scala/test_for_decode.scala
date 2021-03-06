
package scala.ihmm

import scala.collection.mutable.Stack
import org.scalatest.FunSuite

class PLMRF_Decode_Test extends FunSuite {

  test("parseDecode should return Map('inputfile', 'mode') with weightfile") {
    val opt = Decode.parseDecode(Map("mode" -> "decode"), List("hoge.txt", "weightfile"))
    assert(opt.contains("inputfile"))
    assert(opt.get("inputfile") == Some("hoge.txt"))
    assert(opt.contains("probfile"))
    assert(opt.get("probfile") == Some("weightfile"))
  }

  test("ArgumentParse should get Map like below ver decode") {
    val opt = Main.ArgumentParse(Map(), List("decode", "file", "model"))
    assert(opt.contains("inputfile"))
    assert(opt.get("inputfile") == Some("file"))
    assert(opt.contains("probfile"))
    assert(opt.get("probfile") == Some("model"))
  }

}
