package Add;


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
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;


public class Add{


	/** 
	 * Relationship types 
	 * We must to define the RelationshipType that we want to stock.
	 * In this case we will store sole Enmus of Java for the relation that we want to manage:
	 */ 
	public enum Rels2 implements RelationshipType 
	{ 
		/** 
		 * We only need crosslinks.  
		 */ 
		crosslink 

	} 

	private static final File dbPath = new File( "/Users/Neo4j/neo4j-community-3.0.7/data/databases/graph.db");

	private GraphDatabaseService graphDb;

	static List<String> ListNode = new ArrayList<String>();

	/** 
	 * Main 
	 */ 
	public static void main(String[] args) throws IOException
	{
		Add wikipedia = new Add();
		wikipedia.setUp();
		//wikipedia.PrintWikiArticles();
		//System.out.println(wikipedia.addno());
		wikipedia.Remove();
		wikipedia.shutdown();

	} 
	/** 
	 * SetUp: Connect to the DataBase
	 */ 
	public void setUp() throws IOException
	{

		graphDb = new GraphDatabaseFactory().newEmbeddedDatabase( dbPath );
		//registerShutdownHook();

		System.out.println("Connecting to the database..."); 
		System.out.println("Done!");

	}    
	/** 
	 * Shutdown: ShutDown the database
	 */ 
	public void shutdown()
	{
		graphDb.shutdown();
		System.out.println("Shutdown-Done!");
	}

		
	public String addno()
	{
		System.out.println("\n--------------Second Method-------------- !"+ "\n");

		//----- Declarar Variables----------//

		final String Art = "Article";
		final Label recordClassLabel = DynamicLabel.label(Art);
		String FindNode = null;
		int Equals = 0;
		String NameNode = null;
		int numberOfFriends = 0;
		String output = null;
		int j=1;
		
	
		try ( Transaction tx = graphDb.beginTx() )
		{
			
			//Node newNode = (Node) graphDb.findNodes(recordClassLabel, "title", "Abortion");
			
			Node newNode=graphDb.createNode();
			newNode.setProperty( "title", "Prueba" );
			newNode.setProperty( "lang", "en" );
			ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Aborto");

			Node neoNode 			  = null; 
			while( it.hasNext() )
			{ 
				neoNode = it.next(); 
				String lang = (String)neoNode.getProperty("lang"); 
			} 
			//Create Relation with principal node
			neoNode.createRelationshipTo( newNode, Rels2.crosslink );
			
			System.out.println("\n Node selected => "+ neoNode.getProperty("title"));

			output = neoNode.getProperty( "title" ) + "- Cross-links:\n";
			Traverser friendsTraverser = getFriends( neoNode );
			 //friendsTraverser
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
			
			
			tx.success();
			return output;
		}
	}
	

	//========================== START SNIPPET: get-friends=====================================

	private Traverser getFriends(
			final Node person )
	{
		TraversalDescription td = graphDb.traversalDescription()
				.breadthFirst()
				.relationships( Rels2.crosslink, Direction.OUTGOING )
				.evaluator( Evaluators.excludeStartPosition() );
		return td.traverse( person );
	}

	//========================== START SNIPPET: Remove =====================================

	
	public void Remove()
	{
		System.out.println("\n--------------Second Method-------------- !"+ "\n");

		//----- Declarar Variables----------//

		final String Art = "Article";
		final Label recordClassLabel = DynamicLabel.label(Art);
		int numberOfFriends = 0;
		String output = null;
		String NameNode;
		
		try ( Transaction tx = graphDb.beginTx() )
		{
			
			//Node newNode = (Node) graphDb.findNodes(recordClassLabel, "title", "Abortion");
			
			ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Aborto");

			Node neoNode 			  = null; 
			while( it.hasNext() )
			{ 
				neoNode = it.next(); 
				String lang = (String)neoNode.getProperty("lang"); 
			} 
			
			System.out.println("\n Node selected => "+ neoNode.getProperty("title"));

			output = neoNode.getProperty( "title" ) + "- Cross-links:\n";
			Traverser friendsTraverser = getFriends( neoNode );
			 //friendsTraverser
			for ( Path friendPath : friendsTraverser )
			{
				output += "At depth " + friendPath.length() + " => "
					   + friendPath.endNode()
						.getProperty( "title" )+" -- " + friendPath.endNode().getProperty("lang") + "\n";
				
				NameNode = (String) friendPath.endNode()
						.getProperty( "title" );
				
				if (NameNode.equals("Prueba")){
					//friendPath.getSingleRelationship(Rels2.crosslink, Direction.OUTGOING ).delete();
					friendPath.endNode().delete();
				}
				numberOfFriends++;
			}
			// System.out.println(output);
			numberOfFriends++;
			output += "Number of friends found: " + numberOfFriends + "\n";
			
			
			tx.success();
			
		}
	}
	
	

}
