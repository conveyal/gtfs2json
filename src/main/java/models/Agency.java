package models;

public class Agency {

	public String agency_id;
	public String agency_name;
	public String agency_url;
	public String agency_timezone;
    
    public Agency(org.onebusaway.gtfs.model.Agency agency){
    	
    	agency_id = agency.getId();
    	agency_name = agency.getName();
    	agency_url = agency.getUrl();
    	agency_timezone = agency.getTimezone();
    	
    }
}
