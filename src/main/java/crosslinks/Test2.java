package crosslinks;

import java.io.BufferedWriter; 
import java.io.File; 
import java.io.FileWriter; 
import java.io.IOException; 
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.neo4j.cypher.internal.frontend.v3_0.ast.functions.Labels;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.index.Index;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.graphdb.index.IndexManager;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.io.fs.FileUtils;

import crosslinks.NewMatrix.RelTypes;

/**
 * Esta clase tiene dos metodos para buscar nodos, el segundo sin recibir nada
 */


public class Test2 {


	/** 
	 * Relationship types 
	 * Debemos definir los tipos de relación que queremos almacenar.
	 *  En este caso vamos a crear unos enums de Java para los tipos de relación que queremos manejar:
	 */ 
	public enum Rels implements RelationshipType 
	{ 
		/** 
		 * We only need crosslinks.  
		 */ 
		crosslink 

	} 

	private static final File dbPath = new File( "/Users/Neo4j/neo4j-community-3.0.7/data/databases/graph.db");

	private GraphDatabaseService graphDb;

	public static void main(String[] args) throws IOException
	{
		Test wikipedia = new Test();
		wikipedia.setUp();
		//System.out.println( wikipedia.PrintWikiArticles() );
		wikipedia.PrintWikiArticles();
		//System.out.println(wikipedia.printNodeFriends());
		wikipedia.shutdown();
	} 
	public void setUp() throws IOException
	{

		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( dbPath );
		//registerShutdownHook();

		System.out.println("Connecting to the database..."); 
		System.out.println("Done!");

	}    

	public void shutdown()
	{
		graphDb.shutdown();
		System.out.println("Shutdown-Done!");
	}

	/**
	 * Imprimir los crossLinks del articulo
	 * Print the crosslinks of the article
	 */
	public void  PrintWikiArticles()
	{
		//----- Declarar Variables----------//
		final String Art = "Article";
		int NumberCross = 0;
		//---------------------------------//

		Set<Long> visited = new HashSet<Long>(); 
		try (Transaction tx = graphDb.beginTx()) {

			final Label recordClassLabel = DynamicLabel.label(Art); 
			//ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Abraham Lincoln");
			//ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Abortion");
			ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Blizzard");
			//Node neoNode = graphDb.findNode(recordClassLabel, "title", "Abortion");

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

	}

	public static void visit(GraphDatabaseService graphDb, Node start, Set<Long> visited, int NumberCross) { 
		//NumberCross =0;
		for (Node nn : graphDb.traversalDescription().depthFirst().relationships(Rels.crosslink, Direction.OUTGOING).traverse(start).nodes()) { 
			NumberCross = NumberCross + 1;
			System.out.println(nn.getProperty("title") + " -- " + nn.getProperty("lang")); 
		} 
		System.out.println("Numero de Clls!"+ NumberCross);
		//return NumberCross;

	}

	/**
	 * Encuentra los crosslinks del nodo buscado
	 * @return
	 */
	public String printNodeFriends()
	{
		//----- Declarar Variables----------//
		final String Art = "Article";
		//---------------------------------//
		try ( Transaction tx = graphDb.beginTx() )
		{

			final Label recordClassLabel = DynamicLabel.label(Art);

			Node neoNode = graphDb.findNode(recordClassLabel, "title", "Blizzard");
			System.out.println("Node selected => "+ neoNode.getProperty("title"));
			int numberOfFriends = 0;
			String output = neoNode.getProperty( "title" ) + " Cross-links:\n";
			Traverser friendsTraverser = getFriends( neoNode );
			for ( Path friendPath : friendsTraverser )
			{
				output += "At depth " + friendPath.length() + " => "
						+ friendPath.endNode()
						.getProperty( "title" )+" -- " + friendPath.endNode().getProperty("lang") + "\n";
				numberOfFriends++;
			}
			// System.out.println(output);
			output += "Number of friends found: " + numberOfFriends + "\n";
			// END SNIPPET: friends-usage
			return output;
		}
	}

	// START SNIPPET: get-friends
	private Traverser getFriends(
			final Node person )
	{
		TraversalDescription td = graphDb.traversalDescription()
				.breadthFirst()
				.relationships( Rels.crosslink, Direction.OUTGOING )
				.evaluator( Evaluators.excludeStartPosition() );
		return td.traverse( person );
	}
	// END SNIPPET: get-friends

}
