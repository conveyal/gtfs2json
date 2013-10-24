package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AgencyGroup {

	public Agency agency;
	
	public List<Route> routes = new ArrayList<Route>();
	
	public List<Stop> stops = new ArrayList<Stop>();
	
	public AgencyGroup(org.onebusaway.gtfs.model.Agency a) {
		
		agency = new Agency(a);
		
	}
	
	public void addRoute(Route r) {
		
		routes.add(r);
		
	}
	
	public void buildStopList(Map<Long, org.onebusaway.gtfs.model.Stop> stopMap) {
		
		HashMap<Long, Stop> stops = new HashMap<Long, Stop>();
		
		for(Route r : routes) {
			
			for(Pattern p : r.patterns) {
			
				for(StopSequence s : p.stops) {
				
					if(!stops.containsKey(s.stop_id))
						stops.put(s.stop_id, new Stop(s.stop_id, stopMap.get(s.stop_id)));
					
					stops.get(s.stop_id).patterns.add(new PatternReference(p));
				}
			}			
		}
		
		this.stops.clear();
		this.stops.addAll(stops.values());
	}
}
