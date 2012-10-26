/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2012 SonarSource
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.batch.bootstrapper;

import org.sonar.api.BatchComponent;

/**
 * Describes execution environment.
 * 
 * @since 2.6
 */
public class EnvironmentInformation implements BatchComponent {

  private String key;
  private String version;

  public EnvironmentInformation(String key, String version) {
    this.key = key;
    this.version = version;
  }

  /**
   * @return unique key of environment, for example - "maven", "ant"
   */
  public String getKey() {
    return key;
  }

  /**
   * @return version of environment, for example Maven can have "2.2.1" or "3.0.2",
   *         but there is no guarantees about format - it's just a string.
   */
  public String getVersion() {
    return version;
  }

  @Override
  public String toString() {
    return String.format("%s/%s", key, version);
  }
}
