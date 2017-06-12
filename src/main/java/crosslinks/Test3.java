package crosslinks;

import java.io.BufferedWriter; 
import java.io.File; 
import java.io.FileWriter; 
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
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
import crosslinks.Test.Rels;



public class Test3 {


	/** 
	 * Relationship types 
	 * We must to define the RelationshipType that we want to stock.
	 * In this case we will store sole Enmus of Java for the relation that we want to manage:
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
	static ArrayList<ListArt> ListNode = 	new ArrayList<ListArt>();
	//static List<String> ListNode = new ArrayList<String>();
	static List<String> ListIncorrects = new ArrayList<String>();

	/** 
	 * Main 
	 */ 
	public static void main(String[] args) throws IOException
	{
		Test wikipedia = new Test();
		wikipedia.setUp();
		wikipedia.PrintWikiArticles(); // find more than one node
		//wikipedia.FindoneNode();      // find a node depending of wikiid 
		System.out.println(wikipedia.printNodeFriends(ListNode));
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

	/**
	 * Print the cross-links of the article
	 */
	public ArrayList<ListArt>  PrintWikiArticles()
	{
		//----- Declare variables----------//
		final String Art = "Article";
		int NumberCross = 0;
		final Label recordClassLabel = DynamicLabel.label(Art); 
		ListArt aux = null;
		//String[] myStringArray = new String[3];

		//---------------------------------//

		//Set<Long> visited = new HashSet<Long>(); 
		try (Transaction tx = graphDb.beginTx()) {


			//ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Adobe");
			//ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Abraham Lincoln");
			//ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Blizzard");

			//ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "title", "Agricultural science");
			ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "wikiid", "765");
			
			Node next = null; 
			while( it.hasNext() ) { 
				next = it.next(); 
				String lang = (String)next.getProperty("lang");
				if ( lang.equals("en") ) 
				break; 
			} 

			//visit(graphDb, next, /*visited,*/ NumberCross, ListNode);
			visit(graphDb, next, /*visited,*/ NumberCross, aux, ListNode);

			tx.success(); 

		}
		return ListNode;

	}
	
	/**
	 * print each node with the relationship "Cross-link" while adding each node to ListNode
	 * Return a List of nodes
	 * @param listNode2 
	 */
	public static ArrayList<ListArt> visit(GraphDatabaseService graphDb, Node start,/* Set<Long> visited,*/ int NumberCross, ListArt aux2, ArrayList<ListArt> listNode2) { 
		//List<String> ListNode = new ArrayList<String>();
		
		for (Node nn : graphDb.traversalDescription().depthFirst().relationships(Rels.crosslink, Direction.OUTGOING).traverse(start).nodes()) { 
			NumberCross = NumberCross + 1;
			//listNode2.add((String) nn.getProperty("wikiid")); // add to ListNode the nodes
			aux2 = new ListArt();
			aux2.setWikiid((String) nn.getProperty("wikiid"));
			aux2.setLang((String) nn.getProperty("lang"));
			listNode2.add(aux2);

			//System.out.println(  "Article => "+nn.getProperty("title") + " -- " + nn.getProperty("lang")+ "---"+ nn.getId());
			System.out.println(  "Article => "+nn.getProperty("wikiid") + " -- " + nn.getProperty("lang")+ "---"+ nn.getId());

		} 
		System.out.println("Number of Clls!"+ NumberCross + "\n");

		//--------------------Print List--------------------------------------//
		System.out.println("Lista length!"+ listNode2.size() + "\n");
		for (int i=0;i<listNode2.size();i++){
			System.out.println("Lista Node !"+ listNode2.get(i).getWikiid());
		}
		//-------------------------------------------------------------------//

		return listNode2;

	}
//------------------------------------------------------------------------------------------------------//
// 									Find one Node     													//
//------------------------------------------------------------------------------------------------------//
	
	private ArrayList<ListArt> FindoneNode() 
	{
		//----- Declarar Variables----------//
		final String Art = "Article";
		int NumberCross = 0;
		final Label recordClassLabel = DynamicLabel.label(Art); 
		ListArt aux = null;


		try (Transaction tx = graphDb.beginTx()) {
			ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "wikiid", "2433561");

			Node next = null; 
			while( it.hasNext() ) { 
				next = it.next(); 
				String lang = (String)next.getProperty("lang");
				if ( lang.equals("es") ) 
					break; 

			} 
			Node start = next;

			FindCl(graphDb, start, aux, NumberCross, ListNode);
			tx.success(); 

		}
		return ListNode;

	}
	
	public static ArrayList<ListArt> FindCl(GraphDatabaseService graphDb, Node start ,ListArt aux2,int NumberCross, ArrayList<ListArt> listNode2) { 
		
		

		TraversalDescription myFriends = graphDb.traversalDescription()
				.breadthFirst()
				.relationships(Rels.crosslink, Direction.OUTGOING)
				.evaluator( Evaluators.atDepth( 1 ) );
		Traverser traverser = myFriends.traverse( start );
		System.out.println( start.getProperty("title")+ "  friends: " );
		for( Node friend : traverser.nodes() )
		{
			NumberCross = NumberCross + 1;
			System.out.println( "\t" + friend.getProperty( "title" )+ "-- id--"+friend.getProperty("wikiid") );
			//ListNode.add((String) friend.getProperty("title")); // add to ListNode the nodes
			//ListNode.add((String) friend.getProperty("wikiid"),(String) friend.getProperty("lang") ); // add to ListNode the nodes
			aux2 = new ListArt();
			aux2.setWikiid((String) friend.getProperty("wikiid"));
			aux2.setLang((String) friend.getProperty("lang"));
			listNode2.add(aux2);

		}

		//---------Print List--------------------------------------//
		System.out.println("Lista longitud!"+ listNode2.size() + "\n");
		for (int i=0;i<listNode2.size();i++){
			System.out.println("Lista Node !"+ listNode2.get(i).getWikiid()+ " lang! "+ listNode2.get(i).getLang() );
		}
		//--------------------------------------------------------//

		return listNode2;

	}
	
	
		
