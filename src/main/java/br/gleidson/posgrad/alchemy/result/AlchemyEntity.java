package br.gleidson.posgrad.alchemy.result;

public class AlchemyEntity extends AlchemyResult{
	
	public String type = null;
	public Integer count = null;
	
	@Override
	public String toString() {
		return "Text: "+text+"\nType: "+type +"\nRelevance: "+relevance+"\nCount: "+count+"\n";
	}
}
