package br.gleidson.posgrad.freebase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import br.gleidson.posgrad.ConceptInstance;
import br.gleidson.posgrad.freebase.mql.MqlQuery;
import br.gleidson.posgrad.freebase.mql.MqlQueryFactory;
import br.gleidson.posgrad.freebase.result.FreebaseResult;
import br.gleidson.posgrad.freebase.result.FreebaseType;
import br.gleidson.posgrad.utility.FileLogger;
import br.gleidson.posgrad.wordnet.WordNetSynset;
import br.gleidson.posgrad.wordnet.WordNetWrapper;

public class FreebaseHandler {
	
	private static final String FREEBASE_KEY = "AIzaSyDscR_BtbHo1ObJ0CyvWbP6wokXHunXGxc";
	private static final String FREEBASE_URL = "https://www.googleapis.com/freebase/v1/mqlread";
	private GenericUrl url;
	private NetHttpTransport httpTransport;
	private HttpRequestFactory requestFactory;
	private JsonParser parser;
	public ArrayList<FreebaseResult> conceptUnderAverageList = null;
	public ArrayList<FreebaseResult> entityUnderAverageList = null;
	public ArrayList<FreebaseResult> keywordUnderAverageList = null;
	private HashMap<String, ArrayList<FreebaseResult>> freebaseResultCache;
	public static int cacheAproveitada = 0;
	private static FreebaseHandler freebaseHandler = null;

	private FreebaseHandler() {
		httpTransport = new NetHttpTransport();
		requestFactory = httpTransport.createRequestFactory();
		parser = new JsonParser();		
		conceptUnderAverageList = new ArrayList<>();
		entityUnderAverageList = new ArrayList<>();
		keywordUnderAverageList = new ArrayList<>();
		freebaseResultCache = new HashMap<String, ArrayList<FreebaseResult>>();
	}
	
	public static FreebaseHandler getInstance() {
		if (freebaseHandler == null) {
			freebaseHandler = new FreebaseHandler();		
		}
		return freebaseHandler;
	}
	
	public ArrayList<FreebaseResult> queryCommonTypesAndDomainsByName(String name) throws IOException {
		JsonArray queryResult = executeQuery(MqlQueryFactory.queryCommonTypesAndDomainsByName(name));
		ArrayList<FreebaseResult> arrayList = new ArrayList<>();
		for (JsonElement jsonResult : queryResult) {
			FreebaseResult result = new FreebaseResult();
			JsonObject jsonObject = jsonResult.getAsJsonObject();
			result.name = jsonObject.get("name").getAsString();
			if (result.name.isEmpty())
				continue;
			JsonArray typeArray = jsonObject.get("type").getAsJsonArray();
			for (JsonElement jsonElement : typeArray) {
				String type =((JsonObject) jsonElement).get("name").getAsString();
				String domain =((JsonObject)((JsonObject) jsonElement).get("domain")).get("name").getAsString();
				result.typeList.add(new FreebaseType(type, domain));
				
			}
			arrayList.add(result);
		}
		ArrayList<FreebaseResult> newList = groupResultTypesByName(arrayList);
		verifyConceptThatAreDomains(newList);
		return newList;
	}
	 
	public ArrayList<FreebaseResult> queryCommonTopicsMasterRelatedToName(String name) throws IOException {
		JsonArray queryResult = executeQuery(MqlQueryFactory.queryCommonTopicsMasterRelatedToName(name));
		ArrayList<FreebaseResult> arrayList = new ArrayList<>();
		for (JsonElement jsonResult : queryResult) {			
			JsonObject relationObject = jsonResult.getAsJsonObject();			
			JsonArray masterRelationArray = relationObject.get("/type/reflect/any_master").getAsJsonArray();
			for (JsonElement jsonElement : masterRelationArray) {
				try {
					JsonObject masterObject = jsonElement.getAsJsonObject();				
					FreebaseResult result = new FreebaseResult();
					JsonArray typeArray = masterObject.get("type").getAsJsonArray();				
					result.name = masterObject.get("name").getAsString();								
					for (JsonElement jsonElement2 : typeArray) {
						String type =((JsonObject) jsonElement2).get("name").getAsString();
						String domain =((JsonObject)((JsonObject) jsonElement2).get("domain")).get("name").getAsString();
						result.typeList.add(new FreebaseType(type, domain));
						
					}		
					arrayList.add(result);
				} catch (Exception e) {
					continue;
				}
			}						
		}
		ArrayList<FreebaseResult> newList = groupTopicsAndTypes(arrayList);
		verifyConceptThatAreDomains(newList);
		return newList;
	}
	
	public ArrayList<FreebaseResult> queryCommonTopicsReverseRelatedToName(String name) throws IOException {
		JsonArray queryResult = executeQuery(MqlQueryFactory.queryCommonTopicsReverseRelatedToName(name));
		ArrayList<FreebaseResult> arrayList = new ArrayList<>();
		for (JsonElement jsonResult : queryResult) {			
			JsonObject relationObject = jsonResult.getAsJsonObject();			
			JsonArray masterRelationArray = relationObject.get("/type/reflect/any_reverse").getAsJsonArray();
			for (JsonElement jsonElement : masterRelationArray) {
				try {
					JsonObject masterObject = jsonElement.getAsJsonObject();				
					FreebaseResult result = new FreebaseResult();
					JsonArray typeArray = masterObject.get("type").getAsJsonArray();				
					result.name = masterObject.get("name").getAsString();								
					for (JsonElement jsonElement2 : typeArray) {
						String type =((JsonObject) jsonElement2).get("name").getAsString();
						String domain =((JsonObject)((JsonObject) jsonElement2).get("domain")).get("name").getAsString();
						result.typeList.add(new FreebaseType(type, domain));
						
					}		
					arrayList.add(result);
				} catch (Exception e) {
					continue;
				}
			}						
		}
		ArrayList<FreebaseResult> newList = groupTopicsAndTypes(arrayList);
		verifyConceptThatAreDomains(newList);
		return newList;
	}
	
