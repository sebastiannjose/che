/*
 * Copyright (c) 2012-2017 Red Hat, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Red Hat, Inc. - initial API and implementation
 */
package org.eclipse.che.ide.command.manager;

import static java.util.stream.Collectors.toList;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.List;
import org.eclipse.che.api.machine.shared.dto.CommandDto;
import org.eclipse.che.api.promises.client.Function;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.workspace.shared.dto.WorkspaceDto;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandManager;
import org.eclipse.che.ide.api.command.CommandType;
import org.eclipse.che.ide.api.workspace.WorkspaceServiceClient;
import org.eclipse.che.ide.dto.DtoFactory;

/** Responsible for managing the commands which are stored with workspace. */
@Singleton
class WorkspaceCommandManagerDelegate {

  private final DtoFactory dtoFactory;
  private final WorkspaceServiceClient workspaceServiceClient;
  private final AppContext appContext;

  @Inject
  WorkspaceCommandManagerDelegate(
      DtoFactory dtoFactory, WorkspaceServiceClient workspaceServiceClient, AppContext appContext) {
    this.dtoFactory = dtoFactory;
    this.workspaceServiceClient = workspaceServiceClient;
    this.appContext = appContext;
  }

  /** Returns commands which are stored in the workspace with the specified {@code workspaceId}. */
  Promise<List<CommandImpl>> getCommands(String workspaceId) {
    return workspaceServiceClient
        .getCommands(workspaceId)
        .then(
            (Function<List<CommandDto>, List<CommandImpl>>)
                commands -> commands.stream().map(CommandImpl::new).collect(toList()));
  }

  /**
   * Creates new command of the specified type.
   *
   * <p><b>Note</b> that command's name will be generated by {@link CommandManager} and command line
   * will be provided by an appropriate {@link CommandType}.
   */
  Promise<CommandImpl> createCommand(final CommandImpl command) {
    final CommandDto commandDto =
        dtoFactory
            .createDto(CommandDto.class)
            .withName(command.getName())
            .withCommandLine(command.getCommandLine())
            .withType(command.getType())
            .withAttributes(command.getAttributes());

    return workspaceServiceClient
        .addCommand(appContext.getWorkspaceId(), commandDto)
        .then((Function<WorkspaceDto, CommandImpl>) arg -> command);
  }

  /**
   * Updates the command with the specified {@code name} by replacing it with the given {@code
   * command}.
   *
   * <p><b>Note</b> that name of the updated command may differ from the name provided by the given
   * {@code command} in order to prevent name duplication.
   */
  Promise<CommandImpl> updateCommand(final CommandImpl command) {
    final CommandDto commandDto =
        dtoFactory
            .createDto(CommandDto.class)
            .withName(command.getName())
            .withCommandLine(command.getCommandLine())
            .withType(command.getType())
            .withAttributes(command.getAttributes());

    return workspaceServiceClient
        .updateCommand(appContext.getWorkspaceId(), command.getName(), commandDto)
        .then((Function<WorkspaceDto, CommandImpl>) arg -> command);
  }

  /** Removes the command with the specified {@code commandName}. */
  Promise<Void> removeCommand(String commandName) {
    return workspaceServiceClient
        .deleteCommand(appContext.getWorkspaceId(), commandName)
        .then((Function<WorkspaceDto, Void>) arg -> null);
  }
}
