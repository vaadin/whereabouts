package com.example.whereabouts.projects.query;

import com.example.whereabouts.projects.ProjectId;
import com.example.whereabouts.projects.ProjectListItem;
import com.example.whereabouts.projects.ProjectSortableProperty;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.provider.SortOrder;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Stream;

import static com.example.whereabouts.jooq.Tables.*;

/**
 * @see "Design Decision: DD009-20251029-jooq-user-types.md"
 */
@Component
@NullMarked
class JooqProjectQuery implements ProjectQuery {

    private final DSLContext dsl;

    JooqProjectQuery(DSLContext dsl) {
        this.dsl = dsl;
    }

    // TODO If we return a "live stream", it has to be closed afterwards. Not sure Vaadin does this.
    //  Therefore, we fetch a list and return a stream of it.

    @Override
    public Stream<ProjectListItem> findProjectListItemsBySearchTerm(@Nullable String searchTerm, int limit, int offset, SortOrder<ProjectSortableProperty> sortOrder) {
        var condition = searchTerm != null && !searchTerm.isBlank()
                ? PROJECT.NAME.containsIgnoreCase(searchTerm)
                : DSL.trueCondition();
        return selectProject()
                .where(condition)
                .groupBy(PROJECT.PROJECT_ID, PROJECT.NAME)
                .orderBy(toOrderField(sortOrder))
                .limit(limit)
                .offset(offset)
                .fetch(Records.mapping(ProjectListItem::new))
                .stream();
    }

    @Override
    public Optional<ProjectListItem> findProjectListItemById(ProjectId id) {
        return selectProject()
                .where(PROJECT.PROJECT_ID.eq(id))
                .groupBy(PROJECT.PROJECT_ID, PROJECT.NAME)
                .fetchOptional(Records.mapping(ProjectListItem::new));
    }

    private SelectOnConditionStep<Record5<ProjectId, String, String, Integer, Integer>> selectProject() {
        return dsl.select(
                        PROJECT.PROJECT_ID,
                        PROJECT.NAME,
                        PROJECT.DESCRIPTION,
                        DSL.countDistinct(TASK),
                        DSL.countDistinct(TASK_ASSIGNEE)
                ).from(PROJECT)
                .leftJoin(TASK).on(TASK.PROJECT_ID.eq(PROJECT.PROJECT_ID))
                .leftJoin(TASK_ASSIGNEE).on(TASK_ASSIGNEE.TASK_ID.eq(TASK.TASK_ID));
    }

    private OrderField<?> toOrderField(SortOrder<ProjectSortableProperty> sortOrder) {
        return switch (sortOrder.getSorted()) {
            case NAME -> sortOrder.getDirection() == SortDirection.ASCENDING ? PROJECT.NAME.asc() : PROJECT.NAME.desc();
        };
    }
}
