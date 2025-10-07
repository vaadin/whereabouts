package com.example.application.common;

import org.jspecify.annotations.NullMarked;

@NullMarked
public interface CrudRepository<ID extends Identifier, AR extends AggregateRoot<ID, DATA>, DATA extends Record> extends
        Repository.WithInsert<ID, AR, DATA>,
        Repository.WithUpdate<ID, AR, DATA>,
        Repository.WithDelete<ID, AR, DATA> {
}
