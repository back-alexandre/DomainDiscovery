package br.gleidson.posgrad;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import br.gleidson.posgrad.freebase.result.FreebaseResult;
import br.gleidson.posgrad.freebase.result.FreebaseType;
import br.gleidson.posgrad.utility.FileLogger;
import br.gleidson.posgrad.utility.Util;

public class FreebaseResultTypeManager {
	
	public HashMap<String, Integer> mapTypeQuantity = null;

	public void buildAffinityLists(ArrayList<FreebaseResult> freebaseResultList, FileLogger log) throws IOException {
		if (mapTypeQuantity == null) 
			buildMapTypeQuantity(freebaseResultList);
		for(FreebaseResult freebaseResult : freebaseResultList) {
			buildTypeAffinityLists(freebaseResult, freebaseResultList, log);
		}
	}

	private void buildMapTypeQuantity(ArrayList<FreebaseResult> freebaseResultList) {
		mapTypeQuantity = new HashMap<>();
		for (FreebaseResult freebaseResult : freebaseResultList) {
			for (FreebaseType type : freebaseResult.typeList) {
				if (mapTypeQuantity.containsKey(type.name)) {
					mapTypeQuantity.put(type.name, mapTypeQuantity.get(type.name)+1);
				} else {
					mapTypeQuantity.put(type.name, 1);
				}
			}
		}		
	}

	private void buildTypeAffinityLists(FreebaseResult targetResult, ArrayList<FreebaseResult> freebaseResultList, FileLogger log) throws IOException {
		if (targetResult.percentAffinityList == null) 
			targetResult.percentAffinityList = new HashMap<>();
			
		if (targetResult.quantityAffinityList == null) 
			targetResult.quantityAffinityList = new HashMap<>();
		for (FreebaseResult resultFromList : freebaseResultList) {
			if (targetResult.equals(resultFromList)) continue;
			float typesInCommon = 0;
			float typesShared = 0;
			
			
			for(FreebaseType targetType : targetResult.typeList) {
				try {
					if (resultFromList.isTypeOf(targetType)) 
						typesInCommon++;				
					if (mapTypeQuantity.get(targetType.name) > 1)
						typesShared++;
				} catch (Exception e) {
					 log.cleanWriteLine("[WARN] EXCEPTION");
				}
			}
			
			if (typesShared > 0) {
				targetResult.quantityAffinityList.put(resultFromList.name, Math.round(typesInCommon));
				targetResult.percentAffinityList.put(resultFromList.name, (typesInCommon*100)/targetResult.typeList.size());
			}
		}
	}

	/*
	 * IMPORTANTE PORQUE FAZ A SOMA DE TODAS AS QUANTIDADES DOS TIPOS DO RESULTADO
	 */
	public Integer getTypeWeightFrom(FreebaseResult result) {
		Integer totalTypePoints = 0;
		for(FreebaseType type : result.typeList) {
			totalTypePoints += mapTypeQuantity.get(type.name);
		}
		return totalTypePoints;
	}

	public ArrayList<FreebaseResult> getResultListAboveAffinityMedia(ArrayList<FreebaseResult> freebaseResultList) {
		ArrayList<FreebaseResult> newResultList = new ArrayList<>();
		float affinityMedia = calculateAffinityMedia(freebaseResultList);
		for (FreebaseResult freebaseResult : freebaseResultList) {
			if (freebaseResult.getQtTypeAffinity() >= affinityMedia)
				newResultList.add(freebaseResult);
		}
		return newResultList;
	}

	public float calculateAffinityMedia(ArrayList<FreebaseResult> freebaseResultList) {
		float affinityMedia  = 0f;
		float diferenceMedia = 0;
		float previousAffinity = 0;
		for (FreebaseResult freebaseResult : freebaseResultList) {
			if (previousAffinity == 0) {
				previousAffinity = freebaseResult.getQtTypeAffinity();
			} else {
				diferenceMedia += previousAffinity - freebaseResult.getQtTypeAffinity();
				previousAffinity = freebaseResult.getQtTypeAffinity();
			}				
			affinityMedia += freebaseResult.getQtTypeAffinity();
		}		
		diferenceMedia /= freebaseResultList.size();
		affinityMedia /= freebaseResultList.size();
		if (diferenceMedia < 0) diferenceMedia *= -1;
		affinityMedia -= diferenceMedia;
		return affinityMedia;
	}
	
