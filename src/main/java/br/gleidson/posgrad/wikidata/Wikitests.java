package br.gleidson.posgrad.wikidata;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;

import org.wikidata.wdtk.datamodel.interfaces.EntityDocument;
import org.wikidata.wdtk.datamodel.interfaces.ItemDocument;
import org.wikidata.wdtk.datamodel.interfaces.PropertyIdValue;
import org.wikidata.wdtk.wikibaseapi.WikibaseDataFetcher;
import org.wikidata.wdtk.wikibaseapi.apierrors.MediaWikiApiErrorException;
import org.wikidata.wdtk.examples.ExampleHelpers;

public class Wikitests {

	public static void main(String[] args) throws MediaWikiApiErrorException {
		ExampleHelpers.configureLogging();
		printDocumentation();

		WikibaseDataFetcher wbdf = WikibaseDataFetcher.getWikidataDataFetcher();

		System.out.println("*** Fetching data for one entity:");
		EntityDocument q42 = wbdf.getEntityDocument("Q42");
		System.out
				.println("The current revision of the data for entity Q42 is "
						+ q42.getRevisionId());
		if (q42 instanceof ItemDocument) {
			System.out.println("The English name for entity Q42 is "
					+ ((ItemDocument) q42).getLabels().get("en").getText());
		}

		System.out.println("*** Fetching data for several entities:");
		Map<String, EntityDocument> results = wbdf.getEntityDocuments("Q80",
				"P31");
		// Keys of this map are Qids, but we only use the values here:
		for (EntityDocument ed : results.values()) {
			System.out.println("Successfully retrieved data for "
					+ ed.getEntityId().getId());
		}

		System.out
				.println("*** Fetching data using filters to reduce data volume:");
		// Only site links from English Wikipedia:
		//wbdf.getFilter().setSiteLinkFilter(Collections.singleton("enwiki"));
		// Only labels in French:
//		wbdf.getFilter().setLanguageFilter(Collections.singleton("fr"));
		//wbdf.getFilter().setLanguageFilter(Collections.singleton("pt-br"));
		// No statements at all:
		wbdf.getFilter().setPropertyFilter(
				Collections.<PropertyIdValue> emptySet());
		EntityDocument q8 = wbdf.getEntityDocument("Q8");
		if (q8 instanceof ItemDocument) {
			System.out.println("The French label for entity Q8 is "
					+ ((ItemDocument) q8).getLabels().get("pt-br").getText()
					+ "\nand its English Wikipedia page has the title "
					+ ((ItemDocument) q8).getSiteLinks().get("enwiki")
							.getPageTitle() + ".");
		}

		System.out.println("*** Fetching data based on page title:");
		String title = "cacatua";
		EntityDocument edPratchett = wbdf.getEntityDocumentByTitle("enwiki",
				title);
		System.out.println("The Qid of " + title
				+ (edPratchett == null ? " was not found" : (" is " + edPratchett.getEntityId().getId())));

		System.out.println("*** Fetching data based on several page titles:");
		results = wbdf.getEntityDocumentsByTitle("enwiki", "Wikidata",
				"Wikipedia");
		// In this case, keys are titles rather than Qids
		for (Entry<String, EntityDocument> entry : results.entrySet()) {
			System.out
					.println("Successfully retrieved data for page entitled \""
							+ entry.getKey() + "\": "
							+ entry.getValue().getEntityId().getId());
		}

		System.out.println("*** Done.");
	}

	/**
	 * Prints some basic documentation about this program.
	 */
	public static void printDocumentation() {
		System.out
				.println("********************************************************************");
		System.out.println("*** Wikidata Toolkit: FetchOnlineDataExample");
		System.out.println("*** ");
		System.out
				.println("*** This program fetches individual data using the wikidata.org API.");
		System.out.println("*** It does not download any dump files.");
		System.out
				.println("********************************************************************");
	}

}