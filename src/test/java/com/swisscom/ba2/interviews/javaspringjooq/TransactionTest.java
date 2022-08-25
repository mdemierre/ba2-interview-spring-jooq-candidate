package com.swisscom.ba2.interviews.javaspringjooq;

import com.swisscom.ba2.interviews.javaspringjooq.service.BookService;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.swisscom.ba2.interviews.javaspringjooq.db.h2.Tables.BOOK;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * @author Petri Kainulainen
 * @author Lukas Eder
 * @author Thomas Darimont
 * @see <a
 * href="http://www.petrikainulainen.net/programming/jooq/using-jooq-with-spring-configuration/">http://www.petrikainulainen.net/programming/jooq/using-jooq-with-spring-configuration/</a>
 */
@SpringBootTest(classes = Application.class)
public class TransactionTest {

    @Autowired
    DSLContext dsl;
    @Autowired
    DataSourceTransactionManager txMgr;
    @Autowired
    BookService books;

    @AfterEach
    public void teardown() {

        // Delete all books that were created in any test
        dsl.delete(BOOK).where(BOOK.ID.gt(4)).execute();
    }

    @Test
    public void testExplicitTransactions() {
        boolean rollback = false;

        TransactionStatus tx = txMgr.getTransaction(new DefaultTransactionDefinition());
        try {

            // This is a "bug". The same book is created twice, resulting in a
            // constraint violation exception
            for (int i = 0; i < 2; i++)
                dsl.insertInto(BOOK).set(BOOK.ID, 5).set(BOOK.AUTHOR_ID, 1).set(BOOK.TITLE, "Book 5").execute();

            fail();
        }

        // Upon the constraint violation, we explicitly roll back the transaction.
        catch (DataAccessException e) {
            txMgr.rollback(tx);
            rollback = true;
        }

        assertEquals(4, dsl.fetchCount(BOOK));
        assertTrue(rollback);
    }

    @Test
    public void testDeclarativeTransactions() {
        boolean rollback = false;

        try {
            books.create(5, 1, "Book 5");
            fail();
        } catch (DataAccessException ignore) {
            rollback = true;
        }

        assertEquals(4, dsl.fetchCount(BOOK));
        assertTrue(rollback);
    }

    @Test
    public void testjOOQTransactionsSimple() {
        boolean rollback = false;

        try {
            dsl.transaction(c -> {

                // This is a "bug". The same book is created twice, resulting in a
                // constraint violation exception
                for (int i = 0; i < 2; i++)
                    dsl.insertInto(BOOK).set(BOOK.ID, 5).set(BOOK.AUTHOR_ID, 1).set(BOOK.TITLE, "Book 5").execute();

                fail();
            });
        }

        // Upon the constraint violation, the transaction must already have been rolled back
        catch (DataAccessException e) {
            rollback = true;
        }

        assertEquals(4, dsl.fetchCount(BOOK));
        assertTrue(rollback);
    }

    @Test
    public void testjOOQTransactionsNested() {
        AtomicBoolean rollback1 = new AtomicBoolean(false);
        AtomicBoolean rollback2 = new AtomicBoolean(false);

        try {

            // If using Spring transactions, we don't need the c1 reference
            dsl.transaction(c1 -> {

                // The first insertion will work
                dsl.insertInto(BOOK).set(BOOK.ID, 5).set(BOOK.AUTHOR_ID, 1).set(BOOK.TITLE, "Book 5").execute();

                assertEquals(5, dsl.fetchCount(BOOK));

                try {

                    // Nest transactions using Spring. This should create a savepoint, right here
                    // If using Spring transactions, we don't need the c2 reference
                    dsl.transaction(c2 -> {

                        // The second insertion shouldn't work
                        for (int i = 0; i < 2; i++)
                            dsl.insertInto(BOOK).set(BOOK.ID, 6).set(BOOK.AUTHOR_ID, 1).set(BOOK.TITLE, "Book 6").execute();

                        fail();
                    });
                } catch (DataAccessException e) {
                    rollback1.set(true);
                }

                // We should've rolled back to the savepoint
                assertEquals(5, dsl.fetchCount(BOOK));

                throw new org.jooq.exception.DataAccessException("Rollback");
            });
        }

        // Upon the constraint violation, the transaction must already have been rolled back
        catch (org.jooq.exception.DataAccessException e) {
            assertEquals("Rollback", e.getMessage());
            rollback2.set(true);
        }

        assertEquals(4, dsl.fetchCount(BOOK));
        assertTrue(rollback1.get());
        assertTrue(rollback2.get());
    }
}
