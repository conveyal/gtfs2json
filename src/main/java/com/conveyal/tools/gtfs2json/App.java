package com.conveyal.tools.gtfs2json;

import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;


public class App 
{
    public static void main( String[] args ) throws JsonMappingException, JsonGenerationException, IOException
    {
    	// create the command line parser
    	CommandLineParser parser = new BasicParser();
 
    	// create the Options
    	Options options = new Options();
    	options.addOption( "r", "routes", false, "generate route-specific JSON exports" );
    	
    	Option opt1 = OptionBuilder.hasArgs(1).withArgName("input gtfs file")
    		    .withDescription("This is the input gtfs file as a .zip")
    		    .withLongOpt("input").create("o");
    	
    	options.addOption(opt1);
    	
    	try {
    	    // parse the command line arguments
    	    CommandLine line = parser.parse( options, args );
    	    
    	    ProcessGtfs gtfsProcessor = new ProcessGtfs();
        	gtfsProcessor.loadGtfs(line.getArgs()[0]);
        	
        	String r = line.getOptionValue("r");
        	Boolean exportRoutes = false;
        	if(r != null)
        		exportRoutes = true;
        		
        	gtfsProcessor.exportJson(exportRoutes);
        	
    	}
    	catch( ParseException exp ) {
    	    System.out.println( "Unexpected exception:" + exp.getMessage() );
    	}
    	
    	
    	
    	
    }
}
