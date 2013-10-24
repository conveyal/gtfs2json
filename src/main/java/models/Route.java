package models;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@JsonSerialize(include=Inclusion.NON_NULL)
public class Route {

	public String route_id;
	public String route_short_name;
	public String route_long_name;
	public String route_desc;
	public String route_type;
	public String route_url;
	public String route_color;
	public String route_text_color;
    
	public List<Pattern> patterns = new ArrayList<Pattern>();
    
    public Route(org.onebusaway.gtfs.model.Route route){
    	
    	route_id = route.getId().getId();
        route_short_name = route.getShortName();
        route_long_name = route.getLongName();
        route_desc = route.getDesc();
        route_type = "" + route.getType();
        route_url = route.getUrl();
        route_color = route.getColor();
        route_text_color = route.getTextColor();
    	
    }
    
    public void addPattern(Pattern p) {
    
    	patterns.add(p);
    	
    }
}
