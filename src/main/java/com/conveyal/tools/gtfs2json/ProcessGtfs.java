package com.conveyal.tools.gtfs2json;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import models.AgencyGroup;
import models.Pattern;
import models.PatternFrequency;
import models.StopGroup;
import models.StopSequence;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.onebusaway.gtfs.impl.GtfsDaoImpl;
import org.onebusaway.gtfs.serialization.GtfsReader;
import org.onebusaway.gtfs.model.*;
import org.opentripplanner.util.PolylineEncoder;
import org.opentripplanner.util.model.EncodedPolylineBean;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.index.strtree.STRtree;


public class ProcessGtfs  {
	
	private static ObjectMapper mapper = new ObjectMapper();
    private static JsonFactory jf = new JsonFactory();
	
    public Boolean mergeStops;
    
	private Map<String, Long> agencyIdMap = new HashMap<String, Long>();
	private Map<String, Long> routeIdMap = new HashMap<String, Long>();
	private Map<String, Long> stopIdMap = new HashMap<String, Long>();
	private Map<String, Long> tripIdMap = new HashMap<String, Long>();
	private	Map<String, Long> serviceIdMap = new HashMap<String, Long>();
	private Map<String, Long> serviceDateIdMap = new HashMap<String, Long>();
	private Map<Long, Pattern> patternMap = new HashMap<Long, Pattern>();
	
	private Map<Long, Agency> agencyMap = new HashMap<Long, Agency>();
	private Map<Long, Route> routeMap = new HashMap<Long, Route>();
	private Map<Long, Stop> stopMap = new HashMap<Long, Stop>();
	private Map<Long, Trip> tripMap = new HashMap<Long, Trip>();
	
	private Map<Long, String> tripShapeMap = new HashMap<Long, String>();
	
	private Map<Long, Long> tripRouteMap = new HashMap<Long, Long>();
	
	private Map<Long, Long> mergedStopMap = new HashMap<Long, Long>();
	private Map<Long, StopGroup> stopGroupMap = new HashMap<Long, StopGroup>();
	
	private HashMap<String,String> shapePolylineMap = new HashMap<String,String>();
	
	private	HashMap<Integer, HashSet<Long>> routeTypeStopMap = new HashMap<Integer, HashSet<Long>>();
	
	private	HashMap<Long, HashSet<Long>> stopRouteIdMap = new HashMap<Long, HashSet<Long>>();
	
	private Map<Long, ArrayList<StopSequence>> tripStopTimeMap = new HashMap<Long, ArrayList<StopSequence>>();
	
	private Map<Long, ArrayList<Long>> agencyIdRouteIdMap = new HashMap<Long, ArrayList<Long>>();

	
	private Map<Long, HashMap<Long, ArrayList<StopSequence>>> tripPatternStopMap = new HashMap<Long, HashMap<Long, ArrayList<StopSequence>>>();
	private Map<Long, HashMap<Long,  ArrayList<Long>>> tripPatternFirstStopMap = new HashMap<Long, HashMap<Long, ArrayList<Long>>>();
	//private Map<Long, HashMap<Long, HashMap<Long,  ArrayList<Long>>>> tripPatternFirstLastStopMap = new HashMap<Long, HashMap<Long, HashMap<Long,  ArrayList<Long>>>>();
	
	private HashMap<Long, HashMap<Long,  HashMap<Integer,  Double>>> tripPatternFrequencyMap = new HashMap<Long, HashMap<Long,  HashMap<Integer,  Double>>>();

	//private Map<Long, ArrayList<StopSequence>> tripRouteMap = new HashMap<Long, ArrayList<StopSequence>>();
	//private Map<Long,  Map<Long,  Double>> tripPatternStopRatioMap = new HashMap<Long,  Map<Long,  Double>>();
	
	private Map<String, List<ShapePoint>> shapePointIdMap = new HashMap<String, List<ShapePoint>>();
	
	public ProcessGtfs(Boolean mergeStops) {
		
		this.mergeStops =  mergeStops;

	}
	
