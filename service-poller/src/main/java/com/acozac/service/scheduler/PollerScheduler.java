package com.acozac.service.scheduler;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.jboss.logging.Logger;
import io.quarkus.scheduler.Scheduled;
import io.quarkus.scheduler.ScheduledExecution;
import com.acozac.service.DaoService;
import com.acozac.service.poller.PollerService;

@ApplicationScoped
public class PollerScheduler
{
    DaoService daoService;
    Logger logger;

    private PollerService pollerService;

    public PollerScheduler(DaoService daoService, Logger logger)
    {
        pollerService = new PollerService(daoService, logger);
    }

    @Scheduled(every = "10s")
    void scheduleEvery10Seconds()
    {
        pollerService.pollServices();
    }

   /* @Scheduled(cron = "{cron.expr}")
    void schedule(ScheduledExecution execution)
    {
        pollerService.pollServices();
        System.out.println(execution.getScheduledFireTime());
        logger.debug("Chron scheduled " + execution.getScheduledFireTime());
    }*/
}
