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
import { formatMeasure } from '../../../helpers/measures';
import { translate, translatePlural } from '../../../helpers/l10n';

type Props = {
  loading: boolean,
  total?: number,
  organization?: {},
  children?: {}
};

export default class PageHeader extends React.PureComponent {
  props: Props;

  static defaultProps = {
    loading: false
  };

  render() {
    const { organization } = this.props;
    return (
      <header className="page-header">
        {!organization && <h1 className="page-title">{translate('users.page')}</h1>}
        {this.props.loading && <i className="spinner" />}
        {this.props.children}
        {!organization &&
          <div className="page-description">{translate('users.page.description')}</div>}
        {this.props.total != null &&
          <span className="page-totalcount">
            <strong>{formatMeasure(this.props.total, 'INT')}</strong>
            {' '}
            {translatePlural(
              'organization.members.member',
              this.props.total || 0,
              'organization.members.members'
            )}
          </span>}
      </header>
    );
  }
}
