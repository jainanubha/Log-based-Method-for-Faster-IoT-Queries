package parallelForward;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.concurrent.Callable;

public class forwardAlgorithmProblem implements Callable{
	public ArrayList<Integer> arrList;
	public int key;

	public forwardAlgorithmProblem(List<Integer> list, int key) {
		this.arrList = (ArrayList<Integer>) list;
		this.key = key;
	}
	

	@Override
	public result call() throws Exception {
		// TODO Auto-generated method stub
		//System.out.println("start time for thread of key: "+key+" = "+Calendar.getInstance().getTime());
		//Thread.sleep(1000);
		float freqVal;
		
		float alpha=0.05f;
		
		int num;
		List<Float> lt = new ArrayList<>();
		lt.add(1f);
		Iterator<Integer> listItr = arrList.listIterator();
		while(listItr.hasNext()){
			freqVal = 0f;
			listItr.next();
			ListIterator<Float> it = lt.listIterator();
			num = lt.size(); 
			while(it.hasNext()){
				freqVal += alpha * Math.pow((1-alpha), num) * it.next();
				num--;
			}
			lt.add(freqVal);
		}
		
		freqVal = lt.get(lt.size()-1);
		
		//System.out.println("end time for thread of key: "+key+" = "+Calendar.getInstance().getTime()+", result = "+freqVal);
		return new result(key, freqVal);
	}
	
}
