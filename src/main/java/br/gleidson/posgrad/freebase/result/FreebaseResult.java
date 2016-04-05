package br.gleidson.posgrad.freebase.result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class FreebaseResult {
	
	public String name = null;
	public ArrayList<FreebaseType> typeList = null;
	public boolean isDomain = false;
	public ArrayList<FreebaseResult> masterRelatedTopicList = null;
	public ArrayList<FreebaseResult> reverseRelatedTopicList = null;
	public HashMap<String, Float> percentAffinityList = null;
	public HashMap<String, Integer> quantityAffinityList = null;
	private Integer affinityPoints = null; 
	
	public FreebaseResult() {
		typeList = new ArrayList<>();
		masterRelatedTopicList = new ArrayList<>();
		reverseRelatedTopicList = new ArrayList<>();
		name = "";
	}
	
	@Override
	public String toString() {
		String string = name+" \nTypeList:";
		for (FreebaseType type : typeList) {
			string=string+" "+type+" | ";
		}				
		return string;
	}

	public boolean equals(Object obj) {
		FreebaseResult other = (FreebaseResult) obj;
		return other.name.equalsIgnoreCase(this.name);
	}
	
	public Float getPeAffinityPoints() {
		Float affinityPoints = 0f;
		if (percentAffinityList != null && !percentAffinityList.isEmpty()) {
			for(Entry<String, Float> resultAffinity : percentAffinityList.entrySet()) {
				affinityPoints += resultAffinity.getValue();
			}
		}
		return affinityPoints;
	}

	public boolean isTypeOf(FreebaseType targetType) {
		for (FreebaseType thisType : typeList) {
			if (thisType.equals(targetType)) return true;			
		}
		return false;
	}

	/**
	 * A SOMA DE QUANTOS TIPOS TEM EM COMUM COM CADA UM 
	 * @return
	 */
	public Integer getQtTypeAffinity() {		
		if (affinityPoints == null) {
			affinityPoints = 0;
			if (quantityAffinityList != null && !quantityAffinityList.isEmpty()) {
				for(Entry<String, Integer> resultAffinity : quantityAffinityList.entrySet()) {
					affinityPoints += resultAffinity.getValue();
				}
			}
		}
		return affinityPoints;
	}
}

