package org.apache.oozie.executor.jpa.sla;

import org.apache.oozie.ErrorCode;
import org.apache.oozie.client.event.EventStatusSLA;
import org.apache.oozie.executor.jpa.JPAExecutorException;
import org.apache.oozie.sla.SLASummaryBean;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

enum EventStatusFilter {
    START_MET_FILTER {
        public List<Predicate> createFilterCondition(CriteriaBuilder criteriaBuilder, Root<SLASummaryBean> root) {
            return Collections.singletonList(criteriaBuilder.and(
                    criteriaBuilder.isNotNull(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_START_TS)),
                    criteriaBuilder.isNotNull(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_START_TS)),
                    criteriaBuilder.ge(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_START_TS), root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_START_TS))));
        }
    },
    START_MISS_FILTER {
        public List<Predicate> createFilterCondition(CriteriaBuilder criteriaBuilder, Root<SLASummaryBean> root) {
            Timestamp currentTime = new Timestamp(new Date().getTime());
            return Arrays.asList(criteriaBuilder.and(
                            criteriaBuilder.isNotNull(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_START_TS)),
                            criteriaBuilder.isNotNull(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_START_TS)),
                            criteriaBuilder.lessThanOrEqualTo(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_START_TS),
                                    root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_START_TS))),
                    criteriaBuilder.and(
                            criteriaBuilder.isNotNull(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_START_TS)),
                            criteriaBuilder.isNull(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_START_TS)),
                            criteriaBuilder.lessThanOrEqualTo(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_START_TS), currentTime)));
        }
    },
    DURATION_MET_FILTER {
        public List<Predicate> createFilterCondition(CriteriaBuilder criteriaBuilder, Root<SLASummaryBean> root) {
            return Collections.singletonList(criteriaBuilder.and(
                    criteriaBuilder.notEqual(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_DURATION), -1),
                    criteriaBuilder.notEqual(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_DURATION), -1),
                    criteriaBuilder.ge(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_DURATION), root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_DURATION))));
        }
    },
    DURATION_MISS_FILTER {
        public List<Predicate> createFilterCondition(CriteriaBuilder criteriaBuilder, Root<SLASummaryBean> root) {
            return Arrays.asList(criteriaBuilder.and(
                            criteriaBuilder.notEqual(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_DURATION), -1),
                            criteriaBuilder.notEqual(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_DURATION), -1),
                            criteriaBuilder.lessThan(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_DURATION), root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_DURATION))),
                    criteriaBuilder.equal(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EVENT_STATUS), EventStatusSLA.DURATION_MISS.name())
            );
        }
    },
    END_MET_FILTER {
        public List<Predicate> createFilterCondition(CriteriaBuilder criteriaBuilder, Root<SLASummaryBean> root) {
            return Collections.singletonList(criteriaBuilder.and(
                    criteriaBuilder.isNotNull(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_END_TS)),
                    criteriaBuilder.isNotNull(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_END_TS)),
                    criteriaBuilder.greaterThanOrEqualTo(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_END_TS),
                            root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_END_TS))));
        }
    },
    END_MISS_FILTER {
        public List<Predicate> createFilterCondition(CriteriaBuilder criteriaBuilder, Root<SLASummaryBean> root) {
            Timestamp currentTime = new Timestamp(new Date().getTime());
            return Arrays.asList(criteriaBuilder.and(
                            criteriaBuilder.isNotNull(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_END_TS)),
                            criteriaBuilder.isNotNull(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_END_TS)),
                            criteriaBuilder.lessThanOrEqualTo(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_END_TS),
                                    root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_END_TS))),
                    criteriaBuilder.and(
                            criteriaBuilder.isNotNull(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_END_TS)),
                            criteriaBuilder.isNull(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_ACTUAL_END_TS)),
                            criteriaBuilder.lessThanOrEqualTo(root.get(SLASummaryGetForFilterJPAExecutor.DBFIELD_EXPECTED_END_TS), currentTime)
                    ));
        }
    };

    abstract List<Predicate> createFilterCondition(CriteriaBuilder criteriaBuilder, Root<SLASummaryBean> root);

    static List<Predicate> createFilterConditionForEventStatus(EventStatusSLA eventStatus,
                                                               CriteriaBuilder criteriaBuilder,
                                                               Root<SLASummaryBean> root) throws JPAExecutorException {
        try {
            EventStatusFilter eventStatusFilter = EventStatusFilter.valueOf(eventStatus.name() + "_FILTER");
            return eventStatusFilter.createFilterCondition(criteriaBuilder, root);
        } catch (IllegalArgumentException e) {
            throw new JPAExecutorException(ErrorCode.E0303, FilterField.EVENT_STATUS.getColumnName(), eventStatus.name());
        }
    }
}
