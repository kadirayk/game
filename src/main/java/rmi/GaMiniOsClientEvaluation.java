package rmi;

import java.io.Serializable;

public class GaMiniOsClientEvaluation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1527038062829928502L;
	private Double responseDelay;
	private Double fps;

	public GaMiniOsClientEvaluation() {
		// TODO Auto-generated constructor stub
	}

	public Double getResponseDelay() {
		return responseDelay;
	}

	public void setResponseDelay(Double responseDelay) {
		this.responseDelay = responseDelay;
	}

	public Double getFps() {
		return fps;
	}

	public void setFps(Double fps) {
		this.fps = fps;
	}

}
