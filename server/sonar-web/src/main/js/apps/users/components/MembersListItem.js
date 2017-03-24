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
import Avatar from '../../../components/ui/Avatar';
import { translatePlural } from '../../../helpers/l10n';
import type { Member } from '../../organizations/store/membersDuck';
import type { Organization } from '../../../store/organizations/duck';

type Props = {
  user: Member,
  organization: Organization
};

const AVATAR_SIZE: number = 36;

export default class MembersListItem extends React.PureComponent {
  props: Props;

  render() {
    const { user, organization } = this.props;
    const avatarComponent = <Avatar hash={user.avatar} size={AVATAR_SIZE} />;
    return (
      <tr>
        {avatarComponent &&
          <td className="thin nowrap">
            {avatarComponent}
          </td>
        }
        <td className="nowrap text-middle"><strong className="js-user-name">{user.name}</strong></td>
        {organization.canAdmin &&
          <td className="text-right text-middle">
            {translatePlural('organization.members.x_group', user.groupCount)}
          </td>
        }
      </tr>
    );
  }
}
