package br.gleidson.posgrad;

import java.util.ArrayList;

public class ConceptInstance {
	public String text = null;
	public ArrayList<String> synset;
	
	@Override
	public String toString() {
		return "Instance text: "+text;
	}
}