	public void loadGtfs(String path) {
		
		GtfsReader reader = new GtfsReader();
    	GtfsDaoImpl store = new GtfsDaoImpl();
    	
    	Long agencyCount = new Long(0);
    	Long routeCount = new Long(0);
    	Long stopCount = new Long(0);
    	Long stopTimeCount = new Long(0);
    	Long tripCount = new Long(0);
    	Long shapePointCount = new Long(0);
    	Long serviceCalendarCount = new Long(0);
    	Long serviceCalendarDateCount = new Long(0);
    	Long shapeCount = new Long(0);
    	
    	try {
    		
    		File gtfsFile = new File(path);
    		
    		reader.setInputLocation(gtfsFile);
        	reader.setEntityStore(store);
        	reader.run();
    	    	
        	System.out.println("GtfsImporter: importing agencies...");
     
        	Long agencyId = 1l;
        	
	    	for (Agency gtfsAgency : reader.getAgencies()) {

	    		agencyIdMap.put(gtfsAgency.getId(), agencyId);

	    		agencyMap.put(agencyId, gtfsAgency);
	    		
	    		agencyCount++;  		
	    		agencyId++;
	    	}
	    	
	    	System.out.println("Agencies loaded: " + agencyCount.toString());
	    	System.out.println("GtfsImporter: importing routes...");

	    	Long routeId = 1l;
	        for (Route gtfsRoute : store.getAllRoutes()) {
	        
	        	agencyId = agencyIdMap.get(gtfsRoute.getAgency().getId());
	     
	        	routeIdMap.put(gtfsRoute.getId().toString(), routeId );
	            routeMap.put(routeId, gtfsRoute);
	           	
	            if(!agencyIdRouteIdMap.containsKey(agencyId))
	            	agencyIdRouteIdMap.put(agencyId, new ArrayList<Long>());
	            
	            agencyIdRouteIdMap.get(agencyId).add(routeId);
	            
	            routeId++;
	        	routeCount++;
	        }
	        
	        System.out.println("Routes loaded:" + routeCount.toString()); 
	        System.out.println("GtfsImporter: importing stops...");
	
	        Long stopId = 1l;
	        
	        for (Stop gtfsStop : store.getAllStops()) {	     
	           
	            stopIdMap.put(gtfsStop.getId().toString(), stopId );
	            
	            stopMap.put(stopId, gtfsStop);
	            
	            stopId++;
	           	          
	        	stopCount++;
	        }
	        
	        System.out.println("Stops loaded: " + stopCount);
	        System.out.println("GtfsImporter: importing Shapes...");
	        
	        System.out.println("Calculating agency centroid for stops...");
	        
	        
	        // import points
	        
	        for (ShapePoint shapePoint : store.getAllShapePoints()) {
	        
	        	if(!shapePointIdMap.containsKey(shapePoint.getShapeId().toString()))
	        		shapePointIdMap.put(shapePoint.getShapeId().toString(), new ArrayList<ShapePoint>());
	        
	        	shapePointIdMap.get(shapePoint.getShapeId().toString()).add(shapePoint);
	        	
	        	shapePointCount++;

	        }
	        
	        // sort/load points 
	        
	        GeometryFactory geometryFactory = new GeometryFactory();
	        
	        for(String gtfsShapeId : shapePointIdMap.keySet())
	        {
	        	
	        	List<ShapePoint> shapePoints  = shapePointIdMap.get(gtfsShapeId);
	        	
	        	Collections.sort(shapePoints);
	        	
	        	Double describedDistance = new Double(0);
	        	List<Coordinate> points = new ArrayList<Coordinate>();
	        	
	        	for(ShapePoint shapePoint : shapePoints)
	        	{
	        		describedDistance += shapePoint.getDistTraveled();
	      
	        		points.add(new Coordinate(shapePoint.getLon(), shapePoint.getLat()));
	        		
	        	}
	        	
	        	Coordinate[] cs = new Coordinate[0];
	        	Geometry geom = geometryFactory.createLineString(points.toArray(cs));
	        	EncodedPolylineBean polylineBean =  PolylineEncoder.createEncodings(geom);
	        	
	        	shapePolylineMap.put(gtfsShapeId, polylineBean.getPoints());
	        
	        }
	        
	        System.out.println("Shape points loaded: " + shapePointCount.toString());
	        System.out.println("Shapes loaded: " + shapeCount.toString());
	       	        
	        System.out.println("GtfsImporter: importing Service Calendars...");
	    	
	        
	        Long serviceId = 1l;
	        for (ServiceCalendar gtfsService : store.getAllCalendars()) {
	        	 	        	
	        	serviceIdMap.put(gtfsService.getServiceId().toString(), serviceId);
	        	serviceId++;
	        	serviceCalendarCount++;    	
	        }
	        
	        System.out.println("Service calendars loaded: " + serviceCalendarCount); 
	        
	        System.out.println("GtfsImporter: importing Service Calendar dates...");
	        
	        Long serviceDateId = 1l;
	        
	        for (ServiceCalendarDate gtfsServiceDate : store.getAllCalendarDates()) {
	        	
	        	
	        	serviceDateIdMap.put(gtfsServiceDate.getServiceId().toString(), serviceDateId);
	        	
	        	serviceDateId++;
	        	
	        	serviceCalendarDateCount++;
	        	
	        }
	        
	        System.out.println("Calendar dates loaded: " + serviceCalendarDateCount); 
	        
	        System.out.println("GtfsImporter: importing trips...");
	   
	        Long tripId = 1l;
	        
	        for (Trip gtfsTrip : store.getAllTrips()) {
	        	        	
	        	tripIdMap.put(gtfsTrip.getId().toString(), tripId);
	        	
	        	tripMap.put(tripId, gtfsTrip);
	        	
	        	String gtfsRouteId = gtfsTrip.getRoute().getId().toString();
	        	routeId = routeIdMap.get(gtfsRouteId);
	        	
	        	tripRouteMap.put(tripId, routeId);
	        	
	        	tripShapeMap.put(tripId, gtfsTrip.getShapeId().getId());
	        	
	        	tripId++;
	        	
	        	tripCount++;
	        
	        }
	        
	        System.out.println("Trips loaded: " + tripCount); 
	        	
        	System.out.println("GtfsImporter: indexing stops by mode and route...");
	    	
	        for (StopTime gtfsStopTime : store.getAllStopTimes()) {
	        	
	        	stopId = stopIdMap.get(gtfsStopTime.getStop().getId().toString());
	        	tripId = tripIdMap.get(gtfsStopTime.getTrip().getId().toString());
	       	
	        	routeId = tripRouteMap.get(tripId);
	        	
	        	Route route = routeMap.get(routeId);
	        	
	        	Integer routeType = route.getType();
	        	
	        	if(!routeTypeStopMap.containsKey(routeType)) 
	        		routeTypeStopMap.put(routeType, new HashSet<Long>());
	        	
	        	routeTypeStopMap.get(routeType).add(stopId);
	        	
	        	if(!stopRouteIdMap.containsKey(stopId))
	        		stopRouteIdMap.put(stopId, new HashSet<Long>());
	        	
	        	stopRouteIdMap.get(stopId).add(routeId);
	
	        }
	        
	        // merges stops or builds a no-op map of ids
	        mergeStops(mergeStops);
	        
	        
	        System.out.println("GtfsImporter: importing stopTimes...");
	    	
	        for (StopTime gtfsStopTime : store.getAllStopTimes()) {
	        	
	        	stopId = stopIdMap.get(gtfsStopTime.getStop().getId().toString());
	        	tripId = tripIdMap.get(gtfsStopTime.getTrip().getId().toString());
	       	
	        	// swap for merged stopId (or same id if merge is disabled)
	        	stopId = mergedStopMap.get(stopId);
	        	
	        	// assuming gtfs stop sequences start at 1 and increment by 1 isn't safe -- need to re-pack sequence positions after loading stop times
	        	StopSequence stopSequence = new StopSequence(stopId, gtfsStopTime.getStopSequence(), gtfsStopTime.getDepartureTime());
	        	
	        	if(!tripStopTimeMap.containsKey(tripId))
	        		tripStopTimeMap.put(tripId, new ArrayList<StopSequence>());
	        	
	        	tripStopTimeMap.get(tripId).add(stopSequence);
	        	
	        	stopTimeCount++;
	        }
	        
	        System.out.println("StopTimes loaded: " + stopTimeCount.toString());
	        
	        System.out.println("Imported GTFS file: " + agencyCount + " agencies; " + routeCount + " routes;" + stopCount + " stops; " +  stopTimeCount + " stopTimes; " + tripCount + " trips; " + shapePointCount + " shapePoints");

	        repackStopSequences();
	        
	        inferTripPatterns();
	       
	        calculateStopRatios();
	        
    	}
        catch (Exception e) {
    		
        	System.out.print(e.toString()); 
    	}
	}
	
