import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class exchange implements Runnable{
	static List<person> per;
	Queue<String> queue;
	public static volatile int count = 0;
	private static volatile int outputs = 0;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new exchange(); 
	}
	
	public exchange() {
	this.queue = new ConcurrentLinkedQueue<String> ();
	new Thread(this, "exchange").start();
	}
	

	@Override
	public void run() {
		// TODO Auto-generated method stub
		System.out.println("** Calls to be made **");
		per=new ArrayList<person>();
		try(BufferedReader read = new BufferedReader(new FileReader("calls.txt"))) {
			for(String line1; (line1 = read.readLine()) != null; ) {
				String tmp = line1.replaceAll("[\\[\\]\\{\\}\\.]", "");
				String tmp2 = line1.replaceAll("[\\{\\}\\.]", "");
				String tmp3[] = tmp2.split(",");
				System.out.print(tmp3[0] +": ");
				String tmp4 [] = Arrays.copyOfRange(tmp3, 1, tmp3.length);
				for(int i=0;i<tmp4.length; ++i) {
					if(i==tmp4.length-1)
					System.out.print(tmp4[i].trim());
					else
						System.out.print(tmp4[i].trim()+",");
				}
				System.out.println("");
				String caller = tmp.split(",")[0].trim();
				per.add(new person(caller,this));
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try(BufferedReader read = new BufferedReader(new FileReader("calls.txt"))) {
			for(String line2; (line2 = read.readLine()) != null; ) {
				String tmp1 = line2.replaceAll("[\\[\\]\\{\\}\\.]", "");
				String[] callees = Arrays.copyOfRange(tmp1.split(","), 1, tmp1.split(",").length);
				exchange.count += (callees.length *2);
				exchange.count++;
				List<person> tmp = new ArrayList<person>();
				String caller1 = line2.replaceAll("[\\[\\]\\{\\}\\.]", "").split(",")[0].trim();
				person tmpp = null;
				for(person pr: per) {
					if(pr.getName().equals(caller1))
					{
						tmpp=pr;
						break;
					}
				}
				for(String str: callees) {
					for(person pr: per) {
						if(pr.getName().equals(str.trim()))
						{
							tmp.add(pr);
							break;
						}
					}
				}
				tmpp.setCallee(tmp);
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("");

		for(person pr: per) {
			pr.t.start();
		}
		
		while(true) {
			while(exchange.outputs != exchange.count) {
					while(queue.peek()!=null) {
					String tmpp = queue.poll();
					exchange.outputs++;
					System.out.println(tmpp);
					}
			}
			break;
		}
		
		System.out.println("Master has received no replies for 1.5 seconds, ending...");
	}
}
