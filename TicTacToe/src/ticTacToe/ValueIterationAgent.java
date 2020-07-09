package ticTacToe;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Value Iteration Agent, only very partially implemented. The methods to implement are: 
 * (1) {@link ValueIterationAgent#iterate}
 * (2) {@link ValueIterationAgent#extractPolicy}
 * 
 * You may also want/need to edit {@link ValueIterationAgent#train} - feel free to do this, but you probably won't need to.
 * @author ae187
 *
 */
public class ValueIterationAgent extends Agent {

	/**
	 * This map is used to store the values of states
	 */
	Map<Game, Double> valueFunction=new HashMap<Game, Double>();
	
	/**
	 * the discount factor
	 */
	double discount=0.9;
	
	/**
	 * the MDP model
	 */
	TTTMDP m=new TTTMDP();
	
	/**
	 * the number of iterations to perform - feel free to change this/try out different numbers of iterations
	 */
	int k=200;
	
	
	/**
	 * This constructor trains the agent offline first and sets its policy
	 */
	public ValueIterationAgent()
	{
		super();
		m=new TTTMDP();
		this.discount=0.9;
		initValues();
		train();
	}
	
	
	/**
	 * Use this constructor to initialise your agent with an existing policy
	 * @param p
	 */
	public ValueIterationAgent(Policy p) {
		super(p);
		
	}

	public ValueIterationAgent(double discountFactor) {
		
		this.discount=discountFactor;
		m=new TTTMDP();
		initValues();
		train();
	}
	
	/**
	 * Initialises the {@link ValueIterationAgent#valueFunction} map, and sets the initial value of all states to 0 
	 * (V0 from the lectures). Uses {@link Game#inverseHash} and {@link Game#generateAllValidGames(char)} to do this. 
	 * 
	 */
	public void initValues()
	{
		
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
			this.valueFunction.put(g, 0.0);
		
		
		
	}
	
	
	
	public ValueIterationAgent(double discountFactor, double winReward, double loseReward, double livingReward, double drawReward)
	{
		this.discount=discountFactor;
		m=new TTTMDP(winReward, loseReward, livingReward, drawReward);
	}
	
	/**
	 
	
	/*
	 * Performs {@link #k} value iteration steps. After running this method, the {@link ValueIterationAgent#valueFunction} map should contain
	 * the (current) values of each reachable state. You should use the {@link TTTMDP} provided to do this.
	 * 
	 *
	 */
	public void iterate()
	{
		for (int i = 0; i < k; i++) { //loop over the number of iterations
			
			for (Game game : valueFunction.keySet()) { //loop over all keysets in the game
				List <Move> steps = game.getPossibleMoves(); //create a list of all possible moves for the game
				
				double v = -9999999; //high negative v value in case q is also negative (later comparison)
				
				if(game.isTerminal()) v = 0; //if the game is terminal set v value to 0
				
				
				for (Move move : steps) { //for all possible moves
					List <TransitionProb> transitions = m.generateTransitions(game, move); //create a list of transitions
					
					double q = 0; //set q back to 0 for each move
					
					for (TransitionProb transition : transitions) { //transition probability distribution loop over all transitions
						q += (transition.prob * (transition.outcome.localReward + (discount * valueFunction.get(transition.outcome.sPrime)))); //calculation for the new q value (as on lecture 12)
					}
					
					if (q > v)  v = q; // if q is greater than v, v is now q (FindMax)
				}
				
				valueFunction.replace(game, v); //set the valuefunction to be highest v value
			}
		}
	}

	
	/**This method should be run AFTER the train method to extract a policy according to {@link ValueIterationAgent#valueFunction}
	 * You will need to do a single step of expectimax from each game (state) key in {@link ValueIterationAgent#valueFunction} 
	 * to extract a policy.
	 * 
	 * @return the policy according to {@link ValueIterationAgent#valueFunction}
	 */
	public Policy extractPolicy()
	{
		Policy policy = new Policy(); //create a new policy
		
		for (Game state : valueFunction.keySet()) { //loop over all keysets in the game
			List <Move> steps = state.getPossibleMoves(); //create a list of all possible moves for the game
			double v = -9999999;  //high negative v value in case q is also negative (later comparison)
			
			if(state.isTerminal()) v = 0; //if the game is terminal set v value to 0
			
			for (Move move : steps) { //for all possible moves
				List <TransitionProb> transitions = m.generateTransitions(state, move); //create a list of transitions
				
				double q = 0; //set q back to 0 for each move
				
				for (TransitionProb transition : transitions) { //transition probability distribution loop over all transitions
					q += (transition.prob * (transition.outcome.localReward + (discount * valueFunction.get(transition.outcome.sPrime)))); //calculation for the new q value (as on lecture 12)
				}
				
				if (q > v) {   // if q is greater than v
					v = q; //v is now q
					policy.policy.put(state, move); //update policy with new move
				}
	 }
   }
		return policy;
}
	
	/**
	 * This method solves the mdp using your implementation of {@link ValueIterationAgent#extractPolicy} and
	 * {@link ValueIterationAgent#iterate}. 
	 */
	public void train()
	{
		/**
		 * First run value iteration
		 */
		this.iterate();
		/**
		 * now extract policy from the values in {@link ValueIterationAgent#valueFunction} and set the agent's policy 
		 *  
		 */
		
		super.policy=extractPolicy();
		
		if (this.policy==null)
		{
			System.out.println("Unimplemented methods! First implement the iterate() & extractPolicy() methods");
			System.exit(1);
		}
		
		
		
	}

}
