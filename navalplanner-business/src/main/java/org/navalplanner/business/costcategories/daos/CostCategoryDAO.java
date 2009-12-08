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

package org.navalplanner.business.costcategories.daos;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.navalplanner.business.common.daos.GenericDAOHibernate;
import org.navalplanner.business.costcategories.entities.CostCategory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

/**
 * @author Jacobo Aragunde Perez <jaragunde@igalia.com>
 */
@Repository
@Scope(BeanDefinition.SCOPE_SINGLETON)
public class CostCategoryDAO extends GenericDAOHibernate<CostCategory, Long>
        implements ICostCategoryDAO {


    @Override
    public List<CostCategory> findActive() {

        Criteria c = getSession().createCriteria(CostCategory.class);
        c.add(Restrictions.eq("enabled", true));

        List<CostCategory> list = new ArrayList<CostCategory>();
        list.addAll(c.list());
        return list;
    }
}
