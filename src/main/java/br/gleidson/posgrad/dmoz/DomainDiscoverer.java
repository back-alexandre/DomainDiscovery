package br.gleidson.posgrad.dmoz;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import br.gleidson.posgrad.ConceptInstance;
import br.gleidson.posgrad.FreebaseResultDomainManager;
import br.gleidson.posgrad.FreebaseResultTypeManager;
import br.gleidson.posgrad.alchemy.AlchemyHandler;
import br.gleidson.posgrad.alchemy.result.AlchemyResult;
import br.gleidson.posgrad.freebase.FreebaseHandler;
import br.gleidson.posgrad.freebase.result.FreebaseResult;
import br.gleidson.posgrad.utility.FileLogger;
import br.gleidson.posgrad.utility.Util;

public class DomainDiscoverer {
	
	private AlchemyHandler alchemyHandler = null;
	private FreebaseHandler freebaseHandler = null;
	private ArrayList<FreebaseResult> freebaseResultList = new ArrayList<>();
	private FreebaseResultDomainManager resultDomainManager = null;
	private ArrayList<Object> addedTopicList;
	public String domain = null;
	private String url;
	private FreebaseResultTypeManager resultTypeManager;
	public boolean mainConceptError = false;
	public static FileLogger log = null;
	public static String LOG_DIR = null;
	
	public static void main(String[] args) throws Exception {
		String url = "";
		String logDir = "";
		if (args.length == 0) {
			url = JOptionPane.showInputDialog("Informe a URL para análise:", "http://www.ufsc.br");
			logDir = JOptionPane.showInputDialog("Informe o diretório de log:");
		} else if (args.length < 2) {
			System.out.println("Argumento 1: URL para análise\nArgumento 2: Diretório de log");
			return;
		} 

		if (args.length > 0) {
			url = args[0];
		} 
		if (args.length > 1) {
			logDir = args[1];
		}
		if (args.length > 2) {
			System.out.println("Ignorando outros argumentos.");
		}

		criaDirSeNaoExiste(logDir);
		
		DomainDiscoverer dd3 = new DomainDiscoverer(logDir);
		System.out.println(dd3.getDomainFrom(url));
		return;
	}

	private static void criaDirSeNaoExiste(String logDir) {
		try {
			File dir = new File(logDir);
			if (dir.exists()) {
				System.out.println("Diretórios já existem.");
				return;
			}
			dir.mkdirs();
			System.out.println("Diretórios criados.");
		} catch (Exception e) {
			System.out.println("Erro na hora de criar o diretórios.");
		}
	}
	public void deleteLogFile() throws IOException {
		log.close();
		File file = new File(getLogFileName());
		file.delete();	
	}

	public DomainDiscoverer(String logDir) throws FileNotFoundException, IOException {
		LOG_DIR = logDir;
		alchemyHandler = new AlchemyHandler();
		freebaseHandler = FreebaseHandler.getInstance();	
		new FreebaseResultTypeManager();
		resultDomainManager = new FreebaseResultDomainManager();
		addedTopicList = new ArrayList<>();
	}

	public String getDomainFrom(String url) throws Exception {
//		System.out.println("INICIANDO "+url);
		this.url = url;
		log = new FileLogger(getLogFileName());
		log.writeLine("\nINICIANDO "+url); 
		freebaseResultList.addAll(getTopicsFromURL(url));			
	
		if (freebaseResultList.size() == 0) {
			 log.cleanWriteLine("Sem resultados na base de conhecimento...");
			 return null;
		}		
		
		try {
			getMainConcept();
		} catch (Exception e) {
			log.cleanWriteLine("\n[ERRO AO ENCONTRAR MAIN CONCEPT]\n");
			log.cleanWriteLine(e.getMessage());
			StackTraceElement[] stackTrace = e.getStackTrace();
			for (StackTraceElement stackTraceElement : stackTrace) {
				log.cleanWriteLine(stackTraceElement.toString());
			}
			this.mainConceptError  = true;
		}
	
		log.cleanWriteLine(" ");	
		domain = resultDomainManager.calculateDomainRelevance(freebaseResultList, log);		
		log.cleanWriteLine("\n[DOMAIN]: "+domain+"\n");
		return domain;
	}

