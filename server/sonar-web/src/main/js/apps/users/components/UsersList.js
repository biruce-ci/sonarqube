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
//@flow
import React from 'react';
import MembersListItem from './MembersListItem';
import type { Member } from '../../organizations/store/membersDuck';
import type { Organization } from '../../../store/organizations/duck';

type Props = {
  users: Array<Member>,
  organization?: Organization
};

export default class UsersList extends React.PureComponent {
  props: Props;

  render() {
    return (
      <table className="data zebra">
        <tbody>
          {this.props.users.map(user => (
            <MembersListItem key={user.login} user={user} organization={this.props.organization} />
          ))}
        </tbody>
      </table>
    );
  }
}
