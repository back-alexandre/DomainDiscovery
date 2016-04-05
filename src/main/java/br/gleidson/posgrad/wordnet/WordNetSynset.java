package br.gleidson.posgrad.wordnet;

import java.util.ArrayList;

public class WordNetSynset {
	public ArrayList<String> synset = null;
	
	public WordNetSynset(ArrayList<String> wnSinsetArray) {
		synset = wnSinsetArray;
	}

	public WordNetSynset() {
		synset = new ArrayList<>();
	}

	@Override
	public String toString() {
		return synset.toString();
	}
}
