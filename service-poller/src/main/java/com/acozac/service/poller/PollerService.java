package com.acozac.service.poller;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.inject.Inject;
import org.jboss.logging.Logger;
import com.acozac.service.DaoService;
import com.acozac.service.Service;

public final class PollerService
{
    @Inject
    private final DaoService daoService;
    private final Logger logger;

    public PollerService(DaoService daoService, Logger logger)
    {
        this.daoService = daoService;
        this.logger = logger;
        logger.debug("Pooling services ...");

    }

    public void pollServices()
    {
        daoService.getAllServices()
            .forEach(service -> {
                service.status = pollService(service);
                daoService.update(service);
            });
    }

    private String pollService(Service service)
    {
        URL url;
        try
        {
            url = new URL(service.url);
            HttpURLConnection huc = (HttpURLConnection) url.openConnection();
            logger.debug("Pooling url: " + service.url + " receiving response " + huc.getResponseCode());
            if (huc.getResponseCode() == HttpURLConnection.HTTP_OK)
            {
                return "OK";
            }
            return "FAIL";
        }
        catch (IOException e)
        {
           logger.error("Calling url got exception: ", e);
        }
        return "FAIL";
    }

}
