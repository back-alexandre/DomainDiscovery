package br.gleidson.posgrad.freebase.result;

public class FreebaseType {
	
	public String domain = "";
	public String name = "";
	public int relevance;
	
	public FreebaseType(String type, String domain, int i) {
		this.domain = domain;
		this.name = type;
		this.relevance = i;
	}
	
	 public FreebaseType(String type, String domain2) {
		this.domain = domain2;
		this.name = type;
		this.relevance = 0;
	}

	@Override
	public String toString() {		
		return name + "["+domain+"]";
	}
	
	@Override
	public boolean equals(Object obj) {		
		FreebaseType otherType = (FreebaseType) obj;
		return this.domain.equals(otherType.domain) && this.name.equals(otherType.name);
	}
	
}
