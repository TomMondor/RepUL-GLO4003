package ca.ulaval.glo4003.repul.subscription.api.jobs;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;

import ca.ulaval.glo4003.repul.commons.api.jobs.RepULJob;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;

public class ProcessConfirmationForTheDayJob implements RepULJob {
    private final SubscriberService subscriberService;

    public ProcessConfirmationForTheDayJob(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        subscriberService.processOrders();
    }

    public JobDetail getJobDetail() {
        return JobBuilder.newJob(this.getClass()).build();
    }

    public Trigger getTrigger() {
        String everyDayAt9AM = "0 0 9 * * ?";
        return TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(everyDayAt9AM)).build();
    }
}
