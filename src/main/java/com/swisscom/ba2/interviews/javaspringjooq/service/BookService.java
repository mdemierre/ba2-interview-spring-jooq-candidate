package com.swisscom.ba2.interviews.javaspringjooq.service;

import org.springframework.transaction.annotation.Transactional;

/**
 * Interface for the book service/DAO/repository.
 */
public interface BookService {

    /**
     * Create a new book.
     * <p>
     * The implementation of this method has a bug, which causes this method to fail and roll back the transaction.
     */
    @Transactional
    void create(int id, int authorId, String title);
}
