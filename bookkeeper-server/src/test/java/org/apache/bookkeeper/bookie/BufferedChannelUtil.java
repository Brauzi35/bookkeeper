package org.apache.bookkeeper.bookie;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import org.junit.Assert;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class BufferedChannelUtil {

    private ByteBufAllocator allocator;
    private FileChannel fc;
    private int writeCapacity;
    private long unpersistedBytesBound;

    enum Objects{
        VALID,
        INVALID,
        NOTWRITABLE

    }

    /** called by BufferedChannelReadTest methods*/
    public static ByteBuf destBuilder(Objects object){
        switch (object){
            case VALID:
                return Unpooled.buffer(1024, 1024);
            case INVALID:
                return Unpooled.buffer(0, 0); //for now we return a non writable buffer
            case NOTWRITABLE:
                //v2
                ByteBuf bbs = Unpooled.buffer(1024, 1024);

            default:
                return null;
        }
    }
    public static File createTempFile() {
        File log = null;
        try {
            log = File.createTempFile("file", "log");
        } catch (IOException e) {
            Assert.fail("undesired exception in tempFile creation, something went wrong");
            e.printStackTrace();
        }
        log.deleteOnExit();
        return log;
    }

    public static ByteBufAllocator bbAllocator(Objects objects){
        switch (objects){
            case VALID:
                return UnpooledByteBufAllocator.DEFAULT;
            case INVALID:

                UnpooledByteBufAllocator mockUBBA = Mockito.mock(UnpooledByteBufAllocator.class);
                Mockito.doReturn(null).when(mockUBBA).directBuffer(Mockito.anyInt());
                return mockUBBA; //in realtà non è proprio invalid...
            default:
                return null;
        }
    }

}
