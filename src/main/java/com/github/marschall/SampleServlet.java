package com.github.marschall;

import java.io.IOException;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSource.Emitter;
import org.eclipse.jetty.servlets.EventSourceServlet;

public class SampleServlet extends EventSourceServlet
{
    
    private final Set<Emitter> emitters = new CopyOnWriteArraySet<>();
    
    private static final Logger LOG = Logger.getLogger("event-source-sample");
    
    private volatile ScheduledExecutorService executor;
    
    @Override
    public void init() throws ServletException
    {
        super.init();
        this.executor = Executors.newSingleThreadScheduledExecutor();
        this.executor.scheduleAtFixedRate(new UpdateSender(), 10, 10, TimeUnit.SECONDS);
    }
    
    @Override
    public void destroy()
    {
        this.executor.shutdown();
        this.emitters.clear();
        super.destroy();
    }
    

    @Override
    protected EventSource newEventSource(HttpServletRequest request)
    {
        return new TimeEventSource();
    }
    
    final class UpdateSender implements Runnable {

        @Override
        public void run()
        {
            String serverTime = String.format(Locale.US, "%tT", System.currentTimeMillis());
            for (Emitter emitter : emitters)
            {
                try
                {
                    emitter.data(serverTime);
                }
                catch (IOException e)
                {
                    LOG.log(Level.SEVERE, "could not send update to client", e);
                }
            }
            
        }
        
    }
    
    final class TimeEventSource implements EventSource {

        private volatile Emitter emitter;

        @Override
        public void onOpen(Emitter emitter) throws IOException
        {
            this.emitter = emitter;
            emitters.add(emitter);
            
        }

        @Override
        public void onClose()
        {
            emitters.remove(this.emitter);
            
        }
        
    }

}
