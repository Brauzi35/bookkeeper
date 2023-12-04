package org.apache.bookkeeper.bookie;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.apache.bookkeeper.client.api.BKException;
import org.apache.bookkeeper.proto.BookkeeperInternalCallbacks;
import org.junit.Assert;
import org.mockito.Mockito;
import org.apache.bookkeeper.proto.BookkeeperInternalCallbacks.WriteCallback;


import java.util.concurrent.atomic.AtomicBoolean;

public class BookieImplUtils {

    enum Objects{
        VALID,
        INVALID
    }

    private static AtomicBoolean flag = new AtomicBoolean(false);
    public ByteBuf buildEntry(Objects ob, long ledger, long entry) {
        switch (ob) {
            case VALID:
                byte[] data = ("ledger-" + ledger + "-" + entry).getBytes(); //the last 1 is for entry
                ByteBuf bb = Unpooled.buffer(8 + 8 + data.length);

                bb.writeLong(ledger);
                bb.writeLong(entry); //again this is entry
                bb.writeBytes(data);
                return bb;
            case INVALID:
                ByteBuf ibb = Mockito.mock(ByteBuf.class);
                //ByteBuf exists to collect bytes, so I want to throw an exception anytime those bytes are accessed
                Mockito.doThrow(new RuntimeException("invalid ByteBuf")).when(ibb).getByte(Mockito.anyInt());
                return ibb;
            default:
                Assert.fail("something went wrong in buildEntry");
                return null;
        }
    }

    public WriteCallback getCallback(Objects ob) {

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


}
