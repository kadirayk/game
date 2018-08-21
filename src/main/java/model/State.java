package model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author kadirayk
 *
 */
public class State implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -849218511658141465L;
	private String name;
	private Map<String, String> transition;
	private List<Question> questions;

	public Question getQuestionById(String id) {
		Question question = null;
		if (ListUtil.isNotEmpty(questions)) {
			for (Question q : questions) {
				if (id.equals(q.getId())) {
					return q;
				}
			}
		}
		return question;
	}

	public List<Question> getQuestions() {
		return questions;
	}

	public void setQuestions(List<Question> questions) {
		this.questions = questions;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Map<String, String> getTransition() {
		return transition;
	}

	public void setTransition(Map<String, String> transition) {
		this.transition = transition;
	}

	/**
	 * Generates concrete HTML element from the UI Elements of the questions to
	 * make up the form
	 * 
	 * @return
	 */
	public String toHTML() {
		StringBuilder htmlElement = new StringBuilder();

		if (ListUtil.isNotEmpty(questions)) {
			for (Question q : questions) {
				String formQuestion = q.getContent();
				if (formQuestion != null) {
					htmlElement.append("<br>").append(formQuestion).append("<br>");
				}
				UIElement formUiElement = q.getUiElement();
				if (formUiElement != null) {
					htmlElement.append(formUiElement.toHTML()).append("<br>").append("\n");
				}
			}
		}

		return htmlElement.toString();
	}

}
