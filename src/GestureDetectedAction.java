
public class GestureDetectedAction {

	private String exec;
	
	public void setExec(String exec) {
		this.exec = exec;
	}
	
	public String getExec() {
		return exec;
	}
	
	public void execute(){
		if (exec!=null && exec.isEmpty()==false){
			try{
				Runtime.getRuntime().exec(exec);
			}catch (Exception e) {
				//
			}
		}
	}
	
}