	private ArrayList<FreebaseResult> groupTopicsAndTypes(ArrayList<FreebaseResult> arrayList) {
		ArrayList<FreebaseResult> resultList = new ArrayList<>();
		for (FreebaseResult freebaseResult : arrayList) {
			if (resultList.contains(freebaseResult)) {
				FreebaseResult mergedResult = resultList.get(resultList.indexOf(freebaseResult));		
				mergeTypeLists(mergedResult.typeList, freebaseResult.typeList);
			} else {
				resultList.add(freebaseResult);
			}
		}
		return resultList;		
	}

	private ArrayList<FreebaseResult> groupResultTypesByName(ArrayList<FreebaseResult> resultList) {
		FreebaseResult newResult = new FreebaseResult();		
		ArrayList<FreebaseResult> newResultList = new ArrayList<>();
		ArrayList<FreebaseType> newTypelist = new ArrayList<>();
		for (FreebaseResult freebaseResult : resultList) {			
			if (freebaseResult.name.equalsIgnoreCase(newResult.name)) {
				mergeTypeLists(newTypelist, freebaseResult.typeList);
			} else {
				if (!newResultList.isEmpty()) {
					newResult.typeList = newTypelist;
					newResultList.add(newResult);
					newTypelist = new ArrayList<>();
					newResult = new FreebaseResult();
				}
				newResult.name = freebaseResult.name;
				mergeTypeLists(newTypelist, freebaseResult.typeList);
			}
		}
		newResult.typeList = newTypelist;
		newResultList.add(newResult);
		return newResultList;
	}

	private void mergeTypeLists(ArrayList<FreebaseType> newTypelist, ArrayList<FreebaseType> typeList) {
		for (int i = 0; i < typeList.size(); i++) {
			if (newTypelist.contains(typeList.get(i))) continue;
			newTypelist.add(typeList.get(i));
		}
	}

	private JsonArray executeQuery(MqlQuery mqlQuery) throws IOException {
		url = new GenericUrl(FREEBASE_URL);
		url.put("key", FREEBASE_KEY);	
		url.put("query", mqlQuery.query);
		com.google.api.client.http.HttpRequest request = requestFactory.buildGetRequest(url);
		com.google.api.client.http.HttpResponse httpResponse = request.execute();
		JsonObject response = (JsonObject)parser.parse(httpResponse.parseAsString());
		return (JsonArray)response.get("result");
	}
	
	private void verifyConceptThatAreDomains(ArrayList<FreebaseResult> freebaseResultList) {
		ArrayList<String> domainList = new ArrayList<>();		
		//cria lista de dominios
		for (FreebaseResult freebaseResult : freebaseResultList) {
			ArrayList<FreebaseType> resultTypes = freebaseResult.typeList;
			for (FreebaseType type : resultTypes) {
				if (!domainList.contains(type.domain)) {
					domainList.add(type.domain);
				}
			}
		}		
		//itera os conceitos para ver se algum é domínio
		for (FreebaseResult freebaseResult : freebaseResultList) {
			if (domainList.contains(freebaseResult.name)) {		
				freebaseResult.isDomain = true;
			}
		}		
	}


	public ArrayList<FreebaseResult> getFreebaseTopicsFrom(ArrayList<ConceptInstance> alchemyResultList, FileLogger log) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
		ArrayList<FreebaseResult> freebaseResults = new ArrayList<>();				
		for (ConceptInstance alchemyResult : alchemyResultList) {
			log.cleanWriteLine("\nAlchemy "+alchemyResult);			
			ArrayList<FreebaseResult> freebaseResult = null;
			if ((freebaseResult = freebaseResultCache.get(alchemyResult.text)) == null) {				
				freebaseResult = queryFreebaseInstances(alchemyResult,log);
			} else {
				cacheAproveitada++;
			}
			for (FreebaseResult result : freebaseResult) {
				if (result.name == null || result.name.isEmpty())
					continue;
				log.cleanWriteLine("\nFreebase Result ->"+result);
				freebaseResults.add(result);
			} 
		}	
		return freebaseResults;
	}

	public ArrayList<FreebaseResult> queryFreebaseInstances(ConceptInstance alchemyResult, FileLogger log)throws IOException {
		ArrayList<FreebaseResult> freebaseResult = null;
		WordNetWrapper wnw = WordNetWrapper.getInstance();
		freebaseResult = queryCommonTypesAndDomainsByName(alchemyResult.text);
		if (freebaseResult == null || freebaseResult.isEmpty()) {		
			WordNetSynset synset = wnw.query(alchemyResult.text);			
			ArrayList<String >synList = synset.synset;
			log.cleanWriteLine("\nWordNet Result ->"+synList);
			for (String syn : synList) {
				freebaseResult = queryCommonTypesAndDomainsByName(syn);
				if (freebaseResult != null) {
					break;
				}
			}
		}
		freebaseResultCache.put(alchemyResult.text, freebaseResult);
		return freebaseResult;
	}
	
	public int getCacheNumberOfObects() {
		return freebaseResultCache.size();
	}
	
	public int getCacheByteSize(){
		return freebaseResultCache.toString().getBytes().length;
	}
	
	public void flushCache() {
		freebaseResultCache = new HashMap<>();
	}

}
