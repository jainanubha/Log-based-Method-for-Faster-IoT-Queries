import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class ZipfGenerator {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("start time : "+Calendar.getInstance().getTime());
		File readFile = null;
		Scanner sc = null;
		// log file to export
		File fl = new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/zipf on queries/log file/"+"file_2400.txt");
		FileWriter fw = new FileWriter(fl);
		BufferedWriter bw = new BufferedWriter(fw);
		int numItems = 28;
		//double s = 1;
		int freq; 
		double sum = 0;
		double constant;
		double val;
		int queryNum;
		int numTotal = 2400;
		int totalFreq = 0;
		// query order
		int qorder[] = new int[]{3,13,14,1,9,15,2,4,10,18,19,5,7,8,11,21,22,23,24,25,26,27,20,28,6,16,17,12};
		int forder[] =  new int[qorder.length];
		int time = 0;
		int timeSlice = 1;
		for(int i=0;i<numItems;i++){
			val = 1.0 / (i + 1);
			sum = sum + val;
		}
		System.out.println("sum = "+sum);
		constant = numTotal/sum;
		System.out.println("constant = "+constant);
		// calculate individual frequency
		for(int k = 1;k<=numItems;k++){
			freq = (int) (constant/k);
			System.out.println("for rank "+k+", frequency = "+freq);
			totalFreq += freq;
			forder[k-1]=freq;
		}
		System.out.println("total frequency = "+totalFreq);
		int i = 0;
		while(totalFreq!=0){
			if(forder[i]!=0){
				queryNum = qorder[i];
				// file of records output for each query
				readFile = new File("C:/Users/Lenovo/Desktop/DAIICT/research/Experiments/data scaling/2GB RAM/query output files/"+"query"+queryNum+".csv");
				sc = new Scanner(readFile);
				while(sc.hasNext()){
					bw.append(sc.next()+","+timeSlice+"\n");
					time++;
					if(time%661 == 0)
						timeSlice++;
				}
				// 66148.107
				forder[i]--;
				totalFreq--;
			}
			i = (i+1)%forder.length;
		}
		sc.close();
		bw.close();
		System.out.println("end time : "+Calendar.getInstance().getTime());
	}
		

}
