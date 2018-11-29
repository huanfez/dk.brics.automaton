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
		}
		
		for (ArrayList<String> lang : commonEventsLangSet) {
			CharSequence lang_[] = lang.toArray(new CharSequence[lang.size()]);//Convert String to CharSequence
			commonEventsAutSet.add(BasicAutomata.makeStringUnion(lang_));//Synthesize automaton and collect it 
		}
		
		return commonEventsAutSet;
	}
	
//	public static void main(String args[]) 
//	{ 
//		// give a regular expression, output subautomata
//		RegExp regExpr = new RegExp("e(a(cb|b(c|d))|b(da|a(c|d)))");
//		System.out.print(commonEventsAut(regExpr.toAutomaton()));
//	}
}
