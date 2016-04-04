package br.gleidson.posgrad.utility;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

public class Util {
	  
    public static String getStringFromDocument(Document doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
         
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
         
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            return writer.toString();
        } catch (TransformerException ex) {
            ex.printStackTrace();
            return null;
        }
    }
    
    @SuppressWarnings({"rawtypes","unchecked"})
	public static Map sortMapAsc(Map map) {		
		List list = new LinkedList(map.entrySet());
		 
		// sort list based on comparator
		Collections.sort(list, new Comparator() {
			public int compare(Object o1, Object o2) {
				return ((Comparable) ((Map.Entry) (o1)).getValue())
                                       .compareTo(((Map.Entry) (o2)).getValue());
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map sortedMap = new LinkedHashMap();
		for (Iterator it = list.iterator(); it.hasNext();) {
			Map.Entry entry = (Map.Entry) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
    }
    

	public static Map<String, Float> sortMapDesc(Map<String, Float>map) {		
		List<Entry<String, Float>> list = new LinkedList<Entry<String, Float>>(map.entrySet());
		 
		// sort list based on comparator
		Collections.sort(list, new Comparator<Entry<String, Float>>() {
			public int compare(Entry<String, Float> o1, Entry<String, Float> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
		for (Iterator<Entry<String, Float>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Float> entry = (Map.Entry<String, Float>) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
    }
	
	public static Map<String, Integer> sortMapDescByValue(Map<String, Integer>map) {		
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(map.entrySet());
		 
		// sort list based on comparator
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
    }
	
	public static Map<String, Integer> sortMapAsc2(Map<String, Integer>map) {		
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(map.entrySet());
		 
		// sort list based on comparator
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getValue().compareTo(o2.getValue());
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
    }

	public static Entry<String, Float> getGreaterValue(	HashMap<String, Float> mapResultNamePeTotal) {
		float greaterValue = 0f;
		Entry<String, Float> result = null;
		for (Entry<String, Float> entry : mapResultNamePeTotal.entrySet()) {
			if (entry.getValue() > greaterValue) { 
				result = entry;
				greaterValue = entry.getValue();
			}				
		}		
		return result;
	}
	
	public static String getValidFilenameFrom(String url) {
		String u  = url;
        try {
            u = new String(url.getBytes(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } 
        u = u.replaceAll("[\\?\\\\/:|<>\\*]", " "); 
        u = u.replaceAll("\\s+", "_");
        return u;
	}

	public static Map<Integer, Integer> sortMapDesc(HashMap<Integer, Integer> mapCorrectDomainPosition) {
		List<Entry<Integer, Integer>> list = new LinkedList<Entry<Integer, Integer>>(mapCorrectDomainPosition.entrySet());
		 
		// sort list based on comparator
		Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o2.getValue().compareTo(o1.getValue());
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
		for (Iterator<Entry<Integer, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	public static Map<Integer, Integer> sortMapAscByKey(HashMap<Integer, Integer> mapCorrectDomainPosition) {
		List<Entry<Integer, Integer>> list = new LinkedList<Entry<Integer, Integer>>(mapCorrectDomainPosition.entrySet());
		 
		// sort list based on comparator
		Collections.sort(list, new Comparator<Entry<Integer, Integer>>() {
			public int compare(Entry<Integer, Integer> o1, Entry<Integer, Integer> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map<Integer, Integer> sortedMap = new LinkedHashMap<Integer, Integer>();
		for (Iterator<Entry<Integer, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<Integer, Integer> entry = (Map.Entry<Integer, Integer>) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public static Map<String, Float> sortStringFloatMapAscByKey(HashMap<String, Float> mapCorrectDomainPosition) {
		List<Entry<String, Float>> list = new LinkedList<Entry<String, Float>>(mapCorrectDomainPosition.entrySet());
		 
		// sort list based on comparator
		Collections.sort(list, new Comparator<Entry<String, Float>>() {
			public int compare(Entry<String, Float> o1, Entry<String, Float> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map<String, Float> sortedMap = new LinkedHashMap<String, Float>();
		for (Iterator<Entry<String, Float>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Float> entry = (Map.Entry<String, Float>) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
	public static Map<String, Integer> sortStringIntegerMapAscByKey(HashMap<String, Integer> mapCorrectDomainPosition) {
		List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(mapCorrectDomainPosition.entrySet());
		 
		// sort list based on comparator
		Collections.sort(list, new Comparator<Entry<String, Integer>>() {
			public int compare(Entry<String, Integer> o1, Entry<String, Integer> o2) {
				return o1.getKey().compareTo(o2.getKey());
			}
		});
 
		// put sorted list into map again
                //LinkedHashMap make sure order in which keys were inserted
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
}
