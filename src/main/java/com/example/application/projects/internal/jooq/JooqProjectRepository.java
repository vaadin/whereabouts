package com.example.application.projects.internal.jooq;

import com.example.application.projects.Project;
import com.example.application.projects.ProjectData;
import com.example.application.projects.ProjectId;
import com.example.application.projects.internal.ProjectRepository;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static com.example.application.jooq.Sequences.PROJECT_ID_SEQ;
import static com.example.application.jooq.Tables.PROJECT;

@Component
@NullMarked
class JooqProjectRepository implements ProjectRepository {

    private final DSLContext dsl;

    JooqProjectRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    @Override
    public Optional<Project> findById(ProjectId id) {
        return dsl
                .select(PROJECT.PROJECT_ID,
                        PROJECT.VERSION,
                        PROJECT.NAME,
                        PROJECT.DESCRIPTION
                )
                .from(PROJECT)
                .where(PROJECT.PROJECT_ID.eq(id.toLong()))
                .fetchOptional(record -> new Project(
                        ProjectId.of(record.getValue(PROJECT.PROJECT_ID)),
                        record.getValue(PROJECT.VERSION),
                        new ProjectData(
                                record.getValue(PROJECT.NAME),
                                record.getValue(PROJECT.DESCRIPTION)
                        )
                ));
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public ProjectId insert(ProjectData data) {
        var id = ProjectId.of(dsl.nextval(PROJECT_ID_SEQ));
        dsl.insertInto(PROJECT)
                .set(PROJECT.PROJECT_ID, id.toLong())
                .set(PROJECT.VERSION, 1L)
                .set(PROJECT.NAME, data.name())
                .set(PROJECT.DESCRIPTION, data.description())
                .execute();
        return id;
    }
}