	private void calculateStopRatios() {
		
		// TODO calc cross-pattern stop ratios
		
	}
	
	private void repackStopSequences(){
		
		// TODO average stopDepartureTime deltas across trips
		
		for(Long tripId : tripStopTimeMap.keySet()) {
		
			ArrayList<StopSequence> stopTimes = tripStopTimeMap.get(tripId);
			
			Collections.sort(stopTimes);
			
			for(int i = 0; i < stopTimes.size(); i++){
				stopTimes.get(i).stop_sequence = i + 1;
			}
			
			tripStopTimeMap.put(tripId, stopTimes);
		}	
	}
	
	private void addTripPatternToLookup(Long routeId, Long tripId, Long patternId, ArrayList<StopSequence> stopTimes)
	{
		Long firstStopId = stopTimes.get(0).stop_id;
		Long lastStopId = stopTimes.get(stopTimes.size() - 1).stop_id;
		
		if(!tripPatternFirstStopMap.containsKey(routeId)) {
			
			tripPatternFirstStopMap.put(routeId, new HashMap<Long, ArrayList<Long>>());
			tripPatternStopMap.put(routeId, new HashMap<Long, ArrayList<StopSequence>>());
			
		}
		
		if(!tripPatternFirstStopMap.get(routeId).containsKey(firstStopId)) {
			tripPatternFirstStopMap.get(routeId).put(firstStopId, new ArrayList<Long>());
		}
		
		tripPatternFirstStopMap.get(routeId).get(firstStopId).add(patternId);
		
		
		tripPatternStopMap.get(routeId).put(patternId, stopTimes);
		
		Trip trip = tripMap.get(tripId);
			
		String patternName;
	
		// could make smarter by finding "via" stops to differentiate patterns with shared o/d stops
		if(trip.getTripHeadsign() != null && trip.getTripHeadsign() != "")
			patternName = trip.getTripHeadsign();
		else {
			stopMap.get(firstStopId).getName();
			
			patternName = stopMap.get(firstStopId).getName() + " -- " + stopMap.get(lastStopId).getName();
		}
		
		Pattern pattern = new Pattern(patternId.toString(), patternName, trip.getDirectionId(), null, shapePolylineMap.get(tripShapeMap.get(tripId)), null, null);
		
		pattern.addStops(stopTimes);
		
		// TODO add pattern stop ratio
		patternMap.put(patternId, pattern);
	}
	
