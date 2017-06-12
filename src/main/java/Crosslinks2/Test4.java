package Crosslinks2;

import java.io.File; 
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;



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
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;



/**
 * Este programa guarda los nombres de cada articulo en un arrayList normal
 * @author Ximena
 *
 */

public class Test4 {


	/** 
	 * Relationship types 
	 * Debemos definir los tipos de relaci�n que queremos almacenar.
	 *  En este caso vamos a crear unos enums de Java para los tipos de relaci�n que queremos manejar:
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
	
	static List<String> ListNode = new ArrayList<String>();

	public static void main(String[] args) throws IOException
	{
		Test4 wikipedia = new Test4();
		wikipedia.setUp();
		wikipedia.PrintWikiArticles();
		wikipedia.FindoneNode();
		System.out.println(wikipedia.printNodeFriends(ListNode));
		
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
	 * @return 
	 */
	public List<String>  PrintWikiArticles()
	{
		//----- Declarar Variables----------//
		final String Art = "Article";
		int NumberCross = 0;
		final Label recordClassLabel = DynamicLabel.label(Art); 
		//String[] myStringArray = new String[3];
		
		//---------------------------------//

		//Set<Long> visited = new HashSet<Long>(); 
		try (Transaction tx = graphDb.beginTx()) {

			
			//ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Abraham Lincoln");
			ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Aborto inducido");
			//ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Blizzard");
			//Node start = graphDb.findNode(recordClassLabel, "title", "Abortion");
		
			

			Node next = null; 
			while( it.hasNext() ) { 
				next = it.next(); 
				String lang = (String)next.getProperty("lang");
				if ( lang.equals("es") ) 
				break; 
			} 

			visit(graphDb, next, /*visited,*/ NumberCross, ListNode);
			
			tx.success(); 
			
		}
		return ListNode;

	}
	public static List<String> visit(GraphDatabaseService graphDb, Node start,/* Set<Long> visited,*/ int NumberCross, List<String> ListNode) { 
		//List<String> ListNode = new ArrayList<String>();
		for (Node nn : graphDb.traversalDescription().depthFirst().relationships(Rels.crosslink, Direction.OUTGOING).traverse(start).nodes()) { 
			NumberCross = NumberCross + 1;
			ListNode.add((String) nn.getProperty("title")); // add to ListNode the nodes
			System.out.println(  "Article => "+nn.getProperty("title") + " -- " + nn.getProperty("lang"));

		} 
		System.out.println("Number of Clls!"+ NumberCross + "\n");
		
		//---------Print List--------------------------------------//
		System.out.println("Lista longitud!"+ ListNode.size() + "\n");
		for (int i=0;i<ListNode.size();i++){
		System.out.println("Lista Node !"+ ListNode.get(i) + "\n");
		}
		//--------------------------------------------------------//

		return ListNode;

	}

	/**
	 * Encuentra los crosslinks del nodo buscado
	 * @return
	 */
	public String printNodeFriends(List<String> ListNode)
	{

		//---------Print List--------------------------------------//
		System.out.println("Segundo Metodo List !"+ ListNode.size() + "\n");
		for (int i=0;i<ListNode.size();i++){
		System.out.println("Segundo Lista Node !"+ ListNode.get(i) );
		}
		//--------------------------------------------------------//
		
		//----- Declarar Variables----------//
		
		final String Art = "Article";
		final Label recordClassLabel = DynamicLabel.label(Art);
		
		//---------------------------------//
		try ( Transaction tx = graphDb.beginTx() )
		{

			//Node neoNode = (Node) graphDb.findNodes(recordClassLabel, "title", "Abortion");
			ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Aborto");
			Node neoNode 			  = null; 
			
			while( it.hasNext() )
			{ 
				neoNode = it.next(); 
				String lang = (String)neoNode.getProperty("lang"); 
				if ( lang.equals("es") ) 
				break; 
			} 
			
			System.out.println("Node selected => "+ neoNode.getProperty("title"));
			int numberOfFriends = 0;
			String output			   = neoNode.getProperty( "title" ) + " Cross-links:\n";
			Traverser friendsTraverser = getFriends( neoNode );
			for ( Path friendPath : friendsTraverser )
			{
				output += "At depth " + friendPath.length() + " => "
					   + friendPath.endNode()
						.getProperty( "title" )+" -- " + friendPath.endNode().getProperty("lang") + "\n";
				numberOfFriends++;
			}
			// System.out.println(output);
			numberOfFriends++;
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
				.relationships( Rels.crosslink, Direction.OUTGOING)
				.evaluator( Evaluators.excludeStartPosition() );
		return td.traverse( person );
	}
	
	
	/*---------------------------------------------------------------------------------------
	---------------Find one Node-----------------------------------------------------------*/
	
	private List<String> FindoneNode() 
	{
		//----- Declarar Variables----------//
		final String Art = "Article";
		int NumberCross = 0;
		final Label recordClassLabel = DynamicLabel.label(Art); 


		try (Transaction tx = graphDb.beginTx()) {
			Node start = graphDb.findNode(recordClassLabel, "title", "Abortion");
			//Node start2 = graphDb.findNode(recordClassLabel, "wikiid", "39238");
			ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "wikiid", "765");
			
			Node next = null; 
			while( it.hasNext() ) { 
				next = it.next(); 
				String lang = (String)next.getProperty("lang");
				if ( lang.equals("en") ) 
				break; 
				 
			} 
			Node start2 = next;

			FindCl(graphDb, start2, NumberCross, ListNode);
			tx.success(); 

		}
		return ListNode;

	}
	public static List<String> FindCl(GraphDatabaseService graphDb, Node start2 ,int NumberCross, List<String> ListNode) { 

		TraversalDescription myFriends = graphDb.traversalDescription()
				.breadthFirst()
				.relationships(Rels.crosslink, Direction.OUTGOING)
				.evaluator( Evaluators.atDepth( 1 ) );
		Traverser traverser = myFriends.traverse( start2 );
		System.out.println( start2.getProperty("title")+ "  friends: " );
		for( Node friend : traverser.nodes() )
		{
			NumberCross = NumberCross + 1;
			System.out.println( "\t" + friend.getProperty( "title" ) );
			ListNode.add((String) friend.getProperty("title")); // add to ListNode the nodes
		}

		//---------Print List--------------------------------------//
		System.out.println("Lista longitud!"+ ListNode.size() + "\n");
		for (int i=0;i<ListNode.size();i++){
			System.out.println("Lista Node !"+ ListNode.get(i) + "\n");
		}
		//--------------------------------------------------------//

		return ListNode;

	}


}
