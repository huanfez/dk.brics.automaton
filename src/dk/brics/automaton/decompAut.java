/*
 * dk.brics.automaton
 * 
 * Copyright (c) 2001-2017 Anders Moeller
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR ``AS IS'' AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
 * THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
package dk.brics.automaton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import dk.brics.automaton.Automaton;
import dk.brics.automaton.RegExp;
import dk.brics.automaton.ShuffleOperations;
import dk.brics.automaton.SpecialOperations;
import dk.brics.automaton.State;
import dk.brics.automaton.Transition;

/**
* Parallel decomposition of an automaton 
*/

public class decompAut{
	
	/***Get the event set of automaton******/
	public static Set<Character> getEventSet(Automaton aut) {
		Set<Character> globalEventSet = new HashSet<Character>();
		
		for(State sta : aut.getStates())
			for(Transition tra : sta.getTransitions())
				for(int event = (int)tra.getMin(); event <= (int)tra.getMax(); event++)
					globalEventSet.add((char)event);
		
		return globalEventSet;
	}
	
	/***powerSet generation******/
	public static Set<Set<Character>> powerSet(Set<Character> originalSet) {
        Set<Set<Character>> sets = new HashSet<Set<Character>>();
        
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<Character>());
            return sets;
        }
        
        List<Character> list = new ArrayList<Character>(originalSet);
        Character head = list.get(0);
        Set<Character> rest = new HashSet<Character>(list.subList(1, list.size()));
        for (Set<Character> set : powerSet(rest)) {
            Set<Character> newSet = new HashSet<Character>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }
        
        return sets;
    }
	
	/***PowerSet to Sorted ArrayList******/
	public static List<Set<Character>> powerSetToArrayList(Set<Set<Character>> powerSet){	  
		List<Set<Character>> powerArrayList = new ArrayList<Set<Character>>();
		powerArrayList.addAll(powerSet);
		
		Collections.sort(powerArrayList, new Comparator<Set<Character>>(){
				@Override
				public int compare(Set<Character> a, Set<Character> b) {
					return a.size() - b.size();// Comparison
					}
				});
		//System.out.println(powerArrayList);
		
		return powerArrayList;
	}
	
	/***Sorted PowerSet(ArrayList) to Binary Event Pairs******/
	public static List<List<Set<Character>>> getEventSetPair(List<Set<Character>> powerArrayList, 
			Set<Character> eventSet){
		List<List<Set<Character>>> PairArrayList = new ArrayList<List<Set<Character>>>();
		int powerIndxCnt = 0;
		
		for (Set<Character> eventSubset : powerArrayList) {
			if (powerIndxCnt++ >= powerArrayList.size()/2)
				break;
			
			Set<Character> eventSetPairs = new HashSet<Character>();
			eventSetPairs.addAll(eventSet);
			eventSetPairs.removeAll(eventSubset);

			@SuppressWarnings("serial")
			List<Set<Character>> eventPairArray = new ArrayList<Set<Character>>() {{
				add(eventSubset); 
				add(eventSetPairs);}};
				  
			//System.out.println(eventPairArray);
			PairArrayList.add(eventPairArray);
		}
		//System.out.println(PairArrayList);
		
		return PairArrayList;
	}
	
	/***Iterative Parallel Decomposition*********************/
	public static List<Automaton> paraDecomp(Automaton gloAut){
		///***Initial Regular Expression and Automaton******///
		//RegExp regExpr = new RegExp("a(b|(cd)*)");
		//RegExp regExpr = new RegExp("a(bd|db)|b(ad|da)|d(ab|ba)");
		//RegExp regExpr = new RegExp("(bd)|(db)");
		
		//Automaton gloAut = regExpr.toAutomaton();
		System.out.println("The global automaton is:" + gloAut);
		//System.out.println(getEventSet(gloAut));
	  
		///***get Automaton Event Set******///
		Set<Character> gEventSet = new HashSet<Character>();
		gEventSet.addAll(getEventSet(gloAut));
	  
		/////////////////////***Begin Iterative Decomposition******//////////////////////
		Set<Character> IterGEventSet = new HashSet<Character>();
		IterGEventSet.addAll(gEventSet);		
		Automaton IterGloAut = gloAut.clone();
		boolean DECOMFLAG = false;
		List<Automaton> subAutSet = new ArrayList<Automaton>();
		
		while (IterGEventSet.size() > 1) {	
			///***get the PowerSet of Automaton Event Set******///
			Set<Set<Character>> gEventSetPowerSet = powerSet(IterGEventSet);
			gEventSetPowerSet.remove(IterGEventSet);
			gEventSetPowerSet.remove(Collections.emptySet());
			//System.out.println(gEventSetPowerSet);
			
			///***PowerSet to Sorted ArrayList******///
			List<Set<Character>> gEventPowerArrayList = powerSetToArrayList(gEventSetPowerSet);
		  
			///***Sorted PowerSet(ArrayList) to Binary Event Pairs******///
			List<List<Set<Character>>> gEventPairArrayList = getEventSetPair(gEventPowerArrayList, 
					IterGEventSet);
	  
			///***(One Step Iteration)Automaton Decomposition with Binary Event Pairs******///
			for (List<Set<Character>> gEventPair : gEventPairArrayList) {
				char eventPair1Array[] = new char[(gEventPair.get(0)).size()];
				char eventPair2Array[] = new char[(gEventPair.get(1)).size()];
				
				///***Event Set Pairs to Array******///
				int i = 0;
				for(Character gEvent : gEventPair.get(0))
					eventPair1Array[i++] = gEvent;
				i = 0;
				for(Character gEvent : gEventPair.get(1))
				eventPair2Array[i++] = gEvent;
		  
				///***Project******///
				Automaton proj1 = SpecialOperations.project(IterGloAut, eventPair1Array);
				Automaton proj1_ = SpecialOperations.project(IterGloAut, eventPair2Array);
		  
				///***Parallel composition******///
				Automaton invProj = ShuffleOperations.shuffle(proj1, proj1_);
				
				///***Equivalence Verification******///
				//System.out.println(invProj);
			
				if (IterGloAut.equals(invProj)) {
					DECOMFLAG = true;
					
					subAutSet.add(proj1);
					IterGEventSet.removeAll(gEventPair.get(0));
					IterGloAut = proj1_;
				
					//System.out.println(proj1_);
					break;
				}
			}
			
			if (!DECOMFLAG)
				break;
		}
		
		subAutSet.add(IterGloAut);
		///***print out automata******//////
		if (subAutSet.size() > 1) {
			System.out.println("The decomposed subautomata are:");
			for (Automaton subAut : subAutSet)
				System.out.println("subtask automaton: " + subAut);
		}
		
		return subAutSet;
		//System.out.println(gloAut.toDot());
		/////////////////////***End Iterative Decomposition******//////////////////////

	}
}
