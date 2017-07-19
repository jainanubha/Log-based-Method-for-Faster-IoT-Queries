package parallelForward;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.*;

public class main {

	public void run() throws InterruptedException, ExecutionException, IOException {
		Long startTime = Calendar.getInstance().getTime().getTime();
		
		// Create a map to write access time stamps for each record
		Scanner sc = new Scanner(new File("/home/dell-lab/Downloads/parallelForward/src/results.txt"));
		String line = null;
		int line_num = 0;
		int record_num;
		List<Integer> ti_list;
		Map<Integer, List<Integer>> record_ti_list = new HashMap<>();
		while(sc.hasNext()){
			line_num++;
			line = sc.next();
			record_num = Integer.parseInt(line.split(",")[0]);
			if(record_ti_list.containsKey(record_num)){
				ti_list = record_ti_list.get(record_num);
				ti_list.add(line_num);
				record_ti_list.replace(record_num, ti_list);
			}else{
				record_ti_list.put(record_num, new ArrayList<>(Arrays.asList(line_num)));
			}
		}
		
		System.out.println("end time for distributing log file: "+Calendar.getInstance().getTime());
		int num_tasks = record_ti_list.size();
		System.out.println("number of tasks : "+num_tasks);
		
		System.out.println("start time for calculating results: "+Calendar.getInstance().getTime());
		
		// using ExecutorService
			
		// Check the number of available processors
		int processors = Runtime.getRuntime().availableProcessors();
		ExecutorService eservice = Executors.newFixedThreadPool(processors);
		List<Future> futuresList = new ArrayList<Future>();
		for (HashMap.Entry<Integer, List<Integer>> entry : record_ti_list.entrySet()) {
			futuresList.add(eservice.submit(new forwardAlgorithmProblem(entry.getValue(), entry.getKey())));
		}
		result taskResult;
		Map<Integer, Float> resultVal = new HashMap<>();
		for(Future future:futuresList){
			taskResult = (result) future.get();
			resultVal.put(taskResult.getKey(), taskResult.getEstdVale());
		}
		
		// using CompletionService
		
		// Check the number of available processors
		/*int processors = Runtime.getRuntime().availableProcessors();
		//int numProcesses = 6;
		ExecutorService eservice = Executors.newFixedThreadPool(processors);
		CompletionService < Object > cservice = new ExecutorCompletionService < Object > (eservice);
		for (HashMap.Entry<Integer, List<Integer>> entry : record_ti_list.entrySet()) {
			cservice.submit(new forwardAlgorithmProblem(entry.getValue(), entry.getKey()));
		} 	
		result taskResult;
		Map<Integer, Float> resultVal = new HashMap<>();
		for(int i=0;i<record_ti_list.size();i++){
			taskResult = (result) cservice.take().get();
			resultVal.put(taskResult.getKey(), taskResult.getEstdVale());
		}*/
		
		
		// using fork join
		
		// Check the number of available processors
		//int processors = Runtime.getRuntime().availableProcessors();
		//int numProcesses = 50;
		/*List<Future> futuresList = new ArrayList<Future>();
		ForkJoinPool fjPool = new ForkJoinPool(processors);
		for (HashMap.Entry<Integer, List<Integer>> entry : record_ti_list.entrySet()) {
			futuresList.add(fjPool.submit(new forwardAlgorithmProblem(entry.getValue(), entry.getKey())));
		} 	
		result taskResult;
		Map<Integer, Float> resultVal = new HashMap<>();
		for(Future future:futuresList){
			taskResult = (result) future.get();
			resultVal.put(taskResult.getKey(), taskResult.getEstdVale());
		}*/
		
		resultVal = sortByValue_est_2(resultVal);
		System.out.println("number of records = "+resultVal.size());
		int numb=0;
		FileWriter fw1 = new FileWriter(new File("/media/dell-lab/New Volume/Anubha/Experiments/18-03/estimated_parallel_frequencies.txt"));
		for (Map.Entry<Integer, Float> entry : resultVal.entrySet()) {
			fw1.write("Count: "+(++numb)+" entry set = "+entry.getKey()+" : "+entry.getValue()+"\n");
		}
		fw1.close();
		
		int K = (5*800937)/100;
		int countRecords = 0;
		Float checkValue= 0f;
		List<Integer> hotDataList = new ArrayList<>();
		FileWriter fw = new FileWriter(new File("/media/dell-lab/New Volume/Anubha/Experiments/18-03/estimated_parallel_top_freq.txt"));
		for(HashMap.Entry<Integer, Float> entry : resultVal.entrySet()){
			countRecords++;
			if(countRecords < K){
				fw.write(countRecords+". "+entry.getKey()+" : "+entry.getValue()+"\n");
				hotDataList.add(entry.getKey());
			}else if(countRecords == K){
				checkValue = entry.getValue();
				fw.write(countRecords+". "+entry.getKey()+" : "+entry.getValue()+"\n");
				hotDataList.add(entry.getKey());
			}else{
				if(entry.getValue().floatValue() == checkValue){
					fw.write(countRecords+". "+entry.getKey()+" : "+entry.getValue()+"\n");
					hotDataList.add(entry.getKey());
				}else
					break;
			}
		}
		fw.close();
		
		Long endTime = Calendar.getInstance().getTime().getTime();
		Long timeDiff = endTime-startTime;
		System.out.println("time for calculating results: "+timeDiff);
		
	}
	
	private static Map<Integer, Float> sortByValue_est_2(Map<Integer, Float> unsortMap) {

		// 1. Convert Map to List of Map
        List<Map.Entry<Integer, Float>> list = new LinkedList<Map.Entry<Integer, Float>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        
        
        Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
        	int val=0;
        	public int compare(Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2){
        		val = (o1.getValue()).compareTo(o2.getValue());
        		return val;
        		
        	}
		});
        
        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Integer, Float> sortedMap = new LinkedHashMap<Integer, Float>();
        for (Map.Entry<Integer, Float> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        /*
        //classic iterator example
        for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext(); ) {
            Map.Entry<String, Integer> entry = it.next();
            sortedMap.put(entry.getKey(), entry.getValue());
        }*/


        return sortedMap;
    }
	
	public static void main(String[] args) throws InterruptedException, ExecutionException, IOException{
		// TODO Auto-generated method stub
		new main().run();
		System.out.println("back in main");
	}

}
