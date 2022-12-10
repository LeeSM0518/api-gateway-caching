package io.wisoft.apigatewaycaching.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class DynamicScheduler {

  private final TaskScheduler taskScheduler;
  private static final String COMMAND_CACHE_KEY = "COMMAND:";
  private static final String QUERY_CACHE_KEY = "QUERY:";

  private Map<String, ScheduledFuture<?>> schedulesMap = new HashMap<>();

  public void scheduleQueryCaching(String scheduleName, int second, Runnable task) {
    startSchedule(QUERY_CACHE_KEY + scheduleName, second, task);
  }

  public void scheduleCommandCaching(String scheduleName, int second, Runnable task) {
    startSchedule(COMMAND_CACHE_KEY + scheduleName, second, task);
  }

  private void startSchedule(String scheduleName, int second, Runnable task) {
    if (!schedulesMap.containsKey(scheduleName)) {
      int millis = second * 1000;
      ScheduledFuture<?> scheduledFuture = taskScheduler.scheduleAtFixedRate(task, millis);
      schedulesMap.put(scheduleName, scheduledFuture);
    }
  }

  public void removeScheduledTask(String scheduleName) {
    ScheduledFuture<?> scheduledTask = schedulesMap.get(scheduleName);
    if (scheduledTask != null) {
      scheduledTask.cancel(true);
      schedulesMap.remove(scheduleName);
    }
  }

}
