package br.gleidson.posgrad.dmoz;

public enum EnumDomain {
	SOCCER 		("Soccer", "Soccer", "Soccer","Top/Sports/Soccer/"),
	VIDEO_GAMES	("Video_Games", "Video_Games", "Video Games","Top/Games/Video_Games/Action"),
	MUSIC		("Music","Music", "Music","Top/Arts/Music/Bands_and_Artists/"),
	CLOTHING	("Clothing", "Fashion_Clothing_and_Textiles", "Fashion, Clothing and Textiles", "Top/Shopping/Clothing/"),
	MEDICINE	("Medicine", "Medicine", "Medicine","Top/Health/Medicine"),
	COMPUTERS	("Computers", "Computers", "Computers", "Top/Computers/Hardware"),
	FOOD		("Food", "Food_and_Drink", "Food &amp; Drink", "Top/Recreation/Food/Drink"),
	FILM		("Film", "Film", "Film","Top/Arts/Movies/Titles"),
	LOCATION	("Location", "Location", "Location","Top/Recreation/Travel"),
	EDUCATION	("Education", "Education", "Education","Top/Reference/Education/Colleges_and_Universities/North_America/");
	
	private String dmozDomainName;
	private String freebaseDomainName;
	private String freebaseOriginalDomainName;
	private String dmozDomainDirectory;
	
	private EnumDomain(String dmozDomain, String freebaseDomain, String freebaseOriginalDomainName, String dmozDomainDirectory) {
		this.dmozDomainName = dmozDomain;
		this.freebaseDomainName = freebaseDomain;
		this.freebaseOriginalDomainName = freebaseOriginalDomainName;
		this.dmozDomainDirectory = dmozDomainDirectory;
	}

	public String getDmozDomainName() {
		return dmozDomainName;
	}

	public void setDmozDomainName(String dmozDomainName) {
		this.dmozDomainName = dmozDomainName;
	}

	public String getFreebaseDomainName() {
		return freebaseDomainName;
	}

	public void setFreebaseDomainName(String freebaseDomainName) {
		this.freebaseDomainName = freebaseDomainName;
	}

	public String getFreebaseOriginalDomainName() {
		return freebaseOriginalDomainName;
	}

	public void setFreebaseOriginalDomainName(String freebaseOriginalDomainName) {
		this.freebaseOriginalDomainName = freebaseOriginalDomainName;
	}

	public String getDmozDomainDirectory() {
		return dmozDomainDirectory;
	}

	public void setDmozDomainDirectory(String dmozDomainDirectory) {
		this.dmozDomainDirectory = dmozDomainDirectory;
	}

	public static String getFreebaseNameByDmozName(String actualDesiredTopic) {
		EnumDomain[] values = values();
		for (EnumDomain enumDomain : values) {
			if (enumDomain.getDmozDomainName().equals(actualDesiredTopic))
				return enumDomain.getFreebaseOriginalDomainName();
		}
		return null;
	}
	
	public static String getFreebaseNameByDmozDirectory(String actualDesiredTopic) {
		EnumDomain[] values = values();
		for (EnumDomain enumDomain : values) {
			if (enumDomain.getDmozDomainDirectory().equals(actualDesiredTopic))
				return enumDomain.getFreebaseOriginalDomainName();
		}
		return null;
	}

	public static boolean contains(String key) {
		EnumDomain[] values = values();
		for (EnumDomain enumDomain : values) {
			if (enumDomain.getFreebaseOriginalDomainName().contains(key))
				return true;
		}
		return false;
	}

	public static String getDmozDomainNameByDirectory(String actualDesiredTopic) {
		EnumDomain[] values = values();
		for (EnumDomain enumDomain : values) {
			if (enumDomain.getDmozDomainDirectory().equals(actualDesiredTopic))
				return enumDomain.getDmozDomainName();
		}
		return null;
	}	
	
	public static String getDmozDomainNameByFreebaseOriginalName(String actualDesiredTopic) {
		EnumDomain[] values = values();
		for (EnumDomain enumDomain : values) {
			if (enumDomain.getFreebaseOriginalDomainName().equals(actualDesiredTopic))
				return enumDomain.getDmozDomainName();
		}
		return null;
	}	
	
}
