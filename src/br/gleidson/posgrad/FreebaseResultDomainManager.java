package br.gleidson.posgrad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import br.gleidson.posgrad.dmoz.Constants;
import br.gleidson.posgrad.dmoz.EnumDomain;
import br.gleidson.posgrad.freebase.result.FreebaseResult;
import br.gleidson.posgrad.freebase.result.FreebaseType;
import br.gleidson.posgrad.utility.FileLogger;
import br.gleidson.posgrad.utility.Util;

public class FreebaseResultDomainManager {
	
	public HashMap<String, HashMap<String, Integer>> mapDomainTypeQuantity = null;
	public HashMap<String, Float> mapFinalDomainRelevance = new HashMap<>();
	public HashMap<String, Float> mapDomainTopicPresence;
	private HashMap<String, Float> mapDomainTopicInverseRelevance;
	private HashMap<String, Float> mapDomainRelevance;
	

	/**
	 * MONTA UMA LISTA DE DOMINIIOS QUE TODOS OS TOPICOS TEM EM COMUM
	 * @param newResultList
	 * @return
	 */
	public ArrayList<String> findCommonDomains(ArrayList<FreebaseResult> newResultList) {
		ArrayList<String> commonDomainList = new ArrayList<>();
		HashMap<String, Integer> mapCommomDomain = new HashMap<>();
		for (FreebaseResult freebaseResult : newResultList) {
			ArrayList<String> resultDomainList = new ArrayList<>();
			for (FreebaseType type : freebaseResult.typeList) {
				if (resultDomainList.contains(type.domain)) 
					continue;
				resultDomainList.add(type.domain);
				if (mapCommomDomain.containsKey(type.domain)) 
					mapCommomDomain.put(type.domain, mapCommomDomain.get(type.domain)+1);
				else
					mapCommomDomain.put(type.domain, 1);
			}
		}		
		for (Entry<String, Integer> entry : mapCommomDomain.entrySet()) {
			if (entry.getValue().intValue() == newResultList.size())
				commonDomainList.add(entry.getKey());
		}
		return commonDomainList;
	}

	public HashMap<String, Integer> countTopicsByDomain(ArrayList<String> commonDomainsList, ArrayList<FreebaseResult> freebaseResultList) {
		HashMap<String, Integer> mapDomainTopicsCount = new HashMap<>();
		for (String domain : commonDomainsList) {
			int count = 0;
			for (FreebaseResult result : freebaseResultList) {
				boolean hasThisDomain = false;
				for (FreebaseType type : result.typeList) {
					if (type.domain.equals(domain)) {
						hasThisDomain = true;
						break;
					}						
				}
				if (hasThisDomain)
					count++;
			}
			mapDomainTopicsCount.put(domain, count);
		}
		return mapDomainTopicsCount;
	}

