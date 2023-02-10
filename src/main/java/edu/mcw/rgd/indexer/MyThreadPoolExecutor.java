package edu.mcw.rgd.indexer;

import edu.mcw.rgd.process.Utils;
import edu.mcw.rgd.services.ClientInit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Created by jthota on 11/14/2018.
 */
public class MyThreadPoolExecutor extends ThreadPoolExecutor {
    Logger log= LogManager.getLogger(Manager.class);
    public MyThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    public void afterExecute(Runnable r, Throwable t){
        super.afterExecute(r,t);
        if(t==null && r instanceof Future){
            try{
                Object result=((Future) r).get();

            }catch (CancellationException e){
                t=e;
            }catch (ExecutionException e){
                t=e.getCause();
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
        if(t!=null){
            log.error("Uncaught exception! "+t +" STACKTRACE:"+ Arrays.toString(t.getStackTrace()));
            try {
                if(ClientInit.getClient()!=null)
                    try {
                        ClientInit.getClient().close();
                    } catch (IOException e) {
                        Utils.printStackTrace(e, log);
                    }
            } catch (UnknownHostException e) {
                Utils.printStackTrace(e, log);
            }
            System.exit(1);
        }
    }
}
