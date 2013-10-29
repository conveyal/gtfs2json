package models;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=Inclusion.NON_NULL)
public class Pattern {

	public String pattern_id;
	public String pattern_name;
	public String direction_id;
	public String reverse_pattern;
	public String encoded_polyline; 
    
	public Double stop_ratio;
    
	public List<StopSequence> stops;
	
	public PatternFrequency frequency;
	
	public Pattern(String id, String name, String direction, String reverseId, String polyline, Double ratio, PatternFrequency frequency) {
		
		this.pattern_id = id;
		this.pattern_name = name;
		this.direction_id = direction;
		this.reverse_pattern = reverseId;
		this.encoded_polyline = polyline;
		this.stop_ratio = ratio;
		this.frequency = frequency;
		
	}
   
	public void addStops(List<StopSequence> stops) {
		this.stops = stops;
	}
	
	public Pattern clone() {
		Pattern p = new Pattern(pattern_id, pattern_name, direction_id, reverse_pattern, encoded_polyline, stop_ratio, frequency);
		p.stops = new ArrayList<StopSequence>();
		p.stops.addAll(stops);
		
		return p;
	}
	
}
