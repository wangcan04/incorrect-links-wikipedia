
package crosslinks; 


import java.io.BufferedWriter; 
import java.io.File; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.util.HashSet; 
import java.util.Set; 


import org.neo4j.graphdb.Direction; 


import org.neo4j.graphdb.GraphDatabaseService; 
import org.neo4j.graphdb.Node; 
import org.neo4j.graphdb.RelationshipType; 
import org.neo4j.graphdb.Transaction; 
import org.neo4j.graphdb.factory.GraphDatabaseFactory; 



/** 
 * Main class 
 * 
 */ 
public class Principale { 


	/** 
	 * Relationship types 
	 * 
	 */ 
	public enum Rels implements RelationshipType 
	{ 
		/** 
		 * We only need crosslinks.  
		 */ 
		crosslink 
	} 


	/** 
	 * Main method 
	 * @param args command-line args. 
	 * @throws IOException when screwing up. 
	 */ 
	public static void main(String[] args) throws IOException { 

			// TODO Auto-generated method stub
			long start = System.currentTimeMillis();
			BufferedWriter bw = new BufferedWriter(new FileWriter("connected-components.csv"));
			bw.write("title#lang#size\n");
			String dbPath = "/neo4j-community-3.0.6-windows/neo4j-community-3.0.6/data/databases/graph.db";
			
			GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
			System.out.println("Connecting to the database...");
			final GraphDatabaseService graphDb = graphDbFactory.newEmbeddedDatabase(new File(dbPath));
			Runtime.getRuntime().addShutdownHook( new Thread()
			{
				@Override
				public void run()
				{
					graphDb.shutdown();
				}
			} );
		System.out.println("Done!"); 
		Set<Long> visited = new HashSet<Long>(); 
		System.out.println("Start visiting nodes..."); 
		int cc = 0; 
		try (Transaction tx = graphDb.beginTx()) { 
			for( Node n : graphDb.getAllNodes() ) { 
				long idn = n.getId(); 
				if (visited.contains(idn)) 
					continue; 
				cc += 1; 
				String title = (String)n.getProperty("title"); 
				String lang = (String)n.getProperty("lang"); 
				System.out.println(cc + ") Visiting the cc of [" + title + ", " + lang + "]"); 
				bw.write(title + "#" + lang); 
				int size = visit(graphDb, n, visited); 
				bw.write("#"+size+"\n"); 
				
			} 
			tx.success(); 
		} 


		System.out.println("Disconnecting from the database..."); 
		graphDb.shutdown(); 
		bw.close(); 
		System.out.println("Done!"); 
		long end = System.currentTimeMillis(); 
		long elapsed = end - start; 
		System.out.println("Time elapsed " + ReadableTime.readableTime(elapsed)); 
	} 


	/** 
	 * Visits a connected component from a start node. 
	 * @param graphDb The Neo4j database. 
	 * @param start The start node. 
	 * @param visited Set of nodes already visited. 
	 * @return the size of the connected component 
	 */ 
	public static int visit(GraphDatabaseService graphDb, Node start, Set<Long> visited) { 
		int count = 0; 
		for (Node nn : graphDb.traversalDescription().depthFirst().relationships(Rels.crosslink, Direction.OUTGOING).traverse(start).nodes()) { 
			//System.out.println(nn.getProperty("title") + " -- " + nn.getProperty("lang")); 
			visited.add(nn.getId()); 
			count += 1; 
		} 
		return count; 
	} 


} 


/** 
 * Utility class to get a readable form of elapsed time. 
 * 
 */ 
class ReadableTime { 


	/** 
	 * Milliseconds in one second. 
	 */ 
	private static final long ONE_SEC = 1000; 


	/** 
	 * Milliseconds in one minute 
	 */ 
	private static final long ONE_MIN = ONE_SEC * 60L; 


	/** 
	 * Milliseconds in one hour. 
	 */ 
	private static final long ONE_HOUR = ONE_MIN * 60L; 


	/** 
	 * Milliseconds in one day. 
	 */ 
	private static final long ONE_DAY = ONE_HOUR * 24L; 


	/** 
	 * Milliseconds in one year. 
	 */ 
	private static final long ONE_YEAR = ONE_DAY * 365L; 


	/** 
	 * Returns a readable time duration. 
	 * @param millis The duration in milliseconds. 
	 * @return A readable time duration. 
	 */ 
	public static String readableTime(long millis) { 
		long milliseconds = millis % 1000L; 
		long seconds = (millis / ONE_SEC) % 60L ; 
		long minutes = (millis / ONE_MIN ) % 60L; 
		long hours = (millis / ONE_HOUR) % 24L; 
		long days = (millis / ONE_DAY) % 365L; 
		long years = (millis / ONE_YEAR); 


		long[] time = new long[]{years, days, hours, minutes, seconds, milliseconds}; 
		String[] unit = new String[]{"y", "d", "h", "m", "s", "ms"}; 
		String rt = ""; 
		for ( int i = 0; i < time.length; i += 1 ) { 
			if ( time[i] == 0 ) 
				continue; 
			rt += rt.length() == 0 ? "" + time[i] + unit[i] : ", " + time[i] + unit[i];  
		} 
		if ( rt.length() == 0 ) 
			rt = "0ms"; 
		return rt; 
	} 


}