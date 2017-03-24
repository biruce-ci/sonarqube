/*
 * SonarQube
 * Copyright (C) 2009-2017 SonarSource SA
 * mailto:info AT sonarsource DOT com
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
import uniq from 'lodash/uniq';
import { actions } from './membersDuck';

export const getOrganizationMembersLogins = (state, organization) => {
  if (organization && state[organization]) {
    return state[organization].members || [];
  }
  return [];
};
export const getOrganizationMembersState = (state, organization) =>
  organization && state[organization] ? state[organization] : {};

const organizationMembers = (state = {}, action = {}) => {
  const organization = state[action.organization] || {};
  switch (action.type) {
    case actions.UPDATE_STATE:
      return {
        ...state,
        [action.organization]: {
          ...organization,
          ...action.changes
        }
      };
    case actions.RECEIVE_MEMBERS:
      return {
        ...state,
        [action.organization]: {
          ...organization,
          members: action.members.map(member => member.login)
        }
      };
    case actions.RECEIVE_MORE_MEMBERS: {
      return {
        ...state,
        [action.organization]: {
          ...organization,
          members: uniq(
            getOrganizationMembersLogins(state, action.organization).concat(
              action.members.map(member => member.login)
            )
          )
        }
      };
    }
    default:
      return state;
  }
};

export default organizationMembers;
