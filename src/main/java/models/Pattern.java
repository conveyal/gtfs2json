package models;

import java.util.ArrayList;
import java.util.List;

public class Pattern {

	public String pattern_id;
	public String pattern_name;
	public Integer direction_id;
	public String reverse_pattern;
	public String encoded_polyline; 
    
	public Double stop_ratio;
    
	public List<StopSequence> stops;
	
	public Pattern(String id, String name, Integer direction, String reverseId, String polyline, Double ratio) {
		
		this.pattern_id = id;
		this.pattern_name = name;
		this.direction_id = direction;
		this.reverse_pattern = reverseId;
		this.encoded_polyline = polyline;
		this.stop_ratio = ratio;
		
	}
   
	public void addStops(List<StopSequence> stops) {
		this.stops = stops;
	}
	
	public Pattern clone() {
		Pattern p = new Pattern(pattern_id, pattern_name, direction_id, reverse_pattern, encoded_polyline, stop_ratio);
		p.stops = new ArrayList<StopSequence>();
		p.stops.addAll(stops);
		
		return p;
	}
	
}
