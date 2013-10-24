package models;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=Inclusion.NON_NULL)
public class StopSequence implements Comparable<StopSequence> {
	
	public Long stop_id;
	public Integer stop_sequence;
	public Integer stop_departure_time;

	public StopSequence(Long id, Integer sequence, Integer departureTime)
	{
		stop_id = id;
		stop_sequence = sequence;
		stop_departure_time = departureTime;
	}

	public int compareTo(StopSequence o) {
		return this.stop_sequence.compareTo(o.stop_sequence);
	}

	
}
