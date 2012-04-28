package com.github.marschall;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.servlets.EventSource;
import org.eclipse.jetty.servlets.EventSourceServlet;

import com.github.marschall.ServerTimeServlet.UpdateSender;

public class CounterServlet extends EventSourceServlet
{
    
    private static final Logger LOG = Logger.getLogger("event-source-sample");
    
    
    private volatile ScheduledExecutorService executor;
    
    @Override
    public void init() throws ServletException
    {
        super.init();
        this.executor = Executors.newSingleThreadScheduledExecutor();
    }
    
    @Override
    public void destroy()
    {
        this.executor.shutdown();
        super.destroy();
    }

    @Override
    protected EventSource newEventSource(HttpServletRequest request)
    {
        return new CounterEventSource();
    }
    
    final class CounterEventSource implements EventSource {

        private volatile Emitter emitter;
        
        private volatile boolean closed = false;

        @Override
        public void onOpen(Emitter emitter) throws IOException
        {
            this.emitter = emitter;
            this.sendCount(1L);
            
        }

        @Override
        public void onResume(Emitter emitter, String lastEventId) throws IOException
        {
            this.emitter = emitter;
            try
            {
                long count = Long.parseLong(lastEventId);
                sendCount(count + 1L);
            }
            catch (NumberFormatException e)
            {
                LOG.log(Level.WARNING, "could not parse " + lastEventId + " to a long", e);
                sendCount(1L);
            }
            
            
        }
        
        private void sendCount(final long count) throws IOException {
            if (this.closed)
            {
                return;
            }
            this.emitter.id(Long.toString(count));
            this.emitter.data(Long.toString(count));
            executor.schedule(new Runnable()
            {
                
                @Override
                public void run()
                {
                    try
                    {
                        sendCount(count + 1L);
                    }
                    catch (IOException e)
                    {
                        LOG.log(Level.SEVERE, "could not send update to client", e);
                    }
                    
                }
            }, 5L, TimeUnit.SECONDS);
        }

        @Override
        public void onClose()
        {
            closed = true;
            
        }
        
    }
    
}
