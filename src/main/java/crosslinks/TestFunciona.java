package crosslinks;

import java.io.BufferedWriter; 
import java.io.File; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;



public class TestFunciona {
	
	String[] nombre = new String[3];
	

	public enum Rels implements RelationshipType 
	{ 
		/** 
		 * We only need crosslinks.  
		 */ 
		crosslink 
		
	} 
	public static void main(String[] args){
	
	
	//----- Declarar Variables----------//
			final String Art = "Article";
			int NumberCross = 0;
			//------------------------------------
	
	
	String dbPath = "/Users/Neo4j/neo4j-community-3.0.7/data/databases/graph.db";

	GraphDatabaseFactory graphDbFactory = new GraphDatabaseFactory();
	GraphDatabaseService graphDb = graphDbFactory.newEmbeddedDatabase(new File(dbPath));
	System.out.println("Connecting to the database..."); 
	
	System.out.println("Done!");
	
	//Node actor =  ((Object) graphDb).query( new TermQuery( new Term( "name", "Keanu Reeves" ) ) ).getSingle();

	

	Set<Long> visited = new HashSet<Long>(); 
	try (Transaction tx = graphDb.beginTx()) {
		
		final Label recordClassLabel = DynamicLabel.label(Art); 
		
		//ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Abraham Lincoln");
		//ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Abortion");
		ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Adobe");
		//System.out.print(((Node) graphDb).getId());
		
		Node next = null; 
		while( it.hasNext() ) { 
			next = it.next(); 
			String lang = (String)next.getProperty("lang"); 
			if ( lang.equals("en") ) 
				break; 
		} 

		visit(graphDb, next, visited, NumberCross); 
		tx.success(); 
		
		
	} 

		
	
	graphDb.shutdown(); 
	System.out.println("Done!");
	
	}
	public static void visit(GraphDatabaseService graphDb, Node start, Set<Long> visited, int NumberCross) { 
		int limit = 0; 
		for (Node nn : graphDb.traversalDescription().depthFirst().relationships(Rels.crosslink, Direction.OUTGOING).traverse(start).nodes()) { 
			System.out.println(nn.getProperty("title") + " -- " + nn.getProperty("lang")); 


		} 
		System.out.println("Number of Clls!"+ NumberCross);
	
	}

}
