package br.gleidson.posgrad.alchemy;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import br.gleidson.posgrad.alchemy.api.AlchemyAPI;
import br.gleidson.posgrad.alchemy.result.AlchemyConcept;
import br.gleidson.posgrad.alchemy.result.AlchemyEntity;
import br.gleidson.posgrad.alchemy.result.AlchemyKeyword;
import br.gleidson.posgrad.alchemy.result.AlchemyResult;

public class AlchemyHandler {
	
	private AlchemyAPI alchemyObj = null;
	
	public AlchemyHandler() throws FileNotFoundException, IOException {
        alchemyObj = AlchemyAPI.GetInstanceFromFile("data/api_key.txt");       
	}
	
	public ArrayList<AlchemyResult> getRankedNamedEntities(String url) throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
		ArrayList<AlchemyResult> entityList = new ArrayList<>();
		Document urlRankedNamedEntitiesXml = alchemyObj.URLGetRankedNamedEntities(url);	
		NodeList rankedList = urlRankedNamedEntitiesXml.getElementsByTagName("entity");
			
		for (int i = 0; i < rankedList.getLength(); i++) {						
			AlchemyEntity entity = new AlchemyEntity();
			

			NodeList entityData = rankedList.item(i).getChildNodes();
			
			for (int j = 0; j < entityData.getLength(); j++) {	
				Node node = entityData.item(j);
				
				if (node.getNodeName().equals("text"))
					entity.text = node.getTextContent();
				else if (node.getNodeName().equals("type"))
					entity.type = node.getTextContent();	
				else if (node.getNodeName().equals("relevance"))
					entity.relevance = Double.valueOf(node.getTextContent());	
				else if (node.getNodeName().equals("count"))
					entity.count = Integer.valueOf(node.getTextContent());
			}			
			entityList.add(entity);
		}
		return entityList;
	}	
	
	public ArrayList<AlchemyResult> getRankedConcepts(String url) throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
		ArrayList<AlchemyResult> conceptList = new ArrayList<>();
		
		Document urlRankedConceptsXml = null;
		
			urlRankedConceptsXml = alchemyObj.URLGetRankedConcepts(url);	
		
		
//		System.out.println(Util.getStringFromDocument(urlRankedConceptsXml));
		
		NodeList rankedList = urlRankedConceptsXml.getElementsByTagName("concept");
			
		for (int i = 0; i < rankedList.getLength(); i++) {						
			AlchemyConcept concept = new AlchemyConcept();
			NodeList entityData = rankedList.item(i).getChildNodes();
			
			for (int j = 0; j < entityData.getLength(); j++) {	
				Node node = entityData.item(j);
				
				if (node.getNodeName().equals("text"))
					concept.text = node.getTextContent();
				else if (node.getNodeName().equals("relevance"))
					concept.relevance = Double.valueOf(node.getTextContent());
			}			
			conceptList.add(concept);
		}
		return conceptList;
	}

	public ArrayList<AlchemyResult> getRankedKeywords(String url) throws XPathExpressionException, IOException, SAXException, ParserConfigurationException {
		ArrayList<AlchemyResult> keywordList = new ArrayList<>();
		Document urlRankedKeywordsXml = alchemyObj.URLGetRankedKeywords(url);
		NodeList rankedList = urlRankedKeywordsXml.getElementsByTagName("keyword");
		
		for (int i = 0; i < rankedList.getLength(); i++) {						
			AlchemyKeyword result = new AlchemyKeyword();
			NodeList entityData = rankedList.item(i).getChildNodes();
			
			for (int j = 0; j < entityData.getLength(); j++) {	
				Node node = entityData.item(j);
				
				if (node.getNodeName().equals("text"))
					result.text = node.getTextContent();
			
				else if (node.getNodeName().equals("relevance"))
					result.relevance = Double.valueOf(node.getTextContent());	
			}			
			keywordList.add(result);
		}
		
		return keywordList;
	}	
	
	
    public static String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }

    }
}

