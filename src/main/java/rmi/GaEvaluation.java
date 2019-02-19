package rmi;

public class GaEvaluation {
	Double fps; // maximize
	Double responseDelay; // minimize
	Double encodingError; // minimize

	public GaEvaluation(Double fps, Double responseDelay, Double encodingError) {
		super();
		this.fps = fps;
		this.responseDelay = responseDelay;
		this.encodingError = encodingError;
	}

	public Double getFps() {
		return fps;
	}

	public void setFps(Double fps) {
		this.fps = fps;
	}

	public Double getResponseDelay() {
		return responseDelay;
	}

	public void setResponseDelay(Double responseDelay) {
		this.responseDelay = responseDelay;
	}

	public Double getEncodingError() {
		return encodingError;
	}

	public void setEncodingError(Double encodingError) {
		this.encodingError = encodingError;
	}

}
