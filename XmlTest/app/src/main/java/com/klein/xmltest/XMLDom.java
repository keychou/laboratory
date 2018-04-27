package com.klein.xmltest;

import android.content.Context;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by klein on 18-3-31.
 */

public class XMLDom {

    private final String fileName = "myDomXML.xml";
    private Context mContext = null;

    public XMLDom(Context context){
        this.mContext = context;
    }

    /** Dom方式，创建 XML  */
    public String XMLCreate() {
        String xmlWriter = null;

        Person []persons = new Person[3];		// 创建节点Person对象
        persons[0] = new Person(1, "sunboy_2050", "http://blog.csdn.net/sunboy_2050");
        persons[1] = new Person(2, "baidu", "http://www.baidu.com");
        persons[2] = new Person(3, "google", "http://www.google.com");

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element eleRoot = doc.createElement("root");
            eleRoot.setAttribute("author", "homer");
            eleRoot.setAttribute("date", "2012-04-26");
            doc.appendChild(eleRoot);

            int personsLen = persons.length;
            for(int i=0; i<personsLen; i++) {
                Element elePerson = doc.createElement("person");
                eleRoot.appendChild(elePerson);

                Element eleId = doc.createElement("id");
                Node nodeId = doc.createTextNode(persons[i].getId() + "");
                eleId.appendChild(nodeId);
                elePerson.appendChild(eleId);

                Element eleName = doc.createElement("name");
                Node nodeName = doc.createTextNode(persons[i].getName());
                eleName.appendChild(nodeName);
                elePerson.appendChild(eleName);

                Element eleBlog = doc.createElement("blog");
                Node nodeBlog = doc.createTextNode(persons[i].getBlog());
                eleBlog.appendChild(nodeBlog);
                elePerson.appendChild(eleBlog);
            }


            Properties properties = new Properties();
            properties.setProperty(OutputKeys.INDENT, "yes");
            properties.setProperty(OutputKeys.MEDIA_TYPE, "xml");
            properties.setProperty(OutputKeys.VERSION, "1.0");
            properties.setProperty(OutputKeys.ENCODING, "utf-8");
            properties.setProperty(OutputKeys.METHOD, "xml");
            properties.setProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperties(properties);

            DOMSource domSource = new DOMSource(doc.getDocumentElement());
            OutputStream output = new ByteArrayOutputStream();
            StreamResult result = new StreamResult(output);
            transformer.transform(domSource, result);

            xmlWriter = output.toString();

        } catch (ParserConfigurationException e) {		// factory.newDocumentBuilder
            e.printStackTrace();
        } catch (DOMException e) {						// doc.createElement
            e.printStackTrace();
        } catch (TransformerFactoryConfigurationError e) {		// TransformerFactory.newInstance
            e.printStackTrace();
        } catch (TransformerConfigurationException e) {		// transformerFactory.newTransformer
            e.printStackTrace();
        } catch (TransformerException e) {				// transformer.transform
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        savedXML(fileName, xmlWriter.toString());

        return xmlWriter.toString();
    }


    /** Dom方式，解析 XML  */
    public String XMLResolve() {
        StringWriter xmlWriter = new StringWriter();

        InputStream is= readXML(fileName);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            doc.getDocumentElement().normalize();
            NodeList nlRoot = doc.getElementsByTagName("root");
            Element eleRoot = (Element)nlRoot.item(0);
            String attrAuthor = eleRoot.getAttribute("author");
            String attrDate = eleRoot.getAttribute("date");
            xmlWriter.append("root").append("\t\t");
            xmlWriter.append(attrAuthor).append("\t");
            xmlWriter.append(attrDate).append("\n");

            NodeList nlPerson = eleRoot.getElementsByTagName("person");
            int personsLen = nlPerson.getLength();
            Person []persons = new Person[personsLen];
            for(int i=0; i<personsLen; i++) {
                Element elePerson = (Element) nlPerson.item(i);		// person节点
                Person person = new Person();						// 创建Person对象

                NodeList nlId = elePerson.getElementsByTagName("id");
                Element eleId = (Element)nlId.item(0);
                String id = eleId.getChildNodes().item(0).getNodeValue();
                person.setId(Integer.parseInt(id));

                NodeList nlName = elePerson.getElementsByTagName("name");
                Element eleName = (Element)nlName.item(0);
                String name = eleName.getChildNodes().item(0).getNodeValue();
                person.setName(name);

                NodeList nlBlog = elePerson.getElementsByTagName("blog");
                Element eleBlog = (Element)nlBlog.item(0);
                String blog = eleBlog.getChildNodes().item(0).getNodeValue();
                person.setBlog(blog);

                xmlWriter.append(person.toString()).append("\n");
                persons[i] = person;
            }

        } catch (ParserConfigurationException e) {		// factory.newDocumentBuilder
            e.printStackTrace();
        } catch (SAXException e) {		// builder.parse
            e.printStackTrace();
        } catch (IOException e) {		// builder.parse
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return xmlWriter.toString();
    }


    private void savedXML(String fileName, String xml) {
        FileOutputStream fos = null;

        try {
            fos = mContext.openFileOutput(fileName, mContext.MODE_PRIVATE);
            byte []buffer = xml.getBytes();
            fos.write(buffer);
            fos.close();
        } catch (FileNotFoundException e) {		// mContext.openFileOutput
            e.printStackTrace();
        } catch (IOException e) {		// fos.write
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private InputStream readXML(String fileName) {
        FileInputStream fin = null;

        try {
            fin = mContext.openFileInput(fileName);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return fin;
    }
}
