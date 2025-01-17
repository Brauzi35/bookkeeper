package org.apache.bookkeeper.bookie;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class BufferedChannelWriteTest {

    private ByteBufAllocator allocator;
    private FileChannel fc;
    private int writeCapacity;
    private long unpersistedBytesBound;
    private BufferedChannel bufferedChannel;
    private ByteBuf src;
    private boolean expExc;
    private static int len = 10;


    enum Objects{
        VALID,
        INVALID,

    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() throws FileNotFoundException {
        // Creazione dell'oggetto ByteBuf con le stesse proprietà
        return Arrays.asList(new Object[][]{
                //ByteBufAllocator allocator, FileChannel fc, int writeCapacity, long unpersistedBytesBound, ByteBuf src, boolean expExc

                {BufferedChannelUtil.bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(BufferedChannelUtil.createTempFile(), "rw").getChannel(), 1024, 0, buildSrc(Objects.VALID, "1234567890"), false},
                {BufferedChannelUtil.bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(BufferedChannelUtil.createTempFile(), "rw").getChannel(), 1024, 0, buildSrc(Objects.INVALID, "1234567890"), true},

        });
    }
    private static ByteBuf buildSrc(Objects object, String string){

        switch (object){
            case VALID:
                ByteBuf ret = Unpooled.buffer(1024, 1024);
                byte [] data = string.getBytes();
                ret.writeBytes(data);
                return ret;
            case INVALID:
                //is invalid respect to write operation
                //while is executed but with a negative reader index
                ByteBuf mockbb = Mockito.mock(ByteBuf.class);
                Mockito.doReturn(-1).when(mockbb).readerIndex();
                Mockito.doReturn(10).when(mockbb).readableBytes();
                return mockbb;
            default:
                return null;
        }
    }
    public BufferedChannelWriteTest(ByteBufAllocator allocator, FileChannel fc, int writeCapacity, long unpersistedBytesBound, ByteBuf src, boolean expExc) {
        this.allocator = allocator;
        this.fc = fc;
        this.writeCapacity = writeCapacity;
        this.unpersistedBytesBound = unpersistedBytesBound;
        try {
            this.bufferedChannel = new BufferedChannel(allocator, fc, writeCapacity, unpersistedBytesBound);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.src = src;//buildSrc(Objects.VALID,"1234567890");
        this.expExc = expExc;
    }



    @Test
    public void testWrite() {
        try {
            this.bufferedChannel.write(this.src);
            ByteBuf dest = Unpooled.buffer(len);
            this.bufferedChannel.read(dest, 0);
            //System.out.println(dest.readCharSequence(10, Charset.forName("utf-8")));
            Assert.assertEquals(dest.readCharSequence(len, Charset.forName("utf-8")), src.readCharSequence(10, Charset.forName("utf-8")));
        } catch (IOException | IndexOutOfBoundsException | IllegalArgumentException e) {
            if(!this.expExc) {
                Assert.fail("exception not expected");
            }
        }

    }
}