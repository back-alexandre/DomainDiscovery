package br.gleidson.posgrad.wordnet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;


public class WordNetWrapper {
	
	private static final String WORD_NET_QUERY_URL = "http://wordnetweb.princeton.edu/perl/webwn?s=";
	private HashMap<String, WordNetSynset> synsetCache = new HashMap<String, WordNetSynset>();
	private static WordNetWrapper wordNetWraaper;
	
	public WordNetSynset query(String word) throws IOException {
		WordNetSynset synset = new WordNetSynset();
		if (word == null || word.isEmpty())
			return synset;
		if (synsetCache.containsKey(word)) 
			return synsetCache.get(word);
		return queryWordNet(word);
	}

	private WordNetSynset queryWordNet(String word) throws IOException {
		@SuppressWarnings("deprecation")
		String wnHtml = getHtmlFrom(WORD_NET_QUERY_URL+URLEncoder.encode(word));
		ArrayList<String> wnSinsetArray = getSinsetStringArrayFrom(wnHtml);
		WordNetSynset wnSynset = new WordNetSynset(wnSinsetArray);
		for (String wnWord : wnSinsetArray) {
			if (synsetCache.containsKey(wnWord))
				continue;
			synsetCache.put(wnWord, wnSynset);
		}
		return wnSynset;
	}

	private ArrayList<String> getSinsetStringArrayFrom(String wnHtml) {
		ArrayList<String> synset = new ArrayList<>();  				
		if (wnHtml == null)
				return synset;
		if (wnHtml.contains("<h3>Noun</h3>")) {
			String htmlPart = wnHtml.substring(wnHtml.lastIndexOf("<h3>Noun</h3>"));			
			return extractSynsetFrom(htmlPart.substring(htmlPart.indexOf("<li><a"), htmlPart.indexOf("</ul>")));
		}
		return synset;
	}

	private ArrayList<String> extractSynsetFrom(String htmlLiPart) {
		String[] htmlLiLine = htmlLiPart.split("</li>");
		ArrayList<String> synset = new ArrayList<>();
		for (String line : htmlLiLine) {
			addIfNotContains(synset, getSynsFromLine(line));
		}				
		return synset;
	}

	private void addIfNotContains(ArrayList<String> synset, ArrayList<String> synsFromLine) {
		for (String string : synsFromLine) {
			if (synset.contains(string))
				continue;
			synset.add(string);
		}
	}

	private ArrayList<String> getSynsFromLine(String line) {
		ArrayList<String> synset = new ArrayList<>();
		try {			
			String htmlSynPart = line.substring(line.lastIndexOf(" </a>"));
			String[] htmlLinks = htmlSynPart.split("</a>");
			for (String string : htmlLinks) {
				if (string.endsWith("</i>") || string.trim().isEmpty())
					continue;		
				String syn = string.substring(string.lastIndexOf("\">")+2);
//				System.out.println(syn);
				synset.add(syn);
			}			
		} catch(Exception e) {	}
		return synset;
	}

	public static WordNetWrapper getInstance() {
		if (wordNetWraaper == null)
			wordNetWraaper = new WordNetWrapper();
		return wordNetWraaper;
	}
	
	private String getHtmlFrom(String url) throws IOException {
		   URL formedUrl = new URL(url);
           URLConnection yc = formedUrl.openConnection();
           BufferedReader in = new BufferedReader(new InputStreamReader(
                   yc.getInputStream(), "UTF-8"));
           String inputLine;
           StringBuilder a = new StringBuilder();
           while ((inputLine = in.readLine()) != null)
               a.append(inputLine+"\n");
           in.close();

           return a.toString();
	}
}
