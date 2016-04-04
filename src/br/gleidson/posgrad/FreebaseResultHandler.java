package br.gleidson.posgrad;

import java.util.ArrayList;

import br.gleidson.posgrad.freebase.result.FreebaseResult;

public class FreebaseResultHandler {

	public static FreebaseResult findResultByName(ArrayList<FreebaseResult> freebaseResultList, String key) {
		for (FreebaseResult freebaseResult : freebaseResultList) {
			if (freebaseResult.name.equals(key))
				return freebaseResult;
		}
		return null;
	}

	public static ArrayList<FreebaseResult> findRelatedTopicsBetween(FreebaseResult mainConcept, FreebaseResult result) {
		ArrayList<FreebaseResult> relatedTopicList = new ArrayList<>();
		ArrayList<String> namesAdded = new ArrayList<>();
		for(FreebaseResult mainRelated : mainConcept.masterRelatedTopicList) {
			for(FreebaseResult resultRelated : result.masterRelatedTopicList) {
				if (resultRelated.equals(mainRelated)) {
					if (namesAdded.contains(mainRelated.name))
						continue;
					relatedTopicList.add(resultRelated);
					namesAdded.add(resultRelated.name);
				}
			}
		}
		for(FreebaseResult mainRelated : mainConcept.reverseRelatedTopicList) {
			for(FreebaseResult resultRelated : result.reverseRelatedTopicList) {
				if (resultRelated.equals(mainRelated)) {
					if (namesAdded.contains(mainRelated.name))
						continue;
					relatedTopicList.add(resultRelated);
					namesAdded.add(resultRelated.name);
				}
			}
		}
		for(FreebaseResult mainRelated : mainConcept.reverseRelatedTopicList) {
			for(FreebaseResult resultRelated : result.masterRelatedTopicList) {
				if (resultRelated.equals(mainRelated)) {
					if (namesAdded.contains(mainRelated.name))
						continue;
					relatedTopicList.add(resultRelated);
					namesAdded.add(resultRelated.name);
				}
			}
		}
		return relatedTopicList;
	}
}