	private void getMainConcept() throws IOException {
		this.resultTypeManager = new FreebaseResultTypeManager();	
		
		/**
		 * Calcula afinidades
		 */
		resultTypeManager.buildAffinityLists(freebaseResultList, log);
		
		for (FreebaseResult result : freebaseResultList) {
			 log.cleanWriteLine("\n["+result.name + "] AFFINITY TOTAL = "+result.getQtTypeAffinity()+" | "+resultTypeManager.getTypeWeightFrom(result)+" | "+result.getPeAffinityPoints()+"% ");			
			 log.cleanWriteLine(result.percentAffinityList.toString());
			 log.cleanWriteLine(result.quantityAffinityList.toString());
		}		
		
		//calcula media de afinidade
		float affinityMedia = resultTypeManager.calculateAffinityMedia(freebaseResultList);
		
		 log.cleanWriteLine("\n[AFINITY QT MEDIA] = "+affinityMedia);
				
		ArrayList<FreebaseResult> newResultList = new ArrayList<>();
		
		//imrpime topicos com afinidade maior q a media
		if (freebaseResultList.size() > 2) {
			newResultList = resultTypeManager.getResultListAboveAffinityMedia(freebaseResultList);
			log.cleanWriteLine("\n[MAIN TOPICS]:\n");
			for (FreebaseResult freebaseResult : newResultList) {
				 log.cleanWriteLine(freebaseResult.toString());
				 log.cleanWriteLine("");
			}
		} 
		
					
		FreebaseResult mainConcept = null; 
		
		if (newResultList != null && newResultList.size() == 1)
			mainConcept = newResultList.get(0);
		else  if (affinityMedia > 0) 
				mainConcept = resultTypeManager.getMainConceptBasedOndPeAffinity(newResultList);

		log.cleanWriteLine("[MAIN CONCEPT]: "+mainConcept);
				
	}

	public String getLogFileName() {
		String fileName = Util.getValidFilenameFrom(this.url);
		if (fileName.length() > Constants.MAX_FILENAME_SIZE)
			fileName = fileName.substring(Constants.MAX_FILENAME_SIZE);
		
		String logfile = LOG_DIR+"\\"+fileName+"_log.txt";
		System.out.println(logfile);
		return logfile;
	}

	public ArrayList<FreebaseResult> getTopicsFromURL(String url) throws IOException, XPathExpressionException, SAXException, ParserConfigurationException {
		ArrayList<ConceptInstance> instances = new ArrayList<>();
		ArrayList<AlchemyResult> rankedConcepts = alchemyHandler.getRankedConcepts(url);		
		for (AlchemyResult alchemyResult : rankedConcepts) {
			ConceptInstance instance = new ConceptInstance();
			instance.text = alchemyResult.text;			
			instances.add(instance);			
		}
		ArrayList<FreebaseResult> topicList = freebaseHandler.getFreebaseTopicsFrom(instances, log);
		ArrayList<FreebaseResult> resultList = new ArrayList<>();
		for (FreebaseResult freebaseResult : topicList) {
			if (addedTopicList.contains(freebaseResult.name)) continue;
//			freebaseResult.masterRelatedTopicList = freebaseHandler.queryCommonTopicsMasterRelatedToName(freebaseResult.name);
			resultList.add(freebaseResult);
			addedTopicList.add(freebaseResult.name);
		}
		return resultList;
	}
	
	/**
	 * Retorna, em percentuais, o número de tópicos encontrados relacionados com o domínio fornecido.
	 * @return
	 */
	public Float getDomainFrequencyPercent(String domain) {
		try {
			return (resultDomainManager.mapDomainTopicPresence.get(domain)*100)/freebaseResultList.size();
		} catch (NullPointerException np) {
			return 0f;
		}
	}

	public void closeLog() throws IOException {
		log.close();
	}

	public int getCorrectDomainPosition(String currentDomain) {
		return resultDomainManager.getDomainPosition(currentDomain);
	}
}
