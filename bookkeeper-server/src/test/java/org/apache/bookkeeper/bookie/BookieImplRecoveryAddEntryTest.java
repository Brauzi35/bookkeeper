package org.apache.bookkeeper.bookie;

import com.beust.ah.A;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.bookkeeper.client.api.BKException;
import org.apache.bookkeeper.proto.BookkeeperInternalCallbacks.WriteCallback;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.apache.bookkeeper.proto.BookkeeperInternalCallbacks;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class BookieImplRecoveryAddEntryTest {

    enum Objects{
        VALID,
        INVALID,

    }

    private static BookieImpl bookieimpl;
    private static AtomicBoolean flag = new AtomicBoolean(false);

    private ByteBuf bb;
    private WriteCallback cb;
    private Object ctx;
    private byte[] mk;
    private boolean expExc;


    @Parameters
    public static Collection<Object[]> getParameters() {
        // Creazione dell'oggetto ByteBuf con le stesse proprietà
        return Arrays.asList(new Object[][]{
                //entry, cb, ctx, masterKey, expExeption

                {buildEntry(Objects.VALID, 1), getCallback(Objects.VALID), "ctx", "key".getBytes(), false},
                {buildEntry(Objects.INVALID, 1), getCallback(Objects.VALID), "ctx", "key".getBytes(), true},
                {buildEntry(Objects.VALID, 1), getCallback(Objects.INVALID), "ctx", "key".getBytes(), true},
                {buildEntry(Objects.VALID, 1), getCallback(Objects.VALID), null, "key".getBytes(), true},
                {buildEntry(Objects.VALID, 1), getCallback(Objects.VALID), "ctx", "".getBytes(), true}, //given key does not match expected key
                {buildEntry(Objects.VALID, 1), getCallback(Objects.VALID), "ctx", null, true}



        });
    }

    private static WriteCallback getCallback(Objects ob) {

        switch (ob){
            case VALID:
                return (rc, ledgerId, entryId, addr, ctx) -> flag.set(rc == BKException.Code.OK);
            case INVALID:
                return (rc, ledgerId, entryId, addr, ctx) -> {
                    throw new RuntimeException("Invalid callback");
                };
            default:
                Assert.fail("something went wrong in getCallBack");
                return null;
        }

    }


    private static ByteBuf buildEntry(Objects ob, long ledger) {
        switch (ob) {
            case VALID:
                byte[] data = ("ledger-" + ledger + "-" + 1).getBytes();
                ByteBuf bb = Unpooled.buffer(8 + 8 + data.length);
                bb.writeLong(ledger);
                bb.writeLong(1);
                bb.writeBytes(data);
                return bb;
            case INVALID:
                ByteBuf ibb = Mockito.mock(ByteBuf.class);
                //ByteBuf exixts to collect bytes, so i want to throw an exception anytime those bytes are accessed
                Mockito.doThrow(new RuntimeException("invalid ByteBuf")).when(ibb).getByte(Mockito.anyInt());
                return ibb;
            default:
                Assert.fail("something went wrong in buildEntry");
                return null;
        }
    }

    //todo: mettere before e non chiamarlo nel costruttore
    private static void init() {
        //create bookieImpl using existing setup classes from bookkeeper apache
        try {
            ServerConfiguration conf = TestBKConfiguration.newServerConfiguration();
            bookieimpl = new BookieImplSetup(conf);
            bookieimpl.start();
        } catch (Exception e) {
            Assert.fail("undesired exception in setup operations, something went wrong");
            e.printStackTrace();
        }
    }

    public BookieImplRecoveryAddEntryTest(ByteBuf bb, WriteCallback cb, Object ctx, byte[] mk, boolean expExc){
        init();

        this.bb = bb;
        this.cb = cb;
        this.ctx = ctx;
        this.mk = mk;
        this.expExc = expExc;

    }




    @Test()
    public void testRecoveryAddEntry(){

        boolean isCorrect = true;

        if (!this.expExc){ //exception expected
            try {
                bookieimpl.recoveryAddEntry(this.bb, this.cb, this.ctx, this.mk);
            } catch (IOException | InterruptedException | BookieException e) {
                if(!expExc){
                    //if I expect no exception to be raised, but I've caught one, there is an
                    //underside behaviour -> the test should fail
                    isCorrect = false;
                }

            }
            Assert.assertTrue(isCorrect);

        } else{
            //todo test sbagliato! non controllo mai se non mi aspetto un'eccezione
        }

    }

}