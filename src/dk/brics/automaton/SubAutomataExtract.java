package dk.brics.automaton;

import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.lang.CharSequence;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.SpecialOperations;
import dk.brics.automaton.BasicAutomata;

public class SubAutomataExtract {
	
	/********Get events set of string*********** /
	 * 
	 * @param str
	 * @return
	 */
	public static Set<Character> getStrEventSet(String str) {
		Set<Character> strEventSet = new HashSet<Character>();
		
		for(char event : str.toCharArray())
			strEventSet.add(event);
		
		return strEventSet;
	}
	
	/********Extract automata with common events set *********** /
	 * //
	 * @param automaton
	 * @return
	 */
	public static Set<Automaton> commonEventsAut(Automaton automaton) {
		Set<Automaton> commonEventsAutSet = new HashSet<Automaton>();		
		Set<String> autAllPaths = SpecialOperations.getFiniteStrings(automaton);// Obtain all paths of automaton
		Set<Character> strEventSet;
		Set<ArrayList<String>> commonEventsLangSet = new HashSet<ArrayList<String>>();
		
		for (String path : autAllPaths) {
			boolean addNew = true;//Flag for new subautomaton
			strEventSet = getStrEventSet(path);
			
			System.out.print(path + " " + strEventSet + " " + "-");
			
			for (ArrayList<String> element : commonEventsLangSet) { //check whether sublanguage set contains qualified paths
				System.out.print(getStrEventSet(element.get(0)) + " " + getStrEventSet(element.get(0)).equals(strEventSet) + "--");
				if (element.get(0).length() == path.length() && getStrEventSet(element.get(0)).equals(strEventSet)) {
					element.add(path);
					addNew = false;
					break;
				}
			}
			
			if (addNew == true)
				commonEventsLangSet.add(new ArrayList<>(Arrays.asList(path)));
			System.out.print(commonEventsLangSet + "\n");		
		}
		
		for (ArrayList<String> lang : commonEventsLangSet) {
			CharSequence lang_[] = lang.toArray(new CharSequence[lang.size()]);//Convert String to CharSequence
			commonEventsAutSet.add(BasicAutomata.makeStringUnion(lang_));//Synthesize automaton and collect it 
		}
		
		return commonEventsAutSet;
	}
	
	public static void main(String args[]) 
	{ 
		// give a regular expression, output subautomata
		RegExp regExpr = new RegExp("e(a(cb|b(c|d))|b(da|a(c|d)))");
		System.out.print(commonEventsAut(regExpr.toAutomaton()));
	}
}

///********Remove backwards transitions and self-loop *********** /
//* 
//* @param aut
//* @return
//*/
//public static DirectedAcyclicGraph<String,String> ConvertDAG(Automaton automaton) 
//{ 
//	DirectedAcyclicGraph<String,String> dag = 
//			new DirectedAcyclicGraph<String, String>(String.class);
//	
//	// Create a FIFO queue for BFS 
//	LinkedList<State> queue = new LinkedList<State>(); 
//	//Set<State> visitedSet = new HashSet<State>();
//	State currentState;
//	String currentVertex;
//	
//	// Mark the initial state to be visited and enqueue it 
//	queue.add(automaton.getInitialState()); 
//	dag.addVertex(automaton.getInitialState().toString());
//
//	// Breadth-first search
//	while (queue.size() != 0) 
//	{ 
//		// Dequeue a state from queue and print it 
//		currentState = queue.poll();
//		currentVertex = currentState.toString();
//		System.out.print(currentState+" "); 
//
//		// Get all successive states of the dequeued state rootState 
//		// If a successive state has not been visited, then mark it visited and enqueue it 
//		for (Transition transition : currentState.getTransitions())
//		{
//			String event = "";
//			for (char tran = transition.getMin(); tran <= transition.getMax(); tran++)
//				event += tran;
//			
//			//if (visitedSet.contains(transition.getDest()))
//			
//			if (dag.getAncestors(currentVertex).contains(transition.getDest().toString()))
//				continue;
//
//			dag.addVertex(transition.getDest().toString());//may be repeated vertex
//			dag.addEdge(currentVertex, transition.getDest().toString(), event);
//			
//			if (queue.contains(transition.getDest()) == false)
//				queue.add(transition.getDest());
//		}
//		
//		//visitedSet.add(currentState);
//	} 
//	
//	return dag;
//}
//
//public static Automaton acyclicAutomaton(Automaton automaton) 
//{ 
//	DirectedAcyclicGraph<String,String> dag = 
//			new DirectedAcyclicGraph<String, String>(String.class);
//	
//	// Create a FIFO queue for BFS 
//	LinkedList<State> queue = new LinkedList<State>(); 
//	Set<State> visitedSet = new HashSet<State>();
//	State currentState;
//	
//	// Mark the initial state to be visited and enqueue it 
//	queue.add(automaton.getInitialState()); 
//
//	// Breadth-first search
//	while (queue.size() != 0) 
//	{ 
//		// Dequeue a state from queue and print it 
//		currentState = queue.poll();
//		System.out.print(currentState+" "); 
//
//		// Get all successive states of the dequeued state rootState 
//		// If a successive state has not been visited, then mark it visited and enqueue it 
//		for (Transition transition : currentState.getTransitions())
//		{				
//			if (visitedSet.contains(transition.getDest()))
//				continue;
//			
//			if (queue.contains(transition.getDest()) == false)
//				queue.add(transition.getDest());
//		}
//		
//		visitedSet.add(currentState);
//	} 
//	
//	return dag;
//}
//
///********Return acyclic automaton *********** /
//* 
//* @param aut
//* @return
//*/
//public static List<GraphPath<String, DefaultEdge>> nonSelfIntPaths(Automaton automaton) {
//	Graph<String, DefaultEdge> directedGraph = 
//			new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
//	 List<GraphPath<String, DefaultEdge>> allPaths = 
//			 new ArrayList<GraphPath<String, DefaultEdge>>();
//	
//	//s
//	for (State state : automaton.getStates())
//	{
//		directedGraph.addVertex(state.toString());
//		
//		for (Transition transition : state.getTransitions())//if transition has multiple ones?
//		{
//			directedGraph.addVertex(transition.getDest().toString());
//			directedGraph.addEdge(state.toString(), transition.getDest().toString());
//		}
//	}
//	
//	//System.out.print(directedGraph);
//	System.out.print(automaton);
//	Automaton automatonClone = automaton.clone();
//	SpecialOperations.reverse(automatonClone);
//	
//	automatonClone.getInitialState().equals(automaton.getAcceptStates());
//	System.out.print(automatonClone.getInitialState());
//	System.out.print(automaton.getAcceptStates());
//	
//	AllDirectedPaths<String, DefaultEdge> allDirectedPaths = 
//			new AllDirectedPaths<String, DefaultEdge>(directedGraph);
//	
//	System.out.print(allDirectedPaths);
//	
//	allPaths = allDirectedPaths.getAllPaths(automaton.getInitialState().toString(),
//			automaton.getAcceptStates().toString(), true, null);
//	 
//	return allPaths;
//}