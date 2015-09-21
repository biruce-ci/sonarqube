/*
 * SonarQube, open source software quality management tool.
 * Copyright (C) 2008-2014 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * SonarQube is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * SonarQube is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.computation.queue;

import com.google.common.base.Optional;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;
import org.sonar.core.util.logs.Profiler;
import org.sonar.db.ce.CeActivityDto;
import org.sonar.server.computation.log.CeLogging;
import org.sonar.server.computation.queue.report.ReportTaskProcessor;

import static java.lang.String.format;

class CeWorkerRunnable implements Runnable {

  private static final Logger LOG = Loggers.get(CeWorkerRunnable.class);

  private final CeQueue queue;
  private final ReportTaskProcessor reportTaskProcessor;
  private final CeLogging ceLogging;

  public CeWorkerRunnable(CeQueue queue, ReportTaskProcessor reportTaskProcessor, CeLogging ceLogging) {
    this.queue = queue;
    this.reportTaskProcessor = reportTaskProcessor;
    this.ceLogging = ceLogging;
  }

  @Override
  public void run() {
    Optional<CeTask> ceTask = tryAndFindTaskToExecute();
    if (!ceTask.isPresent()) {
      return;
    }

    executeTask(ceTask.get());
  }

  private Optional<CeTask> tryAndFindTaskToExecute() {
    try {
      return queue.peek();
    } catch (Exception e) {
      LOG.error("Failed to pop the queue of analysis reports", e);
    }
    return Optional.absent();
  }

  private void executeTask(CeTask task) {
    ceLogging.initForTask(task);
    Profiler profiler = Profiler.create(LOG).startInfo(format("Analysis of project %s (report %s)", task.getComponentKey(), task.getUuid()));
    try {
      // TODO delegate the message to the related task processor, according to task type
      reportTaskProcessor.process(task);
      queue.remove(task, CeActivityDto.Status.SUCCESS);
    } catch (Throwable e) {
      LOG.error(format("Failed to process task %s", task.getUuid()), e);
      queue.remove(task, CeActivityDto.Status.FAILED);
    } finally {
      profiler.stopInfo(String.format("Total thread execution of project %s (report %s)", task.getComponentUuid(), task.getUuid()));
      ceLogging.clearForTask();
    }
  }
}