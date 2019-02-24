package org.biclas.station.domain.exception;

/** Exception used to signal that no biclas are currently available in a station. */
public class NoBiclaAvailException extends Exception {
	private static final long serialVersionUID = 1L;

	public NoBiclaAvailException() {
	}

	public NoBiclaAvailException(String message) {
		super(message);
	}
}
