package model;

import java.util.Map;

/**
 * HTML Input element &lt;input\&gt;
 * 
 * @author kadirayk
 *
 */
public class Input extends UIElement {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String TAG = "input";

	public Input() {
		setTag(TAG);
	}

	public Input(String content, Map<String, String> attributes) {
		setTag(TAG);
		setContent(content);
		setAttributes(attributes);
	}

}
