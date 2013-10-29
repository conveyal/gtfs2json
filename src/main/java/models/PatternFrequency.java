package models;

import java.util.Map;

public class PatternFrequency {

	public Double average;
	public Double total;
	public Map<Integer, Double> hours;
	
	public PatternFrequency(Map<Integer, Double> hours){
		
		this.hours = hours;
			
		this.total = 0.0;
		
		for(Double count : hours.values()) {
			total += count;
		}
		
		if(hours.keySet().size() > 0 && this.total > 0)
			this.average = this.total / hours.keySet().size();
	}
	
}