	public void updatePatternFrequency(Long routeId,Long patternId, ArrayList<StopSequence> stopTimes) {
		
		Integer firstStopHour = (stopTimes.get(0).stop_departure_time - (stopTimes.get(0).stop_departure_time % (60 * 60)))/ 60 / 60;
	
		if(!tripPatternFrequencyMap.containsKey(routeId)) 
			tripPatternFrequencyMap.put(routeId, new HashMap<Long, HashMap<Integer,Double>>());
		
		if(!tripPatternFrequencyMap.get(routeId).containsKey(patternId))
			tripPatternFrequencyMap.get(routeId).put(patternId, new HashMap<Integer, Double>());
				
		
		if(!tripPatternFrequencyMap.get(routeId).get(patternId).containsKey(firstStopHour))
			tripPatternFrequencyMap.get(routeId).get(patternId).put(firstStopHour, 0.0);
			
		tripPatternFrequencyMap.get(routeId).get(patternId).put(firstStopHour, tripPatternFrequencyMap.get(routeId).get(patternId).get(firstStopHour) + 1);
	}
	
	private void inferTripPatterns()
	{
		System.out.println("Infering trip patterns...");
		
		Set<Long> tripIds = tripStopTimeMap.keySet();
		
		Long nextPatternId = 1l;
		
		for(Long tripId : tripIds)
		{
			ArrayList<StopSequence> stopTimes = tripStopTimeMap.get(tripId);
			
			Long routeId = tripRouteMap.get(tripId);
			
			Long patternId = findExistingPattern(routeId, tripId, stopTimes);
			
			if(patternId == null)
			{
				patternId = nextPatternId;
				addTripPatternToLookup(routeId, tripId, patternId, stopTimes);
				
				nextPatternId++;
			}
			
			updatePatternFrequency(routeId, patternId, stopTimes);
		}
		
		System.out.println(nextPatternId + " patterns found.");
	}
	
