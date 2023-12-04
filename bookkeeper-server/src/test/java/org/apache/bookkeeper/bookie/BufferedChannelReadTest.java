package org.apache.bookkeeper.bookie;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.Random;

@RunWith(Parameterized.class)
public class BufferedChannelReadTest {

    private ByteBufAllocator allocator;
    private FileChannel fc;
    private int writeCapacity;
    private long unpersistedBytesBound;
    private ByteBuf dest;
    private long pos;
    private int lenght;
    private BufferedChannel bufferedChannel;
    private File temp;
    private boolean excExp;


    enum Objects{
        VALID,
        INVALID,

    }

    private static ByteBufAllocator bbAllocator(Objects objects){
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

    private static File createTempFile() {
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

    public BufferedChannelReadTest(ByteBufAllocator allocator, FileChannel fc, int writeCapacity, long unpersistedBytesBound, ByteBuf dest, long pos, int lenght, boolean excExp) {
        this.allocator = allocator;
        this.fc = fc;
        this.writeCapacity = writeCapacity;
        this.unpersistedBytesBound = unpersistedBytesBound;

        try {
            this.bufferedChannel = new BufferedChannel(allocator, fc, writeCapacity, unpersistedBytesBound);
        } catch (IOException e) {
            Assert.fail("undesired exception in buffered channel setup operations, something went wrong");
            e.printStackTrace();
        }

        this.dest = dest;
        this.lenght = lenght;
        this.pos = pos;
        this.excExp = excExp;

    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() throws FileNotFoundException {
        // Creazione dell'oggetto ByteBuf con le stesse proprietà
        return Arrays.asList(new Object[][]{
                //ByteBufAllocator allocator, FileChannel fc, int writeCapacity, long unpersistedBytesBound, ByteBuf dest, long pos, int lenght

                {bbAllocator(Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, Unpooled.buffer(1024, 1024), 0, 124, false},
                {bbAllocator(Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, Unpooled.buffer(1024, 1024), -1, 124, true},
                {bbAllocator(Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, Unpooled.buffer(1024, 1024), 1025, 124, true},

        });
    }

    @Test
    public void testRead() {

        int lenW = 1024;
        if(this.lenght >= 0){
            lenW = this.lenght;
        }
        ByteBuf writeBuf = Unpooled.buffer(lenW, lenW);
        byte [] data = new byte[lenW];
        Random random = new Random();
        random.nextBytes(data);
        writeBuf.writeBytes(data);
        try {
            this.bufferedChannel.write(writeBuf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            int res = this.bufferedChannel.read(this.dest, this.pos, this.lenght);
            System.out.println(res);
            Assert.assertEquals(this.lenght, res);
        } catch (IOException | IllegalArgumentException e) {
            if(this.excExp == true){
                e.printStackTrace();
                Assert.assertTrue(this.excExp); //always true, the expected behaviour has been verified
            }else{
                Assert.fail("test failed");
            }
        }

    }
}