	public float calculateAffinityPeMedia(ArrayList<FreebaseResult> freebaseResultList) {
		float affinityPeMedia  = 0f;
		float diferenceMedia = 0;
		float previousAffinity = 0;
		for (FreebaseResult freebaseResult : freebaseResultList) {
			if (previousAffinity == 0) {
				previousAffinity = freebaseResult.getPeAffinityPoints();
			} else {
				diferenceMedia += previousAffinity - freebaseResult.getPeAffinityPoints();
				previousAffinity = freebaseResult.getPeAffinityPoints();
			}				
			affinityPeMedia += freebaseResult.getPeAffinityPoints();
		}		
		diferenceMedia /= freebaseResultList.size();
		affinityPeMedia /= freebaseResultList.size();
		if (diferenceMedia < 0) diferenceMedia *= -1;
		affinityPeMedia -= diferenceMedia;
		return Math.round(affinityPeMedia);
	}
	
	public ArrayList<FreebaseResult> getResultListAboveAveragePeAffinity(ArrayList<FreebaseResult> resultList, FileLogger log) throws IOException {
		ArrayList<FreebaseResult> newResultList = new ArrayList<>();
		float affinityPeMedia = calculateAffinityPeMedia(resultList);
		log.cleanWriteLine(" AFFINITY PE MEDIA: "+affinityPeMedia);
		if (affinityPeMedia > 0) {
			for (FreebaseResult freebaseResult : resultList) {
				if (freebaseResult.getPeAffinityPoints() > affinityPeMedia)
					newResultList.add(freebaseResult);
			}
			return newResultList;
		} else {
			return resultList;
		}		
	}

	public FreebaseResult getMainConceptBasedOndPeAffinity(ArrayList<FreebaseResult> freebaseResultList) throws IOException {		
		HashMap<String, Float> mapResultNamePeTotal = new HashMap<>();
		for (FreebaseResult freebaseResult : freebaseResultList) {			
			for (Entry<String, Float> conceptPeAffinity : freebaseResult.percentAffinityList.entrySet()) {
				if (mapResultNamePeTotal.containsKey(conceptPeAffinity.getKey())) {
					mapResultNamePeTotal.put(conceptPeAffinity.getKey(), mapResultNamePeTotal.get(conceptPeAffinity.getKey())+conceptPeAffinity.getValue());
				} else {
					if(FreebaseResultHandler.findResultByName(freebaseResultList, conceptPeAffinity.getKey())!=null)
						mapResultNamePeTotal.put(conceptPeAffinity.getKey(), conceptPeAffinity.getValue());
				}					
			}
		}
//		teste10(mapResultNamePeTotal);
		Entry<String, Float> result = Util.getGreaterValue(mapResultNamePeTotal);	
		return FreebaseResultHandler.findResultByName(freebaseResultList, result.getKey());
	}
	
	public FreebaseResult getMostSpecificConcept(FreebaseResult mainConcept, ArrayList<FreebaseResult> freebaseResultList, FileLogger log) throws IOException {
		Float minResultAfinity = 999f;
		FreebaseResult mostSpecificConcept = null;
		for (FreebaseResult freebaseResult : freebaseResultList) {
			if (freebaseResult.name.equals(mainConcept.name)) continue;
			Float resultAffinity = freebaseResult.percentAffinityList.get(mainConcept.name);	
			if (resultAffinity == null) resultAffinity = 0f;
			if (resultAffinity < minResultAfinity) {
				minResultAfinity = resultAffinity;
				mostSpecificConcept = freebaseResult;				
			} else  if (resultAffinity.equals(minResultAfinity)) {
				if (freebaseResult.typeList.size() < mostSpecificConcept.typeList.size()) {
					minResultAfinity = resultAffinity;
					mostSpecificConcept = freebaseResult;	
				}
			}
		}
		log.writeLine("MOST SPECIFIC CONCEPT: "+mostSpecificConcept);
		return mostSpecificConcept;
	}
}
