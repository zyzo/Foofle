package indexing;

import java.util.HashMap;
import java.util.Map;

public class HTMLTagsPonderation {
	public static Map<String, Integer> SPECIAL_TAGS_MAP = new HashMap<>();
	public static Map<String, Integer> META_KEYWORDS_MAPS = new HashMap<>();
	
	public HTMLTagsPonderation() {
		SPECIAL_TAGS_MAP.put("title", 10);
		SPECIAL_TAGS_MAP.put("h1", 10);
		SPECIAL_TAGS_MAP.put("h2", 5);
		SPECIAL_TAGS_MAP.put("p", 2);
		META_KEYWORDS_MAPS.put("description", 10);
		META_KEYWORDS_MAPS.put("og:title", 10);
		META_KEYWORDS_MAPS.put("twitter:description", 10);
	}
}