//------------------------------------------------------------------------------------------------------//
//									printNodeFriends													//
//------------------------------------------------------------------------------------------------------//

	/**
	 * Second method to Find the cross link of one specific node
	 * @return
	 */
	
	public String printNodeFriends(ArrayList<ListArt> listNode2)
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
		int j=0;

		//---------------------------------//

		//---------Print List--------------------------------------//
		System.out.println("Segundo Metodo List !"+ listNode2.size() + "\n");
		for (int i=0;i<listNode2.size();i++){
			System.out.println("Segundo Lista Node !"+ listNode2.get(i).getWikiid()+ " lang! "+ listNode2.get(i).getLang() );
		}
		//--------------------------------------------------------//
		
		while (j <listNode2.size()){
			DetectIncorrects( listNode2,recordClassLabel,FindNode,Equals,NameNode, numberOfFriends, output, j);
			j++;
		}
		return output;

	}
	
	/**
	 * DetectIncorrects: detect the corrects and Incorrect Clls
	 * @return
	 */
	public String DetectIncorrects(ArrayList<ListArt> listNode2, Label recordClassLabel, String FindNode, int Equals, String NameNode, int numberOfFriends,
								  String output, int j)
	{
		int NumberCross = 0;
		try ( Transaction tx = graphDb.beginTx() )
		{
			FindNode = listNode2.get(j).getWikiid();
		
			//Node neoNode = (Node) graphDb.findNodes(recordClassLabel, "title", "Abortion");
			ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "wikiid", FindNode);
			
			Node neoNode 			  = null; 
			while( it.hasNext() )
			{ 
				neoNode = it.next(); 
				String lang = (String)neoNode.getProperty("lang"); 
				if ( lang.equals(listNode2.get(j).getLang()) ) 
					break;
			} 
			Node start = neoNode;

			
			System.out.println("\n Node selected => "+ neoNode.getProperty("title") + " -wikiid-"+ neoNode.getProperty("wikiid"));

			
			TraversalDescription myFriends = graphDb.traversalDescription()
					.breadthFirst()
					.relationships(Rels.crosslink, Direction.OUTGOING)
					.evaluator( Evaluators.atDepth( 1 ) );
			Traverser traverser = myFriends.traverse( start );
			System.out.println( start.getProperty("title")+ "  friends: " );
			for( Node friend : traverser.nodes() )
			{
				NumberCross = NumberCross + 1;
				System.out.println( "\t" + friend.getProperty( "title" )+ "-- id--"+friend.getProperty("wikiid") );
			}
			
			for( Node friend : traverser.nodes() )
			{
				//System.out.println( "numero cross"+ NumberCross );
				if(NumberCross >3){
					for (int i=0; i<listNode2.size(); i++){
						if (friend.getProperty("wikiid").equals(listNode2.get(i).getWikiid())){
							Equals ++;
							break;
						}
					}
				}
				//System.out.println( "\t" + friend.getProperty( "title" )+ "-- id--"+friend.getProperty("wikiid") );
			}
			if(Equals==0){
				System.out.println("\nThe Article: " + start.getProperty("title")+ "- id "+start.getProperty("wikiid")+ " => is not Crosslink" );

			}
			

			output = neoNode.getProperty( "title" )+" -wikiid-"+ neoNode.getProperty("wikiid") + "- Cross-links:\n";
			Traverser friendsTraverser = getFriends( neoNode );
			 //friendsTraverser
			for ( Path friendPath : friendsTraverser )
			{
				/*	output += friendPath.endNode()
								.getProperty( "title" ) + "\n";*/
				//NameNode= (String) friendPath.endNode().getProperty( "title" ) ;
				NameNode= (String) friendPath.endNode().getProperty( "wikiid" ) ;
				
				for (int i=0; i<listNode2.size(); i++){
					if (Equals >=3){
						break;
					}
					if (NameNode.equals(listNode2.get(i))){
						//ListNode.remove(i);
						Equals ++;
						break;
					}
				}
				if (Equals >=3){
					System.out.println("\nThe Article: " + neoNode.getProperty("title")+
							" -wikiid-"+ neoNode.getProperty("wikiid")+"=> is Crosslink"  + " ID "+ neoNode.getId() );
					break;
				}
				numberOfFriends++;
			}

			// System.out.println(output);
			numberOfFriends++;
			//output += "Number of friends found: " + numberOfFriends + "\n";
			
			if (Equals == 0){
				System.out.println("\nThe Article: " + neoNode.getProperty("title")+ "=> is not Crosslink" + " ID "+ neoNode.getId() );
				//ListNode.add((String) neoNode.getProperty("title"), neoNode.getId());

			}
			return output;
		}
	}

	//========================== START SNIPPET: get-friends=====================================

	private Traverser getFriends(
			final Node person )
	{
		TraversalDescription td = graphDb.traversalDescription()
				.breadthFirst()
				.relationships( Rels.crosslink, Direction.OUTGOING )
				.evaluator( Evaluators.excludeStartPosition() );
		return td.traverse( person );
	}
	//========================== START SNIPPET: get-friends=====================================

	public void Incorrects(int numberOfFriends)
	{
		System.out.println("numero de amigos "+ numberOfFriends);
		if(numberOfFriends >= 2){

		}

	}

}