	public HashMap<String, HashMap<String, Integer>> buildDomainTypeList(ArrayList<FreebaseResult> freebaseResultList) {
		mapDomainTypeQuantity = new HashMap<>();
		for (FreebaseResult freebaseResult : freebaseResultList) {
			for (FreebaseType type : freebaseResult.typeList) {
				if (mapDomainTypeQuantity.containsKey(type.domain)) {
					HashMap<String, Integer> typeList = mapDomainTypeQuantity.get(type.domain);
					if (typeList.containsKey(type.name)) {
						typeList.put(type.name, typeList.get(type.name)+1);
					} else {
						typeList.put(type.name,1);	
					}
				} else {
					HashMap<String, Integer> typeList = new HashMap<>();
					typeList.put(type.name, 1);
					mapDomainTypeQuantity.put(type.domain, typeList);
				}
			}
		}	
		return mapDomainTypeQuantity;
	}

	
	public FreebaseResult findMainConceptByDomainPopularity(ArrayList<FreebaseResult> newResultList) {
		HashMap<String, Float> mapDomainTopicCount = new HashMap<>();
		//CONTA QUANTOS TÓPICOS CADA DOMÍNIO ESTÁ PRESENTE		
		for (FreebaseResult freebaseResult : newResultList) {
			ArrayList<FreebaseType> types = freebaseResult.typeList;
			ArrayList<String> topicDomains = new ArrayList<>();
			for (FreebaseType freebaseType : types) {
				if (topicDomains.contains(freebaseType.domain)) continue;
				if (mapDomainTopicCount.containsKey(freebaseType.domain)) {					
					mapDomainTopicCount.put(freebaseType.domain, mapDomainTopicCount.get(freebaseType.domain)+1);
				} else {
					mapDomainTopicCount.put(freebaseType.domain, 1f);
				}
				topicDomains.add(freebaseType.domain);
			}			
		}
		Entry<String, Float> mainDomain = Util.getGreaterValue(mapDomainTopicCount);
		//SE TEM MAIS DE 1 TOPICO PRA ALGUM DOMINIO MONTA LISTA DE TOPICOS DO DOMINIO
		//SENAO VOLTA O PRIMEIRO RESULT DA LISTA
		if (mainDomain.getValue() > 0) {
			FreebaseResult mainConcept = null;
			ArrayList<FreebaseResult> domainRelatedTopics = new ArrayList<>();			
			for (FreebaseResult freebaseResult : newResultList) {
				ArrayList<FreebaseType> types = freebaseResult.typeList;
				for (FreebaseType freebaseType : types) {
					if (freebaseType.domain.equals(mainDomain.getKey())) { 
						domainRelatedTopics.add(freebaseResult);
						break;
					}
				}
			}
			
			int mainDomainPosition = 999999999;
			//DEPOIS PEGA O TOPICO QUE TEM A MENOR POSICAO DO DOMINIO EM COMUM NOS TIPOS			
			for (FreebaseResult freebaseResult : domainRelatedTopics) {
				ArrayList<FreebaseType> types = freebaseResult.typeList;				
				int resultDomainPosition = 0;
				for (FreebaseType freebaseType : types) {
					resultDomainPosition++;
					if (freebaseType.domain.equals(mainDomain.getKey())) {						
						if (resultDomainPosition < mainDomainPosition) {
							mainConcept = freebaseResult;
							mainDomainPosition =  resultDomainPosition;
						} else if (resultDomainPosition == mainDomainPosition) {
							if (freebaseResult.typeList.size() < mainConcept.typeList.size()) {
								mainConcept = freebaseResult;
								mainDomainPosition =  resultDomainPosition;	
							}
						}
						break;	
					}						
				}
			}	
			return mainConcept;
		} else 
			return newResultList.get(0);
	}

