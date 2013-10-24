package com.conveyal.tools.gtfs2json;

import java.io.IOException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
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
    	
    	try {
    	    // parse the command line arguments
    	    CommandLine line = parser.parse( options, args );

    	    // validate that block-size has been set
    	    if( line.hasOption( "block-size" ) ) {
    	        // print the value of block-size
    	        System.out.println( line.getOptionValue( "block-size" ) );
    	    }
    	}
    	catch( ParseException exp ) {
    	    System.out.println( "Unexpected exception:" + exp.getMessage() );
    	}
    	
    	ProcessGtfs gtfsProcessor = new ProcessGtfs();
    	gtfsProcessor.loadGtfs();
    	
    	gtfsProcessor.exportJson();
    }
}
