package viewtotable.util;

public interface WorkerListener {
	void start(String msg);
	void processing(String msg);
	void finishItem(String msg);
	void completed(String msg);
}
