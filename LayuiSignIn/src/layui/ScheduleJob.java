package layui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class ScheduleJob {
	
	private  ScheduledExecutorService scheduExec;
	private  Logger log;
    
    public long start;
    
    ScheduleJob(Logger log){
        this.scheduExec =  Executors.newScheduledThreadPool(1);  
        this.start = System.currentTimeMillis();
        this.log=log;
        
    }
    
    public void runJob(){
        scheduExec.scheduleAtFixedRate(new Runnable() {
            public void run() {
            	
                try {
                    log.info("Ö´ÐÐÇ©µ½³ÌÐò");
                  App.domain();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },0,5,TimeUnit.HOURS);
    }
}
