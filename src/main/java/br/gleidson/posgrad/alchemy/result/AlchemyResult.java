package br.gleidson.posgrad.alchemy.result;

import br.gleidson.posgrad.ConceptInstance;

public abstract class AlchemyResult extends ConceptInstance{
	
	public Double relevance = null;
	
	@Override
	public String toString() {
		return "Text: "+text+" Relevance: "+relevance;
	}
}
