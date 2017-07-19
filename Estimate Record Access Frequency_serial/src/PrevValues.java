
public class PrevValues {
	public float prevEstFreq;
	public int prevEstTime;
	public PrevValues(float pef,int pet){
		this.prevEstFreq=pef;
		this.prevEstTime=pet;
	}
	public float getPrevEstFreq() {
		return prevEstFreq;
	}
	public void setPrevEstFreq(float prevEstFreq) {
		this.prevEstFreq = prevEstFreq;
	}
	public int getPrevEstTime() {
		return prevEstTime;
	}
	public void setPrevEstTime(int prevEstTime) {
		this.prevEstTime = prevEstTime;
	}
	
}
