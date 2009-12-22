/*
 * This file is part of ###PROJECT_NAME###
 *
 * Copyright (C) 2009 Fundación para o Fomento da Calidade Industrial e
 *                    Desenvolvemento Tecnolóxico de Galicia
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

package org.navalplanner.web.common;

import static org.navalplanner.web.I18nHelper._;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.navalplanner.business.calendars.daos.IBaseCalendarDAO;
import org.navalplanner.business.calendars.entities.BaseCalendar;
import org.navalplanner.business.common.daos.IConfigurationDAO;
import org.navalplanner.business.common.daos.IOrderSequenceDAO;
import org.navalplanner.business.common.entities.Configuration;
import org.navalplanner.business.common.entities.OrderSequence;
import org.navalplanner.business.common.exceptions.InstanceNotFoundException;
import org.navalplanner.business.common.exceptions.ValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Manuel Rego Casasnovas <mrego@igalia.com>
 */
@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class ConfigurationModel implements IConfigurationModel {

    /**
     * Conversation state
     */
    private Configuration configuration;

    private List<OrderSequence> orderSequences;

    @Autowired
    private IConfigurationDAO configurationDAO;

    @Autowired
    private IBaseCalendarDAO baseCalendarDAO;

    @Autowired
    private IOrderSequenceDAO orderSequenceDAO;

    @Override
    @Transactional(readOnly = true)
    public List<BaseCalendar> getCalendars() {
        return baseCalendarDAO.getBaseCalendars();
    }

    @Override
    public BaseCalendar getDefaultCalendar() {
        if (configuration == null) {
            return null;
        }
        return configuration.getDefaultCalendar();
    }

    @Override
    @Transactional(readOnly = true)
    public void init() {
        this.configuration = getCurrentConfiguration();
        this.orderSequences = orderSequenceDAO.getAll();
    }

    private Configuration getCurrentConfiguration() {
        Configuration configuration = configurationDAO.getConfiguration();
        if (configuration == null) {
            configuration = Configuration.create();
        }
        forceLoad(configuration);
        return configuration;
    }

    private void forceLoad(Configuration configuration) {
        forceLoad(configuration.getDefaultCalendar());
    }

    private void forceLoad(BaseCalendar calendar) {
        if (calendar != null) {
            calendar.getName();
        }
    }

    @Override
    public void setDefaultCalendar(BaseCalendar calendar) {
        if (configuration != null) {
            configuration.setDefaultCalendar(calendar);
        }
    }

    @Override
    @Transactional
    public void confirm() {
        if (orderSequences.isEmpty()) {
            throw new ValidationException(
                    _("At least one order sequence is needed"));
        }

        if (!checkConstraintJustOneOrderSequenceActive()) {
            throw new ValidationException(
                    _("Just one order sequence must be active"));
        }

        if (!checkConstraintPrefixNotRepeated()) {
            throw new ValidationException(
                    _("Order sequence prefixes can not be repeated"));
        }

        configurationDAO.save(configuration);
        storeAndRemoveOrderSequences();
    }

    private boolean checkConstraintPrefixNotRepeated() {
        Set<String> prefixes = new HashSet<String>();
        for (OrderSequence orderSequence : orderSequences) {
            String prefix = orderSequence.getPrefix();
            if (prefixes.contains(prefix)) {
                return false;
            }
            prefixes.add(prefix);
        }
        return true;
    }

    private void storeAndRemoveOrderSequences() {
        for (OrderSequence orderSequence : orderSequences) {
            orderSequenceDAO.save(orderSequence);
        }

        List<OrderSequence> toRemove = orderSequenceDAO
                .findOrderSquencesNotIn(orderSequences);
        for (OrderSequence orderSequence : toRemove) {
            try {
                orderSequenceDAO.remove(orderSequence.getId());
            } catch (InstanceNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean checkConstraintJustOneOrderSequenceActive() {
        boolean someoneActive = false;
        for (OrderSequence orderSequence : orderSequences) {
            if (orderSequence.isActive()) {
                if (someoneActive) {
                    return false;
                }
                someoneActive = true;
            }
        }
        return someoneActive;
    }

    @Override
    @Transactional(readOnly = true)
    public void cancel() {
        init();
    }

    @Override
    public String getCompanyCode() {
        if (configuration == null) {
            return null;
        }
        return configuration.getCompanyCode();
    }

    @Override
    public void setCompanyCode(String companyCode) {
        if (configuration != null) {
            configuration.setCompanyCode(companyCode);
        }
    }

    @Override
    public List<OrderSequence> getOrderSequences() {
        return Collections.unmodifiableList(orderSequences);
    }

    @Override
    public void addOrderSequence() {
        orderSequences.add(OrderSequence.create(""));
    }

    @Override
    public void removeOrderSequence(OrderSequence orderSequence) {
        orderSequences.remove(orderSequence);
    }

}