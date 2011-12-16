package edu.umass.cs.iesl.scalacommons

import _root_.javax.xml.parsers.SAXParser
import xml.factory.XMLLoader
import xml.{parsing, Elem}
import org.xml.sax.InputSource
import java.io.File

class XmlUtils {}


object XMLIgnoreDTD extends XMLLoader[Elem] {
  override def parser: SAXParser = {
    val f = javax.xml.parsers.SAXParserFactory.newInstance()
    f.setNamespaceAware(false)
    f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
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
