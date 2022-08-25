package com.swisscom.ba2.interviews.javaspringjooq.service;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.swisscom.ba2.interviews.javaspringjooq.db.h2.Tables.BOOK;

@Service
public class DefaultBookService implements BookService {

    private final DSLContext dsl;

    public DefaultBookService(@Autowired DSLContext dsl) {
        this.dsl = dsl;
    }

    @Override
    @Transactional
    public void create(int id, int authorId, String title) {

        // This method has a "bug". It creates the same book twice.
        for (int i = 0; i < 2; i++)
            dsl.insertInto(BOOK).set(BOOK.ID, id).set(BOOK.AUTHOR_ID, authorId).set(BOOK.TITLE, title).execute();
    }
}
