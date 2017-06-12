package crosslinks; 


import java.io.File; 
import java.util.HashSet; 
import java.util.Set; 


import org.neo4j.graphdb.Direction; 


import org.neo4j.graphdb.GraphDatabaseService; 
import org.neo4j.graphdb.Label; 
import org.neo4j.graphdb.Node; 
import org.neo4j.graphdb.RelationshipType; 
import org.neo4j.graphdb.ResourceIterator; 
import org.neo4j.graphdb.Transaction; 
import org.neo4j.graphdb.factory.GraphDatabaseFactory; 


public class PrincipaleEnglish { 


	public enum Rels implements RelationshipType 
	{ 
		link, crosslink 
	} 


	public static void main(String[] args) { 


		 
		String dbPath = "/Users/Documents/Neo4j/neo4j-community-3.0.7/data/databases/graph.db";


		GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory(); 
		System.out.println("Connecting to the database..."); 
		GraphDatabaseService graphDb = graphDbFactory.newEmbeddedDatabase(new File(dbPath)); 
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
		//System.out.println("Start visiting nodes..."); 
		try (Transaction tx = graphDb.beginTx()) { 
			/*for( Node n : graphDb.getAllNodes() ) { 
			long idn = n.getId(); 
			if (visited.contains(idn)) 
			continue; 
 }*/ 


			ResourceIterator<Node> it = graphDb.findNodes(Label.label("Article"), "title", "Flight planning"); 
			Node next = null; 
			while( it.hasNext() ) { 
				next = it.next(); 
				String lang = (String)next.getProperty("lang"); 
				if ( lang.equals("en") ) 
					break; 
			} 




			visit(graphDb, next, visited); 
			tx.success(); 
		} 


		System.out.println("Disconnecting from the database..."); 
		graphDb.shutdown(); 
		System.out.println("Done!"); 
	} 


	public static void visit(GraphDatabaseService graphDb, Node start, Set<Long> visited) { 
		int limit = 0; 
		for (Node nn : graphDb.traversalDescription().depthFirst().relationships(Rels.crosslink, Direction.OUTGOING).traverse(start).nodes()) { 
			System.out.println(nn.getProperty("title") + " -- " + nn.getProperty("lang")); 


		} 
		//return 0; 


	} 


} 
