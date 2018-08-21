package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Interview is defined by states and their conditional transitions each state
 * of the interview can have multiple form inputs
 * 
 * @author kadirayk
 *
 */
public class Interview implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9198421035407778684L;

	private String prototypeName;
	private String questionRepo;
	private List<State> states;
	private Map<String, State> stateMap;
	private State currentState;
	private String id;
	private Set<String> questionSet;

	/**
	 * Returns question with the given path i.e. "step1.q1"
	 * 
	 * @param path
	 * @return
	 */
	public Question getQuestionByPath(String path) {
		Question q = null;
		if (path.contains(".")) {
			String state = path.split("\\.")[0];
			String question = path.split("\\.")[1];
			State s = stateMap.get(state);
			q = s.getQuestionById(question);
		}

		return q;

	}

	public Set<String> getQuestionSet() {
		return questionSet;
	}

	public void setQuestionSet(Set<String> questionSet) {
		this.questionSet = questionSet;
	}

	public String getPrototypeName() {
		return prototypeName;
	}

	public void setPrototypeName(String prototypeName) {
		this.prototypeName = prototypeName;
	}

	public String getQuestionRepo() {
		return questionRepo;
	}

	public void setQuestionRepo(String questionRepo) {
		this.questionRepo = questionRepo;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public State getCurrentState() {
		return currentState;
	}

	public List<State> getStates() {
		return states;
	}

	public void setStates(List<State> states) {
		this.states = states;
		if (ListUtil.isNotEmpty(states)) {
			currentState = states.get(0);
			stateMap = new HashMap<>();
			for (State s : states) {
				stateMap.put(s.getName(), s);
			}
		}

	}

	public Map<String, State> getStateMap() {
		return stateMap;
	}

}