	/**
	 * DEVE CONTAR EM QUANTOS TOPICOS O DOMINIO APARECE
	 * E GUARDAR A SOMA DAS POSICOES DO DOMINIO NOS TOPICOS
	 * @param newResultList
	 * @param log 
	 * @return 
	 * @throws Exception 
	 */
	@SuppressWarnings("unchecked")
	public String calculateDomainRelevance(ArrayList<FreebaseResult> newResultList, FileLogger log) throws Exception {
		//CONTA EM QUANTOS TOPICOS O DOMINIO APARECE
		mapDomainTopicPresence = new HashMap<>();
		//SOMATORIO DAS 1A POSICAO DE CADA DOMINIO EM CADA TOPICO
		mapDomainTopicInverseRelevance = new HashMap<>();
		
		mapDomainRelevance = new HashMap<>();
				
		//CONTA AS POSICOES E QUANTOS TOPICOS O DOMINIO APARECE
		for (FreebaseResult freebaseResult : newResultList) {
			ArrayList<FreebaseType> types = freebaseResult.typeList;		
			ArrayList<String> resultDomains = new ArrayList<>();
			float cont = 0;
			for (FreebaseType freebaseType : types) {
				if (resultDomains.contains(freebaseType.domain))
					continue;
				if (mapDomainTopicPresence.containsKey(freebaseType.domain)) {
					mapDomainTopicPresence.put(freebaseType.domain, mapDomainTopicPresence.get(freebaseType.domain)+1);
				} else {
					mapDomainTopicPresence.put(freebaseType.domain, (float) 1);
				}
				if (mapDomainTopicInverseRelevance.containsKey(freebaseType.domain)) {
					mapDomainTopicInverseRelevance.put(freebaseType.domain, mapDomainTopicInverseRelevance.get(freebaseType.domain)+cont);
				} else {
					mapDomainTopicInverseRelevance.put(freebaseType.domain, cont);
				}
				resultDomains.add(freebaseType.domain);
				cont++;
			}
		}
		log.cleanWriteLine("TOTAL DE TOPICOS: "+newResultList.size());	
		mapDomainTopicPresence = (HashMap<String, Float>) Util.sortMapDesc(mapDomainTopicPresence);
		mapDomainTopicInverseRelevance = (HashMap<String, Float>) Util.sortMapAsc(mapDomainTopicInverseRelevance);
				
		Set<Entry<String, Float>> x = mapDomainTopicPresence.entrySet();
		log.cleanWriteLine("\nPRESENCE FREQUENCY:");		 
		for (Entry<String, Float> domain : x) {	
//			Integer relevance = pegaposicao(domain.getKey(), x);
			//RELEVANCE É A POSIÇÃO NO ARRAY + QUANTAS VEZES APARECE ONDE O QUE IMPORTA É O VALOR
			Float relevance = pegaposicaoporvalor(domain.getKey(), x);
			if (mapDomainRelevance.containsKey(domain.getKey())) {
				mapDomainRelevance.put(domain.getKey(), mapDomainRelevance.get(domain.getKey())+relevance);
			} else {
				mapDomainRelevance.put(domain.getKey(), relevance);
			}
			 log.cleanWriteLine(" "+domain.getKey()+"= "+domain.getValue()+"("+getDomainPe(newResultList, domain)+"%) ");
		}
		 log.cleanWriteLine("\nCONCEPT POSITION SUM");
		//PRA RELEVANCIA SOMA A DISTANCIA DAS POSICOES DE CADA DOMINIO NO ARRAYS DOS CONCEITOS  
		 x = mapDomainTopicInverseRelevance.entrySet();
		for (Entry<String, Float> domain : x) {	
			Float relevance = (float)pegaposicao(domain.getKey(), x);
//			Integer relevance = pegaposicaoporvalor(domain.getKey(), x);
			if (mapDomainRelevance.containsKey(domain.getKey())) {
				mapDomainRelevance.put(domain.getKey(), mapDomainRelevance.get(domain.getKey())+relevance);
			} else {
				mapDomainRelevance.put(domain.getKey(), relevance);
			}
//			System.out.print(" "+domain.getKey()+"= "+domain.getValue());
		}
		
		mapDomainRelevance = (HashMap<String, Float>) Util.sortMapAsc(mapDomainRelevance);
		
		x = mapDomainRelevance.entrySet();
		for (Entry<String, Float> domain : x) {	
			//TOTAL DE TOPICOS - PRESENCA DO DOMINIO + POSICAO DO DOMINIO NA LISTA DE RELEVANCIA = INVERSA RELEVANCIA FINAL
//			Integer relevance = (newResultList.size()-mapDomainTopicPresence.get(domain.getKey()))+pegaposicao(domain.getKey(), x);
			Float relevance = (mapDomainRelevance.get(domain.getKey())/mapDomainTopicPresence.get(domain.getKey()));
			mapFinalDomainRelevance.put(domain.getKey(), relevance);
		}
				
		 log.cleanWriteLine("\nMAP DOMAIN POSITION"+mapDomainTopicInverseRelevance);		
				
		 log.cleanWriteLine("\nPOSITION + FREQUENCY: "+mapDomainRelevance+"\n");
		mapFinalDomainRelevance = (HashMap<String, Float>) Util.sortMapAsc(mapFinalDomainRelevance);
		 log.cleanWriteLine("MAP FINAL RELEVANCE: "+mapFinalDomainRelevance.toString());
		
		String dominio = null;
		
		if (Constants.USE_DOMAIN_LIST) {
			dominio = getSelectedDomain(mapFinalDomainRelevance.entrySet());
			return dominio;
		}
			
		
		//PEGA O MAIOR POR DESEMPATE ATE SAIR O PRINCIPAL
		x = mapFinalDomainRelevance.entrySet();
		Float menor = 0f;
		ArrayList<Entry<String, Float >> listaTop = new ArrayList<>();
		for (Entry<String, Float> domain : x) {	
			if (menor == 0) {
				menor = domain.getValue();
				listaTop.add(domain);
			} else {
				if (domain.getValue().equals(menor)) 
					listaTop.add(domain);
			}
		}	

		ArrayList<Entry<String, Float >> listaTop2 = new ArrayList<>();
		if (listaTop.size() > 1) {
			Float maior = 0f;
			 log.cleanWriteLine("Desempate por presença...");
			for (Entry<String, Float> entry : listaTop) {
				Float presenca = mapDomainTopicPresence.get(entry.getKey());
				if (presenca > maior)
					maior = presenca;
			}
			for (Entry<String, Float> entry : listaTop) {
				if (mapDomainTopicPresence.get(entry.getKey()).equals(maior)) 
					listaTop2.add(entry);
			}
			
			if (listaTop2.size() > 1) {
				 log.cleanWriteLine("Desempate por relevancia inversa");
				int menorPosicao = 9999;
				listaTop = new ArrayList<>();
				for (Entry<String, Float> entry : listaTop2) {
					Integer posicao = pegaposicao(entry.getKey(), mapDomainTopicInverseRelevance.entrySet());
					if (posicao <= menorPosicao) {
						menorPosicao = posicao;
					}
				}
				for (Entry<String, Float> entry : listaTop2) {
					Integer posicao = pegaposicao(entry.getKey(), mapDomainTopicInverseRelevance.entrySet());
					if (posicao <= menorPosicao) {
						listaTop.add(entry);
					}
				}			
				if (listaTop.size() == 1)
					dominio = getSelectedDomain(listaTop);
			} else {
				dominio = getSelectedDomain(listaTop2);
			}
		} else {
			dominio = getSelectedDomain(listaTop);
		}
				
		if (dominio == null ) 
			throw new Exception ("NAO DEFINIU O DOMINIO PRECISAMNENTE!");
			
		return dominio;
		
	}

