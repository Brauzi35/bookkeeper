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

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.*;
@RunWith(Parameterized.class)
public class BookieImplRecoveryAddEntryTest {


    private static BookieImpl bookieimpl;
    private static AtomicBoolean flag = new AtomicBoolean(false);


    @Parameters
    public static Collection<Object[]> getParameters() {
        // Creazione dell'oggetto ByteBuf con le stesse proprietà
        return Arrays.asList(new Object[][]{


        });
    }

    private static WriteCallback getCallback() {

        return (rc, ledgerId, entryId, addr, ctx) -> flag.set(rc == BKException.Code.OK);
    }


    private static ByteBuf buildEntry(long ledger) {
        byte[] data = ("ledger-" + ledger + "-" + 1).getBytes();
        ByteBuf bb = Unpooled.buffer(8 + 8 + data.length);
        bb.writeLong(ledger);
        bb.writeLong(1);
        bb.writeBytes(data);
        return bb;
    }

    public BookieImplRecoveryAddEntryTest() throws Exception {
        //create bookieImpl using existing setup classes from bookkeeper apache
        ServerConfiguration conf = TestBKConfiguration.newServerConfiguration();
        bookieimpl = new BookieImplSetup(conf);
        bookieimpl.start();
        //TO-DO implement try-catch to correctly catch any undesired behaviour
    }

    @Test()
    public void recoveryAddEntry(){

        boolean isCorrect = true;
        try {
            bookieimpl.recoveryAddEntry(buildEntry(1), getCallback(), "test", "ciao".getBytes());
        } catch (IOException e) {
            isCorrect = false;
        } catch (BookieException e) {
            isCorrect = false;
        } catch (InterruptedException e) {
            isCorrect = false;
        }

        Assert.assertTrue(isCorrect);

    }

}