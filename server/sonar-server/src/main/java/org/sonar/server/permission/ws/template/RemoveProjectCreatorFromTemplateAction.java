/*
 * SonarQube
 * Copyright (C) 2009-2016 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.sonar.server.permission.ws.template;

import java.util.Optional;
import org.sonar.api.server.ws.Request;
import org.sonar.api.server.ws.Response;
import org.sonar.api.server.ws.WebService;
import org.sonar.api.utils.System2;
import org.sonar.db.DbClient;
import org.sonar.db.DbSession;
import org.sonar.db.permission.PermissionTemplateDto;
import org.sonar.db.permission.template.PermissionTemplateCharacteristicDao;
import org.sonar.db.permission.template.PermissionTemplateCharacteristicDto;
import org.sonar.server.permission.ws.PermissionDependenciesFinder;
import org.sonar.server.permission.ws.PermissionsWsAction;
import org.sonar.server.permission.ws.WsTemplateRef;
import org.sonar.server.user.UserSession;
import org.sonarqube.ws.client.permission.RemoveProjectCreatorFromTemplateWsRequest;

import static org.sonar.server.permission.PermissionPrivilegeChecker.checkGlobalAdminUser;
import static org.sonar.server.permission.ws.PermissionRequestValidator.validateProjectPermission;
import static org.sonar.server.permission.ws.PermissionsWsParametersBuilder.createProjectPermissionParameter;
import static org.sonar.server.permission.ws.PermissionsWsParametersBuilder.createTemplateParameters;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_PERMISSION;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_TEMPLATE_ID;
import static org.sonarqube.ws.client.permission.PermissionsWsParameters.PARAM_TEMPLATE_NAME;

public class RemoveProjectCreatorFromTemplateAction implements PermissionsWsAction {
  private final DbClient dbClient;
  private final PermissionDependenciesFinder dependenciesFinder;
  private final UserSession userSession;
  private final System2 system;

  public RemoveProjectCreatorFromTemplateAction(DbClient dbClient, PermissionDependenciesFinder dependenciesFinder, UserSession userSession, System2 system) {
    this.dbClient = dbClient;
    this.dependenciesFinder = dependenciesFinder;
    this.userSession = userSession;
    this.system = system;
  }

  @Override
  public void define(WebService.NewController context) {
    WebService.NewAction action = context.createAction("remove_project_creator_from_template")
      .setDescription("Remove a project creator from a permission template.<br>" +
        "Requires the 'Administer' permission.")
      .setSince("6.0")
      .setPost(true)
      .setHandler(this);

    createTemplateParameters(action);
    createProjectPermissionParameter(action);
  }

  @Override
  public void handle(Request request, Response response) throws Exception {
    checkGlobalAdminUser(userSession);
    doHandle(toWsRequest(request));
    response.noContent();
  }

  private void doHandle(RemoveProjectCreatorFromTemplateWsRequest request) {
    DbSession dbSession = dbClient.openSession(false);
    try {
      PermissionTemplateDto template = dependenciesFinder.getTemplate(dbSession, WsTemplateRef.newTemplateRef(request.getTemplateId(), request.getTemplateName()));
      PermissionTemplateCharacteristicDao dao = dbClient.permissionTemplateCharacteristicDao();
      Optional<PermissionTemplateCharacteristicDto> templatePermission = dao.selectByPermissionAndTemplateId(dbSession, request.getPermission(), template.getId());
      if (templatePermission.isPresent()) {
        updateTemplateCharacteristic(dbSession, templatePermission.get());
      }
    } finally {
      dbClient.closeSession(dbSession);
    }
  }

  private void updateTemplateCharacteristic(DbSession dbSession, PermissionTemplateCharacteristicDto templatePermission) {
    PermissionTemplateCharacteristicDto targetTemplatePermission = templatePermission
      .setUpdatedAt(system.now())
      .setWithProjectCreator(false);
    dbClient.permissionTemplateCharacteristicDao().update(dbSession, targetTemplatePermission);
    dbSession.commit();
  }

  private static RemoveProjectCreatorFromTemplateWsRequest toWsRequest(Request request) {
    RemoveProjectCreatorFromTemplateWsRequest wsRequest = RemoveProjectCreatorFromTemplateWsRequest.builder()
      .setPermission(request.mandatoryParam(PARAM_PERMISSION))
      .setTemplateId(request.param(PARAM_TEMPLATE_ID))
      .setTemplateName(request.param(PARAM_TEMPLATE_NAME))
      .build();
    validateProjectPermission(wsRequest.getPermission());
    return wsRequest;
  }
}