	private String getSelectedDomain(Set<Entry<String, Float>> entrySet) {
		Entry<String, Float> oneEntry = null;
		for (Entry<String, Float> entry : entrySet) {
			if (EnumDomain.contains(entry.getKey())) {
				return entry.getKey();
			}
			oneEntry = entry;
		}		
		return (String) oneEntry.getKey();
	}

	public String getSelectedDomain(ArrayList<Entry<String, Float>> listaTop) {
		if (Constants.USE_DOMAIN_LIST) {
			int index = 0;
			ArrayList<Entry<String, Float>> lista = listaTop;
			for (Entry<String, Float> entry : lista) {
				if (EnumDomain.contains(entry.getKey())) {
					return listaTop.get(index).getKey();
				}
				index++;
			}
		} 
		return listaTop.get(0).getKey();
	}

	private Float pegaposicaoporvalor(String key, Set<Entry<String, Float>> x) {
		Float cont = 0f;
		Float valorProcurado = 0f;
		ArrayList<Float> valoresAnteriores = new ArrayList<>();
		for (Entry<String, Float> entry : x) {		
			if (entry.getKey().equals(key))
				valorProcurado = entry.getValue();				
		}
		for (Entry<String, Float> entry : x) {
			if (entry.getKey().equals(key))
				return cont;
			if (valoresAnteriores.contains(entry.getValue()))
				continue;
			valoresAnteriores.add(entry.getValue());
			if (entry.getValue().equals(valorProcurado))
				continue;
			cont++;
		}
		return cont;
	}
	
	private Integer pegaposicao(String key, Set<Entry<String, Float>> x) {
		int cont = 0;
		for (Entry<String, Float> entry : x) {
			cont++;
			if (entry.getKey().equals(key))
				return cont;				
		}
		return cont;
	}
	
	private Integer pegaPosicaoDominioUsandoLista(String key, Set<Entry<String, Float>> x) {
		int cont = 0;
		for (Entry<String, Float> entry : x) {
			if (!EnumDomain.contains(entry.getKey()))
				continue;
			cont++;
			if (entry.getKey().equals(key))
				return cont;				
		}
		return cont;
	}

