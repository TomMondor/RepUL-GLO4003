package ca.ulaval.glo4003.repul.config.initializer.jobs;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import ca.ulaval.glo4003.repul.subscription.api.jobs.ProcessOrders;
import ca.ulaval.glo4003.repul.subscription.application.SubscriberService;

public class ProcessOrdersJobInitializer implements JobInitializer {
    private final SubscriberService subscriberService;
    private final String runFrequency;

    public ProcessOrdersJobInitializer(SubscriberService subscriberService, String runFrequency) {
        this.subscriberService = subscriberService;
        this.runFrequency = runFrequency;
    }

    public void launchJob() {
        JobDetail job = getJobDetail();
        Trigger trigger = getTrigger(job);

        startJob(job, trigger);
    }

    private Scheduler getScheduler() {
        try {
            return new StdSchedulerFactory().getScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException("Could not get scheduler", e);
        }
    }

    private JobDetail getJobDetail() {
        JobDataMap data = new JobDataMap();
        data.put("SubscriberService", subscriberService);
        return JobBuilder.newJob(ProcessOrders.class)
            .usingJobData(data)
            .build();
    }

    private Trigger getTrigger(JobDetail jobDetail) {
        return TriggerBuilder
            .newTrigger()
            .forJob(jobDetail)
            .withSchedule(CronScheduleBuilder.cronSchedule(runFrequency))
            .build();
    }

    private void startJob(JobDetail jobDetail, Trigger trigger) {
        try {
            Scheduler scheduler = getScheduler();
            scheduler.start();
            scheduler.scheduleJob(jobDetail, trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException("Could not start job", e);
        }
    }
}