	private Long findExistingPattern(Long routeId, Long tripId, ArrayList<StopSequence> stopTimes)
	{	
		
		try {
			ArrayList<Long> candidatePatterns = tripPatternFirstStopMap.get(routeId).get(stopTimes.get(0).stop_id);
			
			if(candidatePatterns != null)
			{
				for(Long candidate : candidatePatterns)
				{
					ArrayList<StopSequence> patternStops = tripPatternStopMap.get(routeId).get(candidate);
					
					if(patternStops.size() != stopTimes.size())
						continue;
					
					int index = 0;
					
					for(StopSequence patternStop : patternStops)
					{
						if(!patternStop.stop_id.equals(stopTimes.get(index).stop_id));
							continue;
					}
					
					return candidate;
				}
			}
			// if exact match failed look for pattern within known patterns
			
			for(Long candidate : tripPatternStopMap.get(routeId).keySet()) {

				ArrayList<StopSequence> patternStops = tripPatternStopMap.get(routeId).get(candidate);
				
				Boolean firstStopFound = false;
				int subPatternOffest = 0;
				
				Boolean matchFound = false;
				
				for(StopSequence patternStop : patternStops ) {
					
					if(stopTimes.size() <= subPatternOffest) {
						break;
					}
					 
					StopSequence currentSubPatternStop = stopTimes.get(subPatternOffest);
					
					if(firstStopFound){
						
						if(currentSubPatternStop.stop_id.equals(patternStop.stop_id)){
							subPatternOffest++;	
						}
						else {
							matchFound = false;
							break;
						}
					}
					else {
						if(currentSubPatternStop.stop_id.equals(patternStop.stop_id)) {
							firstStopFound = true;
							subPatternOffest++;
							matchFound = true;
						}
					}
				}
				
				if(subPatternOffest < stopTimes.size() - 1)
					matchFound = false;
				
				
				if(matchFound)
					return candidate;
				
			}
			
			// look for new patterns that contain existing patterns -- replace old with longer
			
			for(Long candidate : tripPatternStopMap.get(routeId).keySet()) {
				
				ArrayList<StopSequence> patternStops = tripPatternStopMap.get(routeId).get(candidate);
				
				Boolean firstStopFound = false;
				int subPatternOffest = 0;
				
				Boolean matchFound = false;
				
				for(StopSequence patternStop : stopTimes ) {
					
					if(patternStops.size() <= subPatternOffest) {
						break;
					}
					
					StopSequence currentSubPatternStop = patternStops.get(subPatternOffest);
					
					if(firstStopFound){
						
						if(currentSubPatternStop.stop_id.equals(patternStop.stop_id)){
							subPatternOffest++;	
						}
						else {
							matchFound = false;
							break;
						}
					}
					else {
						if(currentSubPatternStop.stop_id.equals(patternStop.stop_id)) {
							firstStopFound = true;
							subPatternOffest++;
							matchFound = true;
						}
					}
				}
		
				if(subPatternOffest < patternStops.size() - 1)
					matchFound = false;
				
				if(matchFound) {
					addTripPatternToLookup(routeId, tripId, candidate, stopTimes);
					
					return candidate;
				}
			}
			
			return null;
		}
		catch(Exception e) {
			return null;
			
		}
	}
	
	public void mergeStopGroups(StopGroup sg1, StopGroup sg2) {
		sg1.stops.addAll(sg2.stops);
		//replace references for sg2 with sg1
		for(Long stopId : sg2.stops){
			stopGroupMap.put(stopId, sg1);
		}
	}
	
	public void addStopToGroup(Long stopId, StopGroup sg) {
		if(sg != null)
			sg.stops.add(stopId);
		else {
			// create new group
			sg = new StopGroup();
			sg.stops.add(stopId);
			stopGroupMap.put(stopId, sg);
		}
			
	}
	
	public void groupStops(Long stopId1, Long stopId2) {
		
		StopGroup sg1 = stopGroupMap.get(stopId1);
		StopGroup sg2 = stopGroupMap.get(stopId2);
		
		// check for groups and merge
		if(sg1 != null && sg2 != null) {
			mergeStopGroups(sg1, sg2);
		}
		else {
			if(sg1 != null) 
				addStopToGroup(stopId2, sg1);
			else if(sg2 != null)
				addStopToGroup(stopId1, sg2);
			else
				addStopToGroup(stopId1, null);
		}
	}
	
