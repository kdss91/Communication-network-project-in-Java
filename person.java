import java.util.List;

public class person implements Runnable{
	private String name;
	Thread t;
	List<person> callees ;
	exchange ex;
	boolean flag = false;
	person(String name, exchange ex){
		this.name = name;
		t= new Thread(this,name);
		this.ex = ex;
	}

	public void setCallee(List<person> callees) {
		this.callees = callees;
	}

	public String getName() {
		return name;
	}

	public List<person> getCallees() {
		return callees;
	}

	public synchronized String receiveCall(String msg) {
		long tmp =  System.currentTimeMillis();
		ex.queue.offer(this.name + " received " + msg + " [ " + tmp + " ]");
		return this.sendReply(tmp);
	}

	private String sendReply(long m) {	
					try {
						Thread.sleep(generateRandom());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			
		return "reply message from " + this.name + " [ " + m + " ]";
	}

	public void receiveReply(String msg) {
		ex.queue.offer(this.name + " received " + msg);
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		for(person pr: callees) {
			try {	
				  Thread.sleep(this.generateRandom());
				}
				catch(InterruptedException e) {
					e.printStackTrace();
				}
			String request= "intro message from " + this.name;
			String response = pr.receiveCall(request);	
			this.receiveReply(response);
		}
		

		try {
			t.join(8000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
		ex.queue.offer("Process " + this.name + " has recieved no calls for 1 second, ending...");
		}
	}

	public synchronized int generateRandom() {
		int tmp =(int)(Math.random()*100);
		if(tmp==0) {
			tmp=1;
		}
		return tmp;
	}
}
