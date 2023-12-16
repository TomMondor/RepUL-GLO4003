package ca.ulaval.glo4003.repul.subscription.api.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;

public class ProcessOrders implements Job {
    private static final String SUBSCRIBER_SERVICE_KEY = "SubscriberService";

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        SubscriberService service = (SubscriberService) jobExecutionContext.getJobDetail()
            .getJobDataMap()
            .get(SUBSCRIBER_SERVICE_KEY);
        service.processOrders();
    }
}