	public void mergeStops(Boolean mergeStops) {
		
		System.out.println("merging stops...");
		System.out.println("gtfs stops: " + stopMap.keySet().size());
		
		
		HashSet<Long> majorStops = new HashSet<Long>();
		
		// infer stops?
		if(mergeStops) {
			
			STRtree stopIndex = new STRtree();
			HashMap<Long, IndexedStop> indexedStopMap = new HashMap<Long, IndexedStop>();
			
			// index stops
			for(Long stopId : stopMap.keySet()) {
				Stop s = stopMap.get(stopId);
		
				ProjectedCoordinate pc = GeoUtils.convertLatLonToEuclidean(new Coordinate(s.getLon(), s.getLat()));
				Point p = GeoUtils.projectedGeometryFactory.createPoint(pc);
								
				IndexedStop is = new IndexedStop();
				is.stopId = stopId;
				is.gtfsStop = s;
				is.pc = pc;
				is.p = p;
				
				stopIndex.insert(p.getEnvelopeInternal(), stopId);
				
				indexedStopMap.put(stopId, is);
			}
				
			// TODO group by parent station id if set
			
			// group by name within radius 
			
			for(Long stopId1 : stopMap.keySet()) {
				
				IndexedStop is1 = indexedStopMap.get(stopId1);
				
				Polygon b = (Polygon) is1.p.buffer(100);
				for(Long stopId2 : (List<Long>)stopIndex.query(b.getEnvelopeInternal())) {
					IndexedStop is2 = indexedStopMap.get(stopId2);
					
					if(is1.gtfsStop.getName().equals(is2.gtfsStop.getName()) && !stopId1.equals(stopId2) ) {
						groupStops(stopId1, stopId2);
					}			
				}
			}	
			
			// group by radius 
			
			for(Long stopId1 : stopMap.keySet()) {
				
				IndexedStop is1 = indexedStopMap.get(stopId1);
				
				Polygon b = (Polygon) is1.p.buffer(25);
				for(Long stopId2 : (List<Long>)stopIndex.query(b.getEnvelopeInternal())) {
					IndexedStop is2 = indexedStopMap.get(stopId2);
					
					if(!stopId1.equals(stopId2) ) {
						groupStops(stopId1, stopId2);
					}			
				}
			}	
			
			// group by radius surrounding rail/metro stops 
			
			// find subway/metro stops: gtfs route type 1
			if(routeTypeStopMap.containsKey(1))
				majorStops.addAll(routeTypeStopMap.get(1));
			
			// find rail stops: gtfs route type 2
			if(routeTypeStopMap.containsKey(2))
				majorStops.addAll(routeTypeStopMap.get(2));
			
			for(Long stopId1 : majorStops) {
				
				IndexedStop is1 = indexedStopMap.get(stopId1);
				
				Polygon b = (Polygon) is1.p.buffer(250);
				for(Long stopId2 : (List<Long>)stopIndex.query(b.getEnvelopeInternal())) {
					IndexedStop is2 = indexedStopMap.get(stopId2);
					
					// grouping every stop within 250m of rail or subway?
					// TODO this won't work in areas with dense/multi-line rail networks (NYC & London) 
					// need to use Thiessen Buffer
					if(!stopId1.equals(stopId2) ) {
						groupStops(stopId1, stopId2);
					}			
				}
			}	
			
			// find and add un-grouped stops
			for(Long stopId : stopMap.keySet()) {
				
				if(!stopGroupMap.containsKey(stopId))
					groupStops(stopId, stopId);
			}	
			
		}
		else {
			
			// a no-op operation to skip merge
			
			for(Long stopId : stopMap.keySet()) {
				groupStops(stopId, stopId);
			}
		}	
		
		// find primary stop for each stop group
		// if there's a major stop it takes precedent  -- otherwise stop with most routes
		
		for(StopGroup gr : stopGroupMap.values()) {
			if(gr.primaryStop == null) {
				Integer maxRouteCount = 0;
				for(Long stopId : gr.stops) {
					if(majorStops.contains(stopId)) {
						gr.primaryStop = stopId;
						break;
					}
					
					if(stopRouteIdMap.containsKey(stopId) && stopRouteIdMap.get(stopId).size() > maxRouteCount) {
						maxRouteCount = stopRouteIdMap.get(stopId).size();
						gr.primaryStop = stopId;
						// not handling ties for now...
					}
				}
			}
		}
		
		// map merged stops to primary stop

		HashSet<Long> primaryStops = new HashSet<Long>();
				
		for(Long stopId : stopGroupMap.keySet()) {
			
			if(stopGroupMap.get(stopId).primaryStop == null)
				System.out.println("Stop not mapped to primary: " + stopId);
			else {
				primaryStops.add(stopGroupMap.get(stopId).primaryStop);
				mergedStopMap.put(stopId, stopGroupMap.get(stopId).primaryStop);
			}
		}	
		
		System.out.println("merged stops: " + primaryStops.size());
	}

