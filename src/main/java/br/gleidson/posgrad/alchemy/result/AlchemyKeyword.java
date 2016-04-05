package br.gleidson.posgrad.alchemy.result;

public class AlchemyKeyword extends AlchemyResult {
	@Override
	public String toString() {
		return "Keyword-> "+text+" Relevance: "+relevance;
	}
}
