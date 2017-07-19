import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

public class FrequencyCalc {

	public static void main(String[] args) throws IOException, SQLException {
		// TODO Auto-generated method stub
		FrequencyCalc fc = new FrequencyCalc();
		System.out.println("start time : "+Calendar.getInstance().getTime());
		fc.calcActualFreq();
		//fc.calcEstdFreqForward();
		//fc.calcEstdFreqForwardTimeSlice();
		//fc.findLossInHitRate();
		//fc.findColdData();
		fc.calcEstdFreqBackwardTimeSlice();
		System.out.println("end time : "+Calendar.getInstance().getTime());
	}
	
	public void calcActualFreq() throws IOException{
		// read log file
		Scanner sc = new Scanner(new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/query pattern diff/file_log_28_1.txt"));
		String line=null;
		
		Map<Integer, Float> mp = new HashMap<Integer, Float>();
		int count=0;
		//String record;
		int rec;
	
		while(sc.hasNext()){
			line = sc.next().split(",")[0];
			rec = Integer.parseInt(line);
			if(mp.containsKey(rec))  // if record has been accessed earlier
				mp.replace(rec, mp.get(rec)+1f);  // increase its access frequency by 1
			
			else // if record is accessed for first time
				mp.put(rec, 1f);   // set record access frequency as 1
		}
		
		mp = sortByValue_est(mp);	// sort records by non-increasing order of their access frequencies
		
		FileWriter fw = new FileWriter(new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/query pattern diff/zipf/actual_frequencies_28.txt"));
		for (HashMap.Entry<Integer, Float> entry : mp.entrySet())	// write access frequencies of each record in a file
		    fw.write("Count: "+(++count)+" entry set = "+entry.getKey()+" : "+entry.getValue()+"\n");

		fw.close();
		
		int K = (int) ((8*10000)/100);		// hot data size
		int countRecords = 0;
		Float checkValue= 0f;
		// write access frequencies of top K records in file
		fw = new FileWriter(new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/query pattern diff/zipf/actual_top_frequencies_28.txt"));
		for(HashMap.Entry<Integer, Float> entry : mp.entrySet()){
			countRecords++;
			if(countRecords < K)
				fw.write(entry.getKey()+":"+entry.getValue()+"\n");
			else if(countRecords == K){
				checkValue = entry.getValue();
				fw.write(entry.getKey()+":"+entry.getValue()+"\n");
			}else{
				if(entry.getValue().floatValue() == checkValue){
					fw.write(entry.getKey()+":"+entry.getValue()+"\n");
				}else
					break;
			}
		}
		fw.close();
	
	}
	
	private void calcEstdFreqForward() throws IOException, SQLException{
		
		Map<Integer, List<PrevValues>> mp = new HashMap<Integer, List<PrevValues>>();
		// read log file
		Scanner sc = new Scanner(new File("C:/Users/Lenovo/Desktop/DAIICT/research/programs/EstimateCalculation/src/results.txt"));
		String line=null;
		float estimatedFreq = 0f;
		float alpha=0.05f;
		int key;
		float freqVal = 0f;
		int count = 0;
		int size = 0;
		List<PrevValues > list = null;
		PrevValues pvobj = null;
		
		while(sc.hasNext()){
			count++;
			freqVal = 0f;
			key = sc.nextInt();
			
			if(mp.containsKey(key)){	// if record has been accessed earlier
				size = mp.get(key).size();
				pvobj = mp.get(key).get(size-1);
				// exponential smoothing : estr(tn) = alpha + (1 - alpha)(tn-tprev) * estr(tprev)
				freqVal = (float) (alpha + Math.pow((1-alpha), (count-pvobj.getPrevEstTime())) * pvobj.getPrevEstFreq());
				mp.get(key).add(new PrevValues(freqVal, count));	// add current estimation value and time in the list
			
			}else{	// if record is accessed for first time
				estimatedFreq = 1f;		// take 1st estimate value as actual value = 1
				list = new ArrayList<>();
				list.add(new PrevValues(estimatedFreq, count));	// add current estimation value and time in the list
				mp.put(key,list);
			}
		}
		
		//create hashmap <record id,estimatedFrequencies>
		Map<Integer, Float> finalValMap = new HashMap<Integer, Float>();
		float totalVal = 0f;
		List<PrevValues> listprev = null;
		// calculate total estimated freq for each record
		for (HashMap.Entry<Integer, List<PrevValues>> entry : mp.entrySet()) {
			totalVal = 0f;
			listprev = entry.getValue();
			for(int i=0;i<listprev.size();i++){		// total estimated freq = sum of probabilities at each access
				totalVal += listprev.get(i).getPrevEstFreq();
			}
			finalValMap.put(entry.getKey(), totalVal);
		}
		
		finalValMap = sortByValue_est(finalValMap);	// sort records by non-increasing order of estimated access frequency
		
		int numb=0;
		// write estimated freq of each record in a file
		FileWriter fw1 = new FileWriter(new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/query pattern diff/estimated_frequencies.txt"));
		for (Map.Entry<Integer, Float> entry : finalValMap.entrySet()) {
			fw1.write("Count: "+(++numb)+" entry set = "+entry.getKey()+" : "+entry.getValue()+"\n");
		}
		fw1.close();
		
		int K = (int) ((8*10505690)/100);		// hot data size
		int countRecords = 0;
		Float checkValue= 0f;
		List<Integer> hotDataList = new ArrayList<>();
		List<Integer> coldDataList = new ArrayList<>();
		
		// write estimated freq of top K records in a file
		FileWriter fw = new FileWriter(new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/query pattern diff/estimated_top_freq.txt"));
		for(HashMap.Entry<Integer, Float> entry : finalValMap.entrySet()){
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
				}else{
					//coldDataList.add(entry.getKey());
					break;
				}
					
			}
		}
		
		fw.close();
		System.out.println("hot data = "+hotDataList.size());
		//System.out.println("cold data = "+coldDataList.size());
		System.out.println("write end time : "+Calendar.getInstance().getTime());
		System.out.println(hotDataList.toString());
		// insert hot data in a hotdatatable
		//writeDataToTable(hotDataList);
	
	
	}
	
	public static void writeDataToTable(List<Integer> hotDataList) throws SQLException{
		// insert hot data in a hotdatatable
		
				String url = "jdbc:postgresql://localhost:5433/smartcity";
				String user = "postgres";
				String pswrd = "postgres";
				Connection con = DriverManager.getConnection(url, user, pswrd);
				StringBuffer sqlst = null;
				Statement st = null;
				StringBuffer str = new StringBuffer();
				for(int i=0;i<hotDataList.size();i++){
					if(i == (hotDataList.size()-1))
						str.append(""+hotDataList.get(i)+"));");
					else
						str.append(""+hotDataList.get(i)+",");
					
				}
				//System.out.println("str: "+str);
				sqlst = new StringBuffer("INSERT INTO hotdatatable (sub,pred,obj) (SELECT sub,pred,obj FROM lod8 WHERE OID IN (");
				sqlst.append(str.toString());
				System.out.println(sqlst);
				st = con.createStatement();
				System.out.println("no. of records entered in hot table= "+st.executeUpdate(sqlst.toString()));
				
				/*sqlst = null;
				st = null;
				str = new StringBuffer();
				for(int i=0;i<coldDataList.size();i++){
					if(i == (coldDataList.size()-1))
						str.append(""+coldDataList.get(i)+"));");
					else
						str.append(""+coldDataList.get(i)+",");
					
				}
				//System.out.println("str: "+str);
				sqlst = new StringBuffer("INSERT INTO colddatatable (sub,pred,obj) (SELECT sub,pred,obj FROM \"LODTriples\" WHERE OID IN (");
				sqlst.append(str.toString());
				//System.out.println(sqlst);
				st = con.createStatement();
				System.out.println("no. of records entered in cold table= "+st.executeUpdate(sqlst.toString()));*/
				
				
				if(!con.isClosed())
					con.close();
	}
	
	private void findColdData() throws IOException{
		List<Integer> hotDataList = new ArrayList<>();
		Scanner sc = new Scanner(new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/data scaling/2GB RAM/experiment results/hot_records.txt"));
		while(sc.hasNext()){
			hotDataList.add(sc.nextInt());
		}
		System.out.println("hot data size: "+hotDataList.size());
		List<Integer> allDataList = new ArrayList<>();
		sc = new Scanner(new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/data scaling/2GB RAM/all OIDs.csv"));
		while(sc.hasNext()){
			allDataList.add(sc.nextInt());
		}
		System.out.println("all data size before: "+allDataList.size());
		Iterator<Integer> listItr = hotDataList.listIterator();
		while(listItr.hasNext()){
			allDataList.remove(listItr.next());
		}
		FileWriter fw1 = new FileWriter(new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/data scaling/2GB RAM/coldDataList.txt"));
		System.out.println("cold Data size: "+allDataList.size());
		listItr = null;
		listItr = allDataList.listIterator();
		while(listItr.hasNext())
			fw1.write(listItr.next()+"\n");
		
		fw1.close();
	}
	
	public void findLossInHitRate() throws FileNotFoundException{
		// actual hot records file
		Scanner sc = new Scanner(new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/query pattern diff/zipf/actual_top_frequencies_zipf.txt"));
		Map<Integer, Float> actualHotRecordData = new HashMap<>();
		Float totalAccessFreq = 0f;
		String st[] = null;
		String line;
		while(sc.hasNext()){
			line = sc.nextLine();
			//System.out.println("line: "+line);
			st = line.split(":");
			actualHotRecordData.put(Integer.parseInt(st[0].trim()), Float.parseFloat(st[1].trim()));	// actual hot records and frequencies
			totalAccessFreq += Float.parseFloat(st[1].trim());
		}
		System.out.println("actualHotRecordData size: "+actualHotRecordData.size());
		Set<Integer> actualHotRecords = actualHotRecordData.keySet();	// actual hot records
		// estimated hot records file
		sc= new Scanner(new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/query pattern diff/backward_estimated_frequencies.txt"));
		int record;
		while(sc.hasNext()){
			record = Integer.parseInt(sc.nextLine().split(":")[0].trim());
			//System.out.println(record);
			actualHotRecords.remove(record);	// remove estimated hot records that are actually hot
		}
		System.out.println("actualHotRecordData size: "+actualHotRecords.size());
		if(actualHotRecordData.size() > 0){	// if there are records which should be hot but are considered cold 
			Float accessFreq = 0f;
			Float lossInHitRate;
			for(HashMap.Entry<Integer, Float> entry : actualHotRecordData.entrySet())
				accessFreq += entry.getValue();		// find total access frequency of such records
			System.out.println("access frequency : "+accessFreq);
			System.out.println("total access freq : "+totalAccessFreq);
			lossInHitRate = (accessFreq*100)/totalAccessFreq;
			System.out.println("loss in hit rate = "+lossInHitRate);
		}
	}
	
	public void calcEstdFreqForwardTimeSlice() throws IOException{
		Map<Integer, List<PrevValues>> mp = new HashMap<Integer, List<PrevValues>>();
		// read log file
		Scanner sc = new Scanner(new File("C:/Users/Lenovo/Desktop/DAIICT/research/programs/EstimateCalculation/src/results.txt"));
		String line=null;
		float estimatedFreq = 0f;
		float alpha=0.05f;
		int key;
		float freqVal = 0f;
		int count = 0;
		int size = 0;
		List<PrevValues > list = null;
		PrevValues pvobj = null;
		String st[] = null;
		while(sc.hasNext()){
			st = sc.next().split(",");
			count=Integer.parseInt(st[1]);
			freqVal = 0f;
			key = Integer.parseInt(st[0]);
			
			if(mp.containsKey(key)){	// if record has been accessed earlier
				size = mp.get(key).size();
				pvobj = mp.get(key).get(size-1);
				// exponential smoothing : estr(tn) = alpha + (1 - alpha)(tn-tprev) * estr(tprev)
				freqVal = (float) (alpha + Math.pow((1-alpha), (count-pvobj.getPrevEstTime())) * pvobj.getPrevEstFreq());
				mp.get(key).add(new PrevValues(freqVal, count));	// add current estimation value and time in the list
			
			}else{	// if record is accessed for first time
				estimatedFreq = 1f;		// take 1st estimate value as actual value = 1
				list = new ArrayList<>();
				list.add(new PrevValues(estimatedFreq, count));	// add current estimation value and time in the list
				mp.put(key,list);
			}
		}
		
		//create hashmap <record id,estimatedFrequencies>
		Map<Integer, Float> finalValMap = new HashMap<Integer, Float>();
		float totalVal = 0f;
		List<PrevValues> listprev = null;
		// calculate total estimated freq for each record
		for (HashMap.Entry<Integer, List<PrevValues>> entry : mp.entrySet()) {
			totalVal = 0f;
			listprev = entry.getValue();
			for(int i=0;i<listprev.size();i++){		// total estimated freq = sum of probabilities at each access
				totalVal += listprev.get(i).getPrevEstFreq();
			}
			finalValMap.put(entry.getKey(), totalVal);
		}
		
		finalValMap = sortByValue_est(finalValMap);	// sort records by non-increasing order of estimated access frequency
		
		int numb=0;
		// write estimated freq of each record in a file
		FileWriter fw1 = new FileWriter(new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/query pattern diff/estimated_frequencies_timeSlice.txt"));
		for (Map.Entry<Integer, Float> entry : finalValMap.entrySet()) {
			fw1.write("Count: "+(++numb)+" entry set = "+entry.getKey()+" : "+entry.getValue()+"\n");
		}
		fw1.close();
		
		int K = (int) ((8*10505690)/100);		// hot data size
		int countRecords = 0;
		Float checkValue= 0f;
		List<Integer> hotDataList = new ArrayList<>();
		List<Integer> coldDataList = new ArrayList<>();
		
		// write estimated freq of top K records in a file
		FileWriter fw = new FileWriter(new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/query pattern diff/estimated_top_freq_timeSlice.txt"));
		for(HashMap.Entry<Integer, Float> entry : finalValMap.entrySet()){
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
				}else{
					//coldDataList.add(entry.getKey());
					break;
				}
					
			}
		}
		
		fw.close();
		System.out.println("hot data = "+hotDataList.size());
		//System.out.println("cold data = "+coldDataList.size());
		System.out.println("write end time : "+Calendar.getInstance().getTime());
		System.out.println(hotDataList.toString());
		// insert hot data in a hotdatatable
		//writeDataToTable(hotDataList);
	}
	
	public void calcEstdFreqBackwardTimeSlice() throws IOException{
		BufferedReader sc = new BufferedReader(new FileReader(new File("C:/Users/Lenovo/Desktop/DAIICT/research/programs/EstimateCalculation/src/results.txt")));
		// reversing log file to read from end
		List<String> lines = new ArrayList<String>();
		String curLine;
		while ( (curLine= sc.readLine()) != null) {
		  lines.add(curLine);
		}
		Collections.reverse(lines);
		int currentTimeSlice, prevTimeSlice;
		int endTime = Integer.parseInt(lines.get(0).split(",")[1]);
		int record;
		String st[] = null;
		float alpha = 0.05f;
		int K = (int) ((4.75*10505690)/100);
		float backEst,upBackEst,loBackEst,klowbound;
		int acptThreshold = 1;
		Boolean initialisationStep = true;
		Map<Integer, Float> mpBackEst = new HashMap<>();
		Map<Integer, Float> mpLoBackEst = new HashMap<>();
		Map<Integer, Float> mpUpBackEst = new HashMap<>();
		Iterator<String> itr = lines.listIterator();
		List<Float> uniqueLoBackEstList = null;
		prevTimeSlice = endTime;
		while(itr.hasNext()){
			st = itr.next().split(",");
			record = Integer.parseInt(st[0]);
			currentTimeSlice = Integer.parseInt(st[1]);
			if(mpBackEst.size()<K){
				if(mpBackEst.containsKey(record)){
					backEst = (float) (alpha*Math.pow((1-alpha),(endTime-currentTimeSlice)) + mpBackEst.get(record));
					upBackEst = (float) (backEst + Math.pow((1-alpha),(endTime-currentTimeSlice+1)));
					loBackEst = (float) (backEst + Math.pow((1-alpha),endTime));
					mpBackEst.replace(record,backEst);
					mpLoBackEst.replace(record, loBackEst);
					mpUpBackEst.replace(record, upBackEst);
				}else{
					backEst = 1f;
					upBackEst = (float) (backEst + Math.pow((1-alpha),(endTime-currentTimeSlice+1)));
					loBackEst = (float) (backEst + Math.pow((1-alpha),endTime));
					mpBackEst.put(record,backEst);
					mpLoBackEst.put(record, loBackEst);
					mpUpBackEst.put(record, upBackEst);
				}
			}else{
				if(initialisationStep){
					// find kth lower bound
					klowbound = Float.MAX_VALUE;
					for(HashMap.Entry<Integer, Float> entry : mpLoBackEst.entrySet()){
						loBackEst = entry.getValue();
						if(loBackEst<klowbound)
							klowbound = loBackEst;	// kth lower bound value
					}
					// calculate accept threshold
					acptThreshold = (int) (endTime - Math.floor(Math.log(klowbound)/Math.log(1-alpha)));
					initialisationStep = false;
				}
				
				if(!(mpBackEst.containsKey(record))){		// check if record already exist in the hashtable
					if(currentTimeSlice > acptThreshold)		// check the read time of record against accept threshold
						mpBackEst.put(record, 1f);		// create a new entry of the OID in the hashtable
						mpUpBackEst.put(record, 0f);
						mpLoBackEst.put(record, 0f);
				}else{
					// calculate backward estimate and update it
					backEst = (float) (alpha * Math.pow((1-alpha),(endTime-currentTimeSlice)) + mpBackEst.get(record));
					mpBackEst.replace(record,backEst);
				}
				
				// FILTER STEP
				// check if end of time slice
				if(currentTimeSlice!=prevTimeSlice){	// end of time slice
					uniqueLoBackEstList = new ArrayList<>();
					for(HashMap.Entry<Integer, Float> entry : mpBackEst.entrySet()){	
						backEst = entry.getValue();
						upBackEst = (float) (backEst + Math.pow((1-alpha),(endTime-prevTimeSlice+1)));	//update up backward estimate
						loBackEst = (float) (backEst + Math.pow((1-alpha),endTime));	//update lower backward estimate
						mpUpBackEst.replace(record, upBackEst);
						mpLoBackEst.replace(record, loBackEst);
						if(!uniqueLoBackEstList.contains(loBackEst))
							uniqueLoBackEstList.add(loBackEst);		// find unique values of lower bounds
					}
					Collections.sort(uniqueLoBackEstList, Collections.reverseOrder());	// sort lower bounds
					System.out.println("uniqueLoBackEstList size : "+uniqueLoBackEstList.size());
					klowbound = uniqueLoBackEstList.get(K);	// find Kth lower bound value
					
					//remove records with upper bound < Kth lower bound value
					for(HashMap.Entry<Integer, Float> entry : mpUpBackEst.entrySet()){
						record = entry.getKey();
						if(entry.getValue() < klowbound){
							mpUpBackEst.remove(record);
							mpLoBackEst.remove(record);
							mpBackEst.remove(record);
						}
					}
					
					if(mpBackEst.size()==K){
						break;
					}
					
				}
				
				
			}
			prevTimeSlice = currentTimeSlice;
			
		}
		System.out.println("size of records : "+mpBackEst);
	}
	
	private static Map<Integer, Float> sortByValue_est(Map<Integer, Float> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<Integer, Float>> list = new LinkedList<Map.Entry<Integer, Float>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
        	int val=0;
        	@Override
			public int compare(Map.Entry<Integer, Float> o1, Map.Entry<Integer, Float> o2){
        		val = (o2.getValue()).compareTo(o1.getValue());
        		return val;
        		
        	}
		});
        
        // 3. Loop the sorted list and put it into a new insertion order Map LinkedHashMap
        Map<Integer, Float> sortedMap = new LinkedHashMap<Integer, Float>();
        for (Map.Entry<Integer, Float> entry : list) {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
	
	private static Map<Integer, Float> sortByValue_est_2(Map<Integer, Float> unsortMap) {

        // 1. Convert Map to List of Map
        List<Map.Entry<Integer, Float>> list = new LinkedList<Map.Entry<Integer, Float>>(unsortMap.entrySet());

        // 2. Sort list with Collections.sort(), provide a custom Comparator
        //    Try switch the o1 o2 position for a different order
        
        
        Collections.sort(list, new Comparator<Map.Entry<Integer, Float>>() {
        	int val=0;
        	@Override
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
        
        return sortedMap;
    }

}