	public void exportJson(Boolean exportRoutes) throws JsonMappingException, JsonGenerationException, IOException {
			
		
		for(Long agencyId : agencyMap.keySet()) {
			
			Agency agency = agencyMap.get(agencyId);
			
			AgencyGroup agencyGroup = new AgencyGroup(agency);
			
			for(Long routeId : agencyIdRouteIdMap.get(agencyId)) {
				
				Route route = routeMap.get(routeId);
				
				models.Route r = new models.Route(route);
				
				if(tripPatternFrequencyMap.containsKey(routeId)) {
					for(Long patternId : tripPatternFrequencyMap.get(routeId).keySet()) {					
						if(patternMap.containsKey(patternId)) {
							Pattern p;
							
							p = patternMap.get(patternId).clone();
							p.stops = null;
							r.addPattern(p);
								
						}
					}
				}
					
				agencyGroup.addRoute(r);
			}
			
			File dir = new File("data");
			dir.mkdir();
			
			File f = new File(dir, "index.json");
			
			if (!f.exists()) {
				f.createNewFile();
			}
 
			FileWriter fileWriter = new FileWriter(f);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(toJson(agencyGroup, true));
			bufferedWriter.close();

			
			agencyGroup =	  new AgencyGroup(agency);
			
			for(Long routeId : agencyIdRouteIdMap.get(agencyId)) {
				
				Route route = routeMap.get(routeId);
				
				models.Route r = new models.Route(route);
				
				if(tripPatternFrequencyMap.containsKey(routeId)) {
					for(Long patternId : tripPatternFrequencyMap.get(routeId).keySet()) {					
						if(patternMap.containsKey(patternId)) {
							Pattern p = patternMap.get(patternId);
							p.frequency = new PatternFrequency(tripPatternFrequencyMap.get(routeId).get(patternId));
							r.addPattern(p);
						}
					}
				}
			
				agencyGroup.addRoute(r);
			}
			
			agencyGroup.buildStopList(this.stopMap);
			
			f = new File(dir, "index_full.json");
			
			if (!f.exists()) {
				f.createNewFile();
			}
 
			fileWriter = new FileWriter(f);
			bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(toJson(agencyGroup, true));
			bufferedWriter.close();
			
			
			if(exportRoutes) {
			
				for(Long routeId : agencyIdRouteIdMap.get(agencyId)) {
					
					agencyGroup = new AgencyGroup(agency);
					
					Route route = routeMap.get(routeId);
					
					models.Route r = new models.Route(route);
					
					if(tripPatternFrequencyMap.containsKey(routeId)) {
						for(Long patternId : tripPatternFrequencyMap.get(routeId).keySet()) {					
							if(patternMap.containsKey(patternId)) {
								Pattern p = patternMap.get(patternId);
								p.frequency = new PatternFrequency(tripPatternFrequencyMap.get(routeId).get(patternId));
								r.addPattern(p);
							}
						}
					}
				
					agencyGroup.addRoute(r);
					
					agencyGroup.buildStopList(this.stopMap);
					
					File routeDir = new File(dir, "routes"); 
					
					routeDir.mkdir();
					
					f = new File(routeDir, routeId + ".json");
					
					if (!f.exists()) {
						f.createNewFile();
					}
		 
					fileWriter = new FileWriter(f);
					bufferedWriter = new BufferedWriter(fileWriter);
					bufferedWriter.write(toJson(agencyGroup, true));
					bufferedWriter.close();
				}
				
			}
		}
	}
	
	
	private static String toJson(Object pojo, boolean prettyPrint)
	    throws JsonMappingException, JsonGenerationException, IOException {
	        StringWriter sw = new StringWriter();
	        JsonGenerator jg = jf.createJsonGenerator(sw);
	        if (prettyPrint) {
	            jg.useDefaultPrettyPrinter();
	        }
	        mapper.writeValue(jg, pojo);
	        return sw.toString();
	}
	
	private class IndexedStop {
		
		public Long stopId;
		public Stop gtfsStop;
		public ProjectedCoordinate pc;
		public Point p;
	}
}

