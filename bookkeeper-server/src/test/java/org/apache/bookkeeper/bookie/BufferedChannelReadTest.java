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

import static org.apache.bookkeeper.bookie.BufferedChannelUtil.*;

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


                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.VALID), -1, -1, false},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.VALID), -1, 0, false},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.VALID), -1, 1, true},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.VALID), 0, -1, false},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.VALID), 0, 0, false},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.VALID), 0, 1, false}, //5
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.VALID), 1, -1, false},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.VALID), 1, 0, false},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.VALID), 1, 1, true},

                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.INVALID), -1, -1, false}, //9
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.INVALID), -1, 0, false},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.INVALID), -1, 1, true},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.INVALID), 0, -1, false},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.INVALID), 0, 0, false},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.INVALID), 0, 1, true}, //14
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.INVALID), 1, -1, false},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.INVALID), 1, 0, false},
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.INVALID), 1, 1, true},





                //v2
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.VALID), 0, 1025, true}, //read buffer start position <= pos

                /*
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 1024, 0, destBuilder(BufferedChannelUtil.Objects.VALID), 0, 1025, true}, //read buffer start position <= pos
                {bbAllocator(BufferedChannelUtil.Objects.VALID),new RandomAccessFile(createTempFile(), "rw").getChannel(), 0, 0, destBuilder(BufferedChannelUtil.Objects.VALID), 50, 1024, true}, //read buffer start position <= pos


                 */

        });
    }

    @Test
    public void testRead() {

        int lenW = 1;
        if(this.lenght >= 0){
            lenW = this.lenght;
        }

        ByteBuf writeBuf = Unpooled.buffer(lenW, lenW);
        byte [] data = new byte[lenW];
        Random random = new Random();
        random.nextBytes(data);
        writeBuf.writeBytes(data);

        int neg = 0; //apparently, if lenght is -1 read returns 0, so we need to check that
        try {
            this.bufferedChannel.write(writeBuf);
        } catch (IOException e) {
            //todo: specialize this catch
            throw new RuntimeException(e);
        }
        try {
            int res = this.bufferedChannel.read(this.dest, this.pos, this.lenght);
            System.out.println(res);
            if(this.excExp){
                //if i was expecting an exception but i didn't get one, the test shoiuld fail
                Assert.fail("was expecting an exception but didn't get one");
            }
            if(this.lenght<0){
                Assert.assertEquals(neg, res);
            }else{ Assert.assertEquals(this.lenght, res); }

        } catch (IOException | IllegalArgumentException  e) {
            if(this.excExp){
                e.printStackTrace();
                Assert.assertTrue(this.excExp); //always true, the expected behaviour has been verified
            }else{
                Assert.fail("test failed");
            }
        }

    }
}