	public Float getDomainPe(ArrayList<FreebaseResult> newResultList, Entry<String, Float> entry) {
		return (entry.getValue()*100)/newResultList.size();
	}

	@SuppressWarnings("unchecked")
	public String getMainDomain(FreebaseResult specificConcept, FileLogger log) throws IOException {
		ArrayList<FreebaseType> typeList = specificConcept.typeList;
		HashMap<String, Integer> mapDomainFinalDomainRelevancePosition = new HashMap<>();
		for (FreebaseType freebaseType : typeList) {
			if (mapDomainFinalDomainRelevancePosition.containsKey(freebaseType.domain)) 
				continue;
			else
				mapDomainFinalDomainRelevancePosition.put(freebaseType.domain, pegaposicao(freebaseType.domain, mapFinalDomainRelevance.entrySet()));			
		}
		mapDomainFinalDomainRelevancePosition = (HashMap<String, Integer>) Util.sortMapAsc(mapDomainFinalDomainRelevancePosition);
		String mainDomain = (String) mapDomainFinalDomainRelevancePosition.keySet().toArray()[0];
		log.cleanWriteLine("MAIN DOMAIN: "+mainDomain);
		return  mainDomain;
	}

	public String getFinalMainDomain(String mainDomain, String domain, FileLogger log) throws IOException {
		int mainDomainPts = 0;
		int domainPts = 0;
		
		if (mainDomain == null)
			return domain;
		if (domain == null)
			return mainDomain;
		
		if (mainDomain.equals(domain))
			return domain;
		
		if (pegaposicao(mainDomain, mapDomainTopicPresence.entrySet()) < pegaposicao(domain, mapDomainTopicPresence.entrySet())) {
			mainDomainPts++;
		} else if (pegaposicao(mainDomain, mapDomainTopicPresence.entrySet()) > pegaposicao(domain, mapDomainTopicPresence.entrySet())) {
			domainPts++;
		}
		
		if (pegaposicao(mainDomain, mapDomainTopicInverseRelevance.entrySet()) < pegaposicao(domain, mapDomainTopicInverseRelevance.entrySet())) {
			mainDomainPts++;
		} else if (pegaposicao(mainDomain, mapDomainTopicInverseRelevance.entrySet()) > pegaposicao(domain, mapDomainTopicInverseRelevance.entrySet())) {
			domainPts++;
		}
		
		if (pegaposicao(mainDomain, mapDomainRelevance.entrySet()) < pegaposicao(domain, mapDomainRelevance.entrySet())) {
			mainDomainPts++;
		} else if (pegaposicao(mainDomain, mapDomainRelevance.entrySet()) > pegaposicao(domain, mapDomainRelevance.entrySet())) {
			domainPts++;
		}
		
		if (pegaposicao(mainDomain, mapFinalDomainRelevance.entrySet()) < pegaposicao(domain, mapFinalDomainRelevance.entrySet())) {
			mainDomainPts++;
		} else if (pegaposicao(mainDomain, mapFinalDomainRelevance.entrySet()) > pegaposicao(domain, mapFinalDomainRelevance.entrySet())) {
			domainPts++;
		}
		
		if (mainDomainPts > domainPts) {
			return mainDomain;
		} else if (mainDomainPts < domainPts) {
			return domain;
		} else {
			log.cleanWriteLine("!!!! DECIDIDO POR EMPATE !!!!");

			if (pegaposicao(mainDomain, mapFinalDomainRelevance.entrySet()) < pegaposicao(domain, mapFinalDomainRelevance.entrySet())) {
				return mainDomain;
			} else if (pegaposicao(mainDomain, mapFinalDomainRelevance.entrySet()) > pegaposicao(domain, mapFinalDomainRelevance.entrySet())) {
				return domain;
			}
		}
		return null;			
	}

	public int getDomainPosition(String currentDomain) {
		if (Constants.USE_DOMAIN_LIST) 
			return pegaPosicaoDominioUsandoLista(currentDomain, mapFinalDomainRelevance.entrySet());
		return pegaposicao(currentDomain, mapFinalDomainRelevance.entrySet());
	}
}
