package br.gleidson.posgrad.freebase.mql;

public class MqlQueryFactory {
	public static MqlQuery queryTypesByName(String name) {
		return new MqlQuery("[{\"id\":null,\"name\":\""+name+"\", \"type\":[]}]");
	}
	
	public static MqlQuery queryCommonTypesAndDomainsByName(String name) {
		return new MqlQuery("[{\"name\": \""+name+"\",\"type\":[{\"name\": null,\"domain\": {\"/freebase/domain_profile/category\": {\"id\": \"/category/commons\"},\"name\": null}}]}]");
	}

	public static MqlQuery queryCommonTopicsMasterRelatedToName(String name) {
		return new MqlQuery("[{\"name\":\""+name+"\",\"/type/reflect/any_master\":[{\"name\":null,\"type\":[{\"name\":null,\"domain\":{\"/freebase/domain_profile/category\":{\"id\":\"/category/commons\"},\"name\":null}}]}]}]");
	}

	public static MqlQuery queryCommonTopicsReverseRelatedToName(String name) {
		return new MqlQuery("[{\"name\":\""+name+"\",\"/type/reflect/any_reverse\":[{\"name\":null,\"type\":[{\"name\":null,\"domain\":{\"/freebase/domain_profile/category\":{\"id\":\"/category/commons\"},\"name\":null}}]}]}]");
	}
}
