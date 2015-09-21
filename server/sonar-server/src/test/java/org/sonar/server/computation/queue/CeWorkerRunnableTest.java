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
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.sonar.db.ce.CeActivityDto;
import org.sonar.db.ce.CeTaskTypes;
import org.sonar.server.computation.queue.report.ReportTaskProcessor;
import org.sonar.server.computation.log.CeLogging;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

public class CeWorkerRunnableTest {

  CeQueue queue = mock(CeQueueImpl.class);
  ReportTaskProcessor taskProcessor = mock(ReportTaskProcessor.class);
  CeLogging ceLogging = mock(CeLogging.class);
  CeWorkerRunnable underTest = new CeWorkerRunnable(queue, taskProcessor, ceLogging);

  @Test
  public void no_pending_tasks_in_queue() throws Exception {
    when(queue.peek()).thenReturn(Optional.<CeTask>absent());

    underTest.run();

    verifyZeroInteractions(taskProcessor, ceLogging);
  }

  @Test
  public void peek_and_process_task() throws Exception {
    CeTask task = new CeTask.Builder().setUuid("TASK_1").setType(CeTaskTypes.REPORT).setComponentUuid("PROJECT_1").setSubmitterLogin(null).build();
    when(queue.peek()).thenReturn(Optional.of(task));

    underTest.run();

    InOrder inOrder = Mockito.inOrder(ceLogging, taskProcessor, queue);
    inOrder.verify(ceLogging).initForTask(task);
    inOrder.verify(taskProcessor).process(task);
    inOrder.verify(queue).remove(task, CeActivityDto.Status.SUCCESS);
    inOrder.verify(ceLogging).clearForTask();
  }

  @Test
  public void fail_to_process_task() throws Exception {
    CeTask task = new CeTask.Builder().setUuid("TASK_1").setType(CeTaskTypes.REPORT).setComponentUuid("PROJECT_1").setSubmitterLogin(null).build();
    when(queue.peek()).thenReturn(Optional.of(task));
    doThrow(new IllegalStateException()).when(taskProcessor).process(task);

    underTest.run();

    InOrder inOrder = Mockito.inOrder(ceLogging, taskProcessor, queue);
    inOrder.verify(ceLogging).initForTask(task);
    inOrder.verify(taskProcessor).process(task);
    inOrder.verify(queue).remove(task, CeActivityDto.Status.FAILED);
    inOrder.verify(ceLogging).clearForTask();
  }
}