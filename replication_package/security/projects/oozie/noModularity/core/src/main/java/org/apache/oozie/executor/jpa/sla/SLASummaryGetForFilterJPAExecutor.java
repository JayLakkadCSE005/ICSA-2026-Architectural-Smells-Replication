/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.oozie.executor.jpa.sla;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import org.apache.oozie.bundle.BundleActionBean;
import org.apache.oozie.BundleJobBean;
import org.apache.oozie.ErrorCode;
import org.apache.oozie.client.event.EventStatusSLA;
import org.apache.oozie.executor.jpa.JPAExecutor;
import org.apache.oozie.executor.jpa.JPAExecutorException;
import org.apache.oozie.service.UUIDService;
import org.apache.oozie.sla.SLASummaryBean;
import org.apache.oozie.util.XLog;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.Metamodel;
import javax.servlet.ServletException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Load the list of SLASummaryBean (for dashboard) and return the list.
 */
public class SLASummaryGetForFilterJPAExecutor implements JPAExecutor<List<SLASummaryBean>> {

    static final String DBFIELD_EXPECTED_START_TS = "expectedStartTS";
    static final String DBFIELD_ACTUAL_START_TS = "actualStartTS";
    static final String DBFIELD_EXPECTED_DURATION = "expectedDuration";
    static final String DBFIELD_ACTUAL_DURATION = "actualDuration";
    static final String DBFIELD_EXPECTED_END_TS = "expectedEndTS";
    static final String DBFIELD_ACTUAL_END_TS = "actualEndTS";
    static final String DBFIELD_EVENT_STATUS = "eventStatus";
    static final String DBFIELD_CREATED_TIME_TS = "createdTimeTS";
    static final String DBFIELD_NOMINAL_TIME_TS = "nominalTimeTS";
    static final String DBFIELD_JOB_STATUS = "jobStatus";
    static final String DBFIELD_USER = "user";
    static final String DBFIELD_APP_TYPE = "appType";
    public static final String DBFIELD_APP_NAME = "appName";
    static final String DBFIELD_SLA_STATUS = "slaStatus";
    static final String DBFIELD_JOB_ID = "jobId";
    static final String DBFIELD_PARENT_ID = "parentId";
    private static final String DBFIELD_BUNDLE_ID = "bundleId";
    private static final String DBFIELD_ID = "id";
    private static final String DBFIELD_COORD_ID = "coordId";
    private static final String DEFAULT_SORTBY_COLUMN = DBFIELD_NOMINAL_TIME_TS;
    @VisibleForTesting
    final FilterCollection filterCollection;
    private int numMaxResults;
    private List<String> possibleSortbyColumns;
    private String sortbyColumn;
    private boolean isDescendingOrder;
    static XLog LOG = XLog.getLog(SLASummaryGetForFilterJPAExecutor.class);
    private CriteriaBuilder criteriaBuilder;
    private CriteriaQuery<SLASummaryBean> criteriaQuery;
    private Root<SLASummaryBean> root;
    private List<Predicate> preds;
    static final String multiValueSeparator = ",";
    /**
     * Matches a bundle_id which has the ${padded_counter}-${startTime}-${systemId}-B format,
     * where padded_counter is exactly 7 digits, startTime is exactly 15 digits and
     * systemId is at most 10 characters like {@code 1234567-150130225116604-oozie-B}
     *
     * {@link UUIDService#generateId(UUIDService.ApplicationType)}
     */
    private static final Pattern BUNDLE_ID_PATTERN = Pattern.compile("\\d{7}-\\d{15}-.{1,10}-B$");

    public SLASummaryGetForFilterJPAExecutor(int numMaxResults) {
        this.filterCollection = new FilterCollection();
        this.numMaxResults = numMaxResults;
    }

    @Override
    public String getName() {
        return SLASummaryGetForFilterJPAExecutor.class.getSimpleName();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<SLASummaryBean> execute(EntityManager em) throws JPAExecutorException {
        initPossibleSortbyColumnList(em);
        createCriteriaQuery(em);
        TypedQuery<SLASummaryBean> typedQuery = em.createQuery(criteriaQuery);
        typedQuery.setMaxResults(numMaxResults);
        LOG.debug("Query string: {0}",typedQuery.unwrap(org.apache.openjpa.persistence.QueryImpl.class).getQueryString());
        return typedQuery.getResultList();
    }

    private void initPossibleSortbyColumnList(EntityManager em) {
        Metamodel metamodel = em.getMetamodel();
        EntityType<SLASummaryBean> slaSummaryBeanEntityType = metamodel.entity(SLASummaryBean.class);
        Set<Attribute<SLASummaryBean,?>> slaSummaryBeanAttributes = slaSummaryBeanEntityType.getDeclaredAttributes();
        possibleSortbyColumns = new ArrayList<>();
        for (Attribute<SLASummaryBean,?> attribute : slaSummaryBeanAttributes) {
            possibleSortbyColumns.add(attribute.getName());
        }
    }

    private void createCriteriaQuery(final EntityManager em) throws JPAExecutorException {
        ensureCriteriaFields(em);
        createSelectFrom();
        createWhereCondition();
        createOrderByClause();
    }

    private void createOrderByClause() throws JPAExecutorException {
        if (Strings.isNullOrEmpty(sortbyColumn)) {
            sortbyColumn = DEFAULT_SORTBY_COLUMN;
        }
        if (!possibleSortbyColumns.contains(sortbyColumn)) {
            String errorMessage = String.format("invalid sortby column: %s", sortbyColumn);
            LOG.error(errorMessage);
            throw new JPAExecutorException(ErrorCode.E0303, errorMessage);
        }

        Path<Object> sortbyColumnPath = root.get(sortbyColumn);
        Path<Object> nominalTimeTSPath = root.get(DBFIELD_NOMINAL_TIME_TS);
        List<Order> orderList = new ArrayList<>();
        if (isDescendingOrder) {
            orderList.add(criteriaBuilder.desc(sortbyColumnPath));
        }
        else {
            orderList.add(criteriaBuilder.asc(sortbyColumnPath));
        }
        if (!DBFIELD_NOMINAL_TIME_TS.equals(sortbyColumn)) {
            orderList.add(criteriaBuilder.asc(nominalTimeTSPath));
        }
        criteriaQuery.orderBy(orderList);
    }

    private void ensureCriteriaFields(EntityManager em) {
        criteriaBuilder = em.getCriteriaBuilder();
        criteriaQuery = criteriaBuilder.createQuery(SLASummaryBean.class);
    }

    private void createSelectFrom() {
        root = criteriaQuery.from(SLASummaryBean.class);
        criteriaQuery.select(root);
    }

    private void createWhereCondition() throws JPAExecutorException {
        preds = new ArrayList<>();
        for (Map.Entry<FilterField, Object> entry : filterCollection.filterValues.entrySet()) {
            FilterField filterField = entry.getKey();
            Object value = entry.getValue();
            if (filterField.filterComparator != null) {
                switch (filterField.filterComparator) {
                    case LIKE:
                        preds.add(criteriaBuilder.like(root.<String>get(filterField.dbFieldName), (String)value));
                        break;
                    case EQUALS:
                        preds.add(criteriaBuilder.equal(root.get(filterField.dbFieldName), value));
                        break;
                    case GREATER_OR_EQUALS:
                        preds.add(criteriaBuilder.greaterThanOrEqualTo(root.get(filterField.dbFieldName), (Comparable)value));
                        break;
                    case LESSTHAN_OR_EQUALS:
                        preds.add(criteriaBuilder.lessThanOrEqualTo(root.get(filterField.dbFieldName), (Comparable)value));
                        break;
                    case IN:
                        preds.add(root.get(filterField.dbFieldName).in((List<String>)value));
                        break;
                }
            }
        }
        createAndAddSpecialCriterias();
        criteriaQuery.where(preds.toArray(new Predicate[0]));
    }

    private void createAndAddSpecialCriterias() throws JPAExecutorException {
        createAndAddIdAndParentIdCriteria();
        createAndAddBundleFilterCriteria();
        createAndAddEventStatusCriteria();
    }

    private void createAndAddIdAndParentIdCriteria() {
        String jobId = (String) getFilterField(DBFIELD_ID);
        String parentId = (String) getFilterField( FilterField.PARENT_ID.getColumnName());

        if (jobId != null && parentId != null) {
            preds.add(criteriaBuilder.or(
                    criteriaBuilder.equal(root.get(FilterField.ID.dbFieldName), jobId),
                    criteriaBuilder.equal(root.get(FilterField.PARENT_ID.dbFieldName), parentId)
            ));
        }
        else if (jobId != null && parentId == null) {
            preds.add(criteriaBuilder.equal(root.get(FilterField.ID.dbFieldName), jobId));
        }
        else if (jobId == null && parentId != null) {
            preds.add(criteriaBuilder.equal(root.get(FilterField.PARENT_ID.dbFieldName), parentId));
        }
    }

    private void createAndAddBundleFilterCriteria() {
        String bundle = (String) getFilterField(FilterField.BUNDLE.getColumnName());

        if (bundle == null) {
            return;
        }
        Subquery<BundleActionBean> subquery = criteriaQuery.subquery(BundleActionBean.class);
        Root<BundleJobBean> subJobBeanRoot = subquery.from(BundleJobBean.class);
        Root<BundleActionBean> subActionBeanRoot = subquery.from(BundleActionBean.class);
        subquery.select(subActionBeanRoot.get(DBFIELD_COORD_ID));
        Predicate bundleJoinPredicate = criteriaBuilder.equal(subActionBeanRoot.get(DBFIELD_BUNDLE_ID),
                subJobBeanRoot.get(DBFIELD_ID));
        if (isBundleId(bundle)) {
            subquery.where(bundleJoinPredicate, criteriaBuilder.equal(subActionBeanRoot.get(DBFIELD_BUNDLE_ID), bundle));
        }
        else {
            subquery.where(bundleJoinPredicate, criteriaBuilder.equal(subJobBeanRoot.get(DBFIELD_APP_NAME), bundle));
        }
        preds.add(criteriaBuilder.in(root.get(DBFIELD_PARENT_ID)).value(subquery));
    }

    private void createAndAddEventStatusCriteria() throws JPAExecutorException {
        String eventStatusFilterFieldName = FilterField.EVENT_STATUS.getColumnName();
        List<String> eventStatusFilterValues = (List<String>)getFilterField(eventStatusFilterFieldName);
        if (eventStatusFilterValues != null) {
            List<Predicate> eventStatusPreds = new ArrayList<>();
            for (String statusStr : eventStatusFilterValues) {
                EventStatusSLA status;
                try {
                    status = EventStatusSLA.valueOf(statusStr);
                }
                catch (IllegalArgumentException e) {
                    throw new JPAExecutorException(ErrorCode.E0303, eventStatusFilterFieldName, statusStr);
                }
                eventStatusPreds.addAll(EventStatusFilter.createFilterConditionForEventStatus(status, criteriaBuilder, root));
            }
            addEventStatusCriteria(eventStatusPreds);
        }
    }

    private void addEventStatusCriteria(List<Predicate> eventStatusPreds) {
        preds.add(criteriaBuilder.or(eventStatusPreds.toArray(new Predicate[0])));
    }

    public void checkAndSetFilterField(String name, String value) throws ServletException, ParseException {
        filterCollection.checkAndSetFilterField(name, value);
    }

    public void setDescendingOrder(boolean isDescendingOrder) {
        this.isDescendingOrder = isDescendingOrder;
    }

    public void setSortbyColumn(String sortbyColumn) {
        this.sortbyColumn = sortbyColumn;
    }

    @VisibleForTesting
    Object getFilterField(String name) {
        return filterCollection.getFilterField(name);
    }

    private boolean isBundleId(String id) {
        return BUNDLE_ID_PATTERN.matcher(id).matches();
    }
}
