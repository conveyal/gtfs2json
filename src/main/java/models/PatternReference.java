package models;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=Inclusion.NON_NULL)
public class PatternReference {

	public String pattern_id;
	
	public PatternReference(Pattern p) {
		pattern_id = p.pattern_id;
	}
}
