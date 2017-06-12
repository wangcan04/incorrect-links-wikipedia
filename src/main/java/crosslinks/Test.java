package crosslinks;

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
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;

import crosslinks.Test3.Rels;



public class Test {


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
	//static List<String> ListWrongs = new ArrayList<String>();

	/** 
	 * Main 
	 */ 
	public static void main(String[] args) throws IOException
	{
		Test wikipedia = new Test();
		wikipedia.setUp();
//		wikipedia.PrintWikiArticles(); // find more than one node
		wikipedia.FindoneNode();      // find a node depending of wikiid 
		System.out.println(wikipedia.printNodeFriends(ListNode));
		wikipedia.printIncorrects(ListIncorrects);
		wikipedia.shutdown();

	} 
	private void printIncorrects(List<String> listIncorrects2) {
		for (int i=0;i<listIncorrects2.size();i++){
			System.out.println("List Wrongs !"+ listIncorrects2.get(i));
		}
		
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
			ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "wikiid", "71719");

			Node next = null; 
			while( it.hasNext() ) { 
				next = it.next(); 
				String lang = (String)next.getProperty("lang");
				if ( lang.equals("el") ) 
					break; 
			} 

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
//		List<String> ListNode = new ArrayList<String>();

		for (Node nn : graphDb.traversalDescription().depthFirst().relationships(Rels.crosslink, Direction.OUTGOING).traverse(start).nodes()) { 
//		for (Node nn : graphDb.traversalDescription().depthFirst().relationships(Rels.crosslink, Direction.BOTH).traverse(start).nodes()) { 

			NumberCross = NumberCross + 1;
//			listNode2.add((String) nn.getProperty("wikiid")); // add to ListNode the nodes
			aux2 = new ListArt();
			aux2.setWikiid((String) nn.getProperty("wikiid"));
			aux2.setLang((String) nn.getProperty("lang"));
			listNode2.add(aux2);

//			System.out.println(  "Article => "+nn.getProperty("title") + " -- " + nn.getProperty("lang")+ "---"+ nn.getId());
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
/*71719*/	ResourceIterator<Node> it = graphDb.findNodes(recordClassLabel, "wikiid", "12630");
/*el*/
/*572*/			Node next = null; 
/*en*/			while( it.hasNext() ) { 
/*12630*/		next = it.next(); 
/*en*/			String lang = (String)next.getProperty("lang");
				if ( lang.equals("en") ) 
					break; 

			} 
			Node start = next;
			aux = new ListArt();
			aux.setWikiid((String) start.getProperty("wikiid"));
			aux.setLang((String) start.getProperty("lang"));
			ListNode.add(aux);


			FindCl(graphDb, start, aux, NumberCross, ListNode);
			tx.success(); 

		}
		return ListNode;

	}

	public static ArrayList<ListArt> FindCl(GraphDatabaseService graphDb, Node start ,ListArt aux2,int NumberCross, ArrayList<ListArt> listNode2) { 



		TraversalDescription myFriends = graphDb.traversalDescription()
				.breadthFirst()
				.relationships(Rels.crosslink, Direction.OUTGOING)
				//				.evaluator( Evaluators.all() );
				.evaluator( Evaluators.atDepth( 1 ));
		Traverser traverser = myFriends.traverse( start );
//		System.out.println( start.getProperty("title")+ "  friends: " );
		for( Node friend : traverser.nodes() )
		{
			NumberCross = NumberCross + 1;
//			System.out.println( "\t" + friend.getProperty( "title" )+ "-- id--"+friend.getProperty("wikiid") );
			aux2 = new ListArt();
			aux2.setWikiid((String) friend.getProperty("wikiid"));
			aux2.setLang((String) friend.getProperty("lang"));
			listNode2.add(aux2);

		}

		//---------Print List--------------------------------------//
/*		System.out.println("Lista longitud!"+ listNode2.size() + "\n");
		for (int i=0;i<listNode2.size();i++){
			System.out.println("Lista Node !"+ listNode2.get(i).getWikiid()+ " lang! "+ listNode2.get(i).getLang() );
		}*/
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
//		System.out.println("\n--------------Second Method-------------- !"+ "\n");

		//----- Declarar Variables----------//

		final String Art = "Article";
		final Label recordClassLabel = DynamicLabel.label(Art);
		String FindNode = null;

		float Equals = 0;
		String NameNode = null;
		int numberOfFriends = 0;
		String output = null;
		int j=1;
		//---------Print List--------------------------------------//
		/*System.out.println("Segundo Metodo List !"+ listNode2.size() + "\n");
		for (int i=0;i<listNode2.size();i++){
			System.out.println("Segundo Lista Node !"+ listNode2.get(i).getWikiid()+ " lang! "+ listNode2.get(i).getLang() );
		}*/
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
	public String DetectIncorrects(ArrayList<ListArt> listNode2, Label recordClassLabel, String FindNode, float Equals, String NameNode, int numberOfFriends,
			String output, int j)
	{
		float NumberCross = 0;
		float percentage = 0;
		double ValidPerc = 0.60;
		int redi= 0;
		int singleton=0;
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


			//System.out.println("\nNode selected => "+ neoNode.getProperty("title") + " -wikiid-"+ neoNode.getProperty("wikiid"));


			TraversalDescription myFriends = graphDb.traversalDescription()
					.breadthFirst()
					.relationships(Rels.crosslink, Direction.OUTGOING)
					.evaluator( Evaluators.atDepth( 1 ) );
			Traverser traverser = myFriends.traverse( start );
			//System.out.println( start.getProperty("title")+ "  friends: " );
			for( Node friend : traverser.nodes() )
			{
				NumberCross = NumberCross + 1;
				//System.out.println( "\t" + friend.getProperty( "title" )+ "-- id--"+friend.getProperty("wikiid") );
			}
			
			//System.out.println( "numero cross: "+ NumberCross );
//--------------------------------------------------------------------------
			if(listNode2.size() == 2){
				for( Node friend : traverser.nodes() )
				{
					for (int i=0; i<listNode2.size(); i++){
						if (friend.getProperty("wikiid").equals(listNode2.get(i).getWikiid())){
							singleton ++;
							break;
						}
					}
					if(singleton == 0){
						//System.out.println("\nThe Article: " + start.getProperty("title")+ "- id "+start.getProperty("wikiid")+ " => is not Crosslink" );
						ListIncorrects.add((String) start.getProperty("wikiid")); // add to ListIncorrects the nodes
						break;
					}
				}
			}
//--------------------------------------------------------------------------
			if (listNode2.size() >= 3){
				for( Node friend : traverser.nodes() )
				{
					for (int i=0; i<1; i++){
						if(friend.getProperty("wikiid").equals(listNode2.get(0).getWikiid())){
							redi ++;
							break;
						}
					}
				}
					if(redi == 0){
						//System.out.println("\nThe Article: " + start.getProperty("title")+ "- id "+start.getProperty("wikiid")+ " => is not Crosslink" );
						ListIncorrects.add((String) start.getProperty("wikiid"));
					}
				

//----------------------------------------------------------------------------			
				if(redi > 0){
					for( Node friend : traverser.nodes() )
					{
						for (int i=0; i<listNode2.size(); i++){
							if (friend.getProperty("wikiid").equals(listNode2.get(i).getWikiid())){
								Equals ++;
								break;
							}
						}

					}
					//System.out.println( "Cross links match: "+ Equals );

					if(NumberCross >=3){
						percentage = Equals/NumberCross;
//						System.out.println( "Percentage "+ percentage );
						if(percentage < ValidPerc){
//							System.out.println("\nThe Article: " + start.getProperty("title")+ "- id "+start.getProperty("wikiid")+ " => is not Crosslink" );
							ListIncorrects.add((String) start.getProperty("wikiid"));
						}
					}
					if(NumberCross == 0){
//						System.out.println("\nThe Article: " + start.getProperty("title")+ "- id "+start.getProperty("wikiid")+ " => is not Crosslink" );
						ListIncorrects.add((String) start.getProperty("wikiid"));
					}
					if(NumberCross == 2 ){
						percentage = Equals/NumberCross;
//						System.out.println( "Percentage "+ percentage );
						if(percentage < ValidPerc){
							//System.out.println("\nThe Article: " + start.getProperty("title")+ "- id "+start.getProperty("wikiid")+ " => is not Crosslink" );
							ListIncorrects.add((String) start.getProperty("wikiid"));
						}
					}
				}
			}
		}
		return output;
	}
}

