package edu.umass.cs.iesl.scalacommons

import _root_.javax.xml.parsers.SAXParser
import xml.factory.XMLLoader
import org.xml.sax.InputSource

import scala.xml._
import collection.TraversableOnce
import java.io.{InputStream, File}
import io.BufferedSource

object XmlUtils {

  /**
   * All the text contained in a node, with no tags, but guaranteeing a space separating text bits that had a tag between them (e.g.
   * blah<foo>bubba</foo>zimbob shourd give blah bubba zimbob, not blahbubbazimbob as node.text would have done.
   * @param node
   * @return
   */
  def spaceSeparatedText(node: Node): String = node.descendant.filter(_.isInstanceOf[Text]).map(_.text).mkString(" ").replaceAll("\\s|\"", " ").trim

  /* {
    // broken up for debugging
    val parts: Seq[String] = node.descendant.filter(_.isInstanceOf[Text]).map(_.text)
    val comb: String = parts.mkString(" ")
    val s: String = comb.replaceAll("\\s|\"", " ").trim
    s
    }*/
  def descendantExcluding(node: Node, excludeLabels: Seq[String]): List[Node] = {
    node.child.toList.filter(c => !excludeLabels.contains(c.label)).flatMap {
      x => x :: descendantExcluding(x, excludeLabels)
    }
  }

  // http://stackoverflow.com/questions/8525675/how-to-get-a-streaming-iteratornode-from-a-large-xml-document
  def processRecordsStreaming[T](input: InputStream)(f: Node => T) {
    new {
      var depth = 0
    } with
      scala.xml.parsing.ConstructingParser(new BufferedSource(input), false) {
      nextch // initialize per documentation
      document

      // trigger parsing by requesting document
      //var depth = 0 // track depth
      override def elemStart(pos: Int, pre: String, label: String, attrs: MetaData, scope: NamespaceBinding) {
        super.elemStart(pos, pre, label, attrs, scope)
        depth += 1
      }

      override def elemEnd(pos: Int, pre: String, label: String) {
        depth -= 1
        super.elemEnd(pos, pre, label)
      }

      override def elem(pos: Int, pre: String, label: String, attrs: MetaData, pscope: NamespaceBinding, nodes: NodeSeq): NodeSeq = {
        val node: Node = super.elem(pos, pre, label, attrs, pscope, nodes).head
        depth match {
          case 1 => <dummy/> // dummy final roll up
          case 2 => f(node); NodeSeq.Empty // process and discard first-level nodes
          case _ => node // roll up other nodes
        }
      }
    }
  }

  // http://stackoverflow.com/questions/8525675/how-to-get-a-streaming-iteratornode-from-a-large-xml-document
  def processRecordsStreaming[T](input: InputStream, labels: Seq[String])(f: Node => T) {
    new {
      var depth = 0
    } with scala.xml.parsing.ConstructingParser(new BufferedSource(input), false) {
      nextch // initialize per documentation
      document

      // trigger parsing by requesting document
      //var depth = 0 // track depth
      override def elemStart(pos: Int, pre: String, label: String, attrs: MetaData, scope: NamespaceBinding) {
        super.elemStart(pos, pre, label, attrs, scope)
        depth += 1
      }

      override def elemEnd(pos: Int, pre: String, label: String) {
        depth -= 1
        super.elemEnd(pos, pre, label)
      }

      override def elem(pos: Int, pre: String, label: String, attrs: MetaData, pscope: NamespaceBinding, nodes: NodeSeq): NodeSeq = {
        val node: Node = super.elem(pos, pre, label, attrs, pscope, nodes).head
        if (labels contains label) {
          f(node);
          if (depth == 1) <dummy/> else NodeSeq.Empty
        }
        else node
        /*
              depth match {
                case 1 => <dummy/> // dummy final roll up
                case _ => if(labels contains label) {f(node); NodeSeq.Empty } else node
              }*/
      }
    }
  }

  // http://stackoverflow.com/questions/3797699/generator-block-to-iterator-stream-conversion
  def generatorToTraversable[T](func: (T => Unit) => Unit) = new Traversable[T] {
    def foreach[X](f: T => X) {
      func(f(_))
    }
  }

  /*
   The result of generatorToTraversable is not traversable more than once (even though a new ConstructingParser
   is instantiated on each foreach call) because the input stream is a Source, which is an Iterator.
   We can't override Traversable.isTraversableAgain because it's final.
   Really we'd like to enforce this by just returning an Iterator.  However, both Traversable.toIterator and
   Traversable.view.toIterator make an intermediate Stream, which will cache all the entries (defeating the
   whole purpose of this exercise).  Oh well; just let the stream throw an exception if it's accessed twice.
   Also note the whole thing isn't threadsafe.
    */
  def firstLevelNodes(input: InputStream): TraversableOnce[Node] = generatorToTraversable(processRecordsStreaming(input))

  def nodesMatching(input: InputStream, labels: Seq[String]): TraversableOnce[Node] = generatorToTraversable(processRecordsStreaming(input, labels))

  //def firstLevelNodes2(input: InputStream): Stream[Node] = generatorToTraversable(processRecordsStreaming(input))
}

object XMLIgnoreDTD extends XMLLoader[Elem] {
  override def parser: SAXParser = {
    val f = javax.xml.parsers.SAXParserFactory.newInstance()
    f.setNamespaceAware(false)
    // in some parsers, this feature just means to ignore the DTD if present
    // others refuse to parse the doc if there is a DTD.  Yuck...
    // f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true)

    f.setFeature("http://xml.org/sax/features/namespaces", false);
    f.setFeature("http://xml.org/sax/features/validation", false);
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

    f.newSAXParser()
  }
}

// thanks to http://weblogs.java.net/blog/cayhorstmann/archive/2011/12/12/sordid-tale-xml-catalogs
// (but it doesn't work anyway)
object XMLCatalogDTD extends XMLLoader[Elem] {
  def setGlobalXMLCatalogDir(catalogDir: String) {
    System.setProperty("xml.catalog.files", catalogDir) //"/etc/xml/catalog")
  }

  val res = new com.sun.org.apache.xml.internal.resolver.tools.CatalogResolver

  override def adapter = new parsing.NoBindingFactoryAdapter() {
    override def resolveEntity(publicId: String, systemId: String) = {
      res.resolveEntity(publicId, systemId)
    }
  }
}

/**
 * Avoid lots of nonsense with absolute paths to DTDs vs. relative based on the working directory (neither of which are portable).
 * by assuming that the dtd filenames are unique
 */
class XMLFilenameOnlyMappingDTDLoader(dtdMap: Map[String, InputSource]) extends XMLLoader[Elem] {

  override def adapter = new parsing.NoBindingFactoryAdapter() {
    override def resolveEntity(publicId: String, systemId: String) = {
      val systemFilename: String = if (systemId == null) null else systemId.substring(systemId.lastIndexOf(File.separator) + 1)
      val publicFilename: String = if (publicId == null) null else publicId.substring(publicId.lastIndexOf(File.separator) + 1)
      val s: InputSource = dtdMap.get(systemFilename).getOrElse(dtdMap.get(publicFilename).get)
      s
    }
  }
}
