package edu.mcw.rgd.indexer;

import edu.mcw.rgd.indexer.client.ESClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Created by jthota on 11/14/2018.
 */
public class MyThreadPoolExecutor extends ThreadPoolExecutor {

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
            System.err.println("Uncaught exception! "+t +" STACKTRACE:"+ Arrays.toString(t.getStackTrace()));
            if(ESClient.getClient()!=null)
                try {
                    ESClient.getClient().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            System.exit(1);
        }
    }
}
