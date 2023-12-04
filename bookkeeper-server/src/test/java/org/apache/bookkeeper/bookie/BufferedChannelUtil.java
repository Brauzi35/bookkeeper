package org.apache.bookkeeper.bookie;

import io.netty.buffer.ByteBufAllocator;
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
