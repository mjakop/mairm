package lib;

public class MAIR implements Runnable {

	public static short MODE_NORMAL = 0;
	public static short MODE_LEARN = 1;

	private boolean doWork = false;
	private short mode = MODE_NORMAL;
	private MAIRInput input;
	private MAIRInputMessageListener inputMessageListener;

	public void setInput(MAIRInput input) {
		this.input = input;
	}

	/**
	 * Use this to set up event listener for received messages. This can be used for drawing graphs.
	 * @param listener
	 */
	public void setInputMessageListener(MAIRInputMessageListener listener) {
		this.inputMessageListener = listener;
	}

	private void checkSettings() throws Exception {
		if (input == null) {
			throw new MAIRExceptionNoInput();
		}
	}

	public boolean start() throws Exception {
		if (doWork == false) {
			checkSettings();
			mode = MODE_NORMAL;
			doWork = true;
			(new Thread(this)).start();
			return true;
		}
		return false;
	}

	public boolean startLearnMode() throws Exception {
		if (doWork == false) {
			checkSettings();
			mode = MODE_LEARN;
			doWork = true;
			(new Thread(this)).start();
			return true;
		}
		return false;
	}

	public void stop() {
		doWork = false;
	}

	public void run() {
		while (doWork) {
			MAIRInputMessage msg = input.get();
			// 1. filter for message, to check if we ignore this message or
			// process it.
			// send to dispacher
			try {
				Thread.sleep(1000);
			} catch (Exception e) {

			}
		}
	}
}
