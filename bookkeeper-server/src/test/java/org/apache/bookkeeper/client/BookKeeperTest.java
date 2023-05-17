package org.apache.bookkeeper.client;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookKeeperTest {

    @Mock
    BookKeeper bookKeeper;
    LedgerHandle ledgerHandle;

    @Before
    public void createMocksAsReturnType() throws BKException, InterruptedException {
        when(bookKeeper.createLedger(anyInt(), anyInt(), anyInt(), any(), any())).thenReturn(ledgerHandle);

    }



    @Test
    public void createLedger() throws BKException, InterruptedException, IOException {
        boolean exc = false;
        try {
            LedgerHandle lh = bookKeeper.createLedger(2, 3, 4, BookKeeper.DigestType.CRC32C, "password".getBytes());
        } catch (BKException bkException){
            exc = true;
            System.err.println("BKException raised");
        } catch (InterruptedException interruptedException){
            exc = true;
            System.err.println("InterruptedException raised");
        } catch (IllegalArgumentException illegalArgumentException) {
            exc = true;
            System.err.println("IllegalArgumentException raised " + illegalArgumentException.getClass());
        }finally {
            assertFalse(exc);
        }

    }


    @Test
    public void createLedger2() throws BKException, InterruptedException, IOException {
        boolean exc = false;
        try {
            BookKeeper bk = new BookKeeper(); // mock skip
            LedgerHandle lh = bk.createLedger(2, 3, 4, BookKeeper.DigestType.CRC32C, "password".getBytes());
        } catch (BKException bkException){
            exc = true;
            System.err.println("BKException raised");
        } catch (InterruptedException interruptedException){
            exc = true;
            System.err.println("InterruptedException raised");
        } catch (IllegalArgumentException illegalArgumentException) {
            exc = true;
            System.err.println("IllegalArgumentException raised " + illegalArgumentException.getClass());
        }finally {
            assertTrue(exc);
        }

    }
}