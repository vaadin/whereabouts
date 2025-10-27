package com.example.whereabouts.security.internal.jooq;

import com.example.whereabouts.jooq.tables.records.AppUserRoleRecord;
import com.example.whereabouts.security.User;
import com.example.whereabouts.security.UserId;
import com.example.whereabouts.security.internal.UserRepository;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

import static com.example.whereabouts.jooq.Sequences.APP_USER_ID_SEQ;
import static com.example.whereabouts.jooq.Tables.APP_USER;
import static com.example.whereabouts.jooq.Tables.APP_USER_ROLE;

@Component
@NullMarked
class JooqUserRepository implements UserRepository {

    private final DSLContext dsl;

    JooqUserRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public UserId insert(String username, @Nullable String password, String displayName, Set<String> roles) {
        var id = new UserId(dsl.nextval(APP_USER_ID_SEQ));
        dsl.insertInto(APP_USER)
                .set(APP_USER.USER_ID, id.value())
                .set(APP_USER.VERSION, 1L)
                .set(APP_USER.USERNAME, username)
                .set(APP_USER.ENCODED_PASSWORD, password)
                .set(APP_USER.DISPLAY_NAME, displayName)
                .set(APP_USER.ENABLED, true)
                .execute();

        insertRoles(id, roles);
        return id;
    }

    private void insertRoles(UserId userId, Set<String> roles) {
        if (roles.isEmpty()) {
            return;
        }

        var batch = roles.stream()
                .map(role -> {
                    var record = dsl.newRecord(APP_USER_ROLE);
                    record.setUserId(userId.value());
                    record.setRoleName(role);
                    return record;
                })
                .toList();

        dsl.batchInsert(batch).execute();
    }

    @Transactional(readOnly = true, propagation = Propagation.MANDATORY)
    @Override
    public Optional<User> findByUsername(String username) {
        var ROLES = DSL.multiset(
                DSL.selectFrom(APP_USER_ROLE)
                        .where(APP_USER_ROLE.USER_ID.eq(APP_USER.USER_ID))
        );
        return dsl
                .select(APP_USER.USER_ID,
                        APP_USER.VERSION,
                        APP_USER.USERNAME,
                        APP_USER.ENCODED_PASSWORD,
                        APP_USER.DISPLAY_NAME,
                        APP_USER.ENABLED,
                        ROLES
                )
                .from(APP_USER)
                .where(APP_USER.USERNAME.eq(username))
                .fetchOptional(record -> new User(
                        new UserId(record.getValue(APP_USER.USER_ID)),
                        record.getValue(APP_USER.VERSION),
                        record.getValue(APP_USER.USERNAME),
                        record.getValue(APP_USER.ENCODED_PASSWORD),
                        record.getValue(APP_USER.DISPLAY_NAME),
                        record.getValue(APP_USER.ENABLED),
                        record.getValue(ROLES).map(AppUserRoleRecord::getRoleName)
                ));
    }
}
