package br.gleidson.posgrad.alchemy.result;

public class AlchemyConcept extends AlchemyResult{
	@Override
	public String toString() {
		return "Concept-> "+text+"\nRelevance: "+relevance;
	}
}
