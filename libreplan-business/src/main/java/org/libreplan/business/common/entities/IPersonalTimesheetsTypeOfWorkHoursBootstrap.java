/*
 * This file is part of LibrePlan
 *
 * Copyright (C) 2012 Igalia, S.L.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.libreplan.business.common.entities;

import org.libreplan.business.IDataBootstrap;

/**
 * Contract for {@link PersonalTimesheetsTypeOfWorkHoursBootstrap}.
 *
 * @author Manuel Rego Casasnovas <mrego@igalia.com>
 */
public interface IPersonalTimesheetsTypeOfWorkHoursBootstrap extends
        IDataBootstrap {

    void loadRequiredData();

}