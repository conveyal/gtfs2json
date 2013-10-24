package models;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=Inclusion.NON_NULL)
public class Stop {
	
	 public String stop_id;
	 public String stop_code; 
	 public String stop_name;
	 public String stop_desc; 
	 public Integer location_type;
	 public String parent_station;
	 public Double stop_lat; 
	 public Double stop_lon;
	 public String stop_url;

	 public List<PatternReference> patterns = new ArrayList<PatternReference>();
	 
	 public Stop(Long stopId, org.onebusaway.gtfs.model.Stop s) {
		 stop_id = stopId.toString();
		 stop_code = s.getCode();
		 stop_name = s.getName();
		 stop_desc = s.getDirection();
		 location_type = s.getLocationType();
		 parent_station = s.getParentStation();
		 stop_lat = s.getLat();
		 stop_lon = s.getLon();
	 }
    
}
