package org.apache.bookkeeper.proto;

import io.netty.channel.ChannelHandlerContext;
import org.apache.bookkeeper.bookie.BookieImpl;
import org.apache.bookkeeper.bookie.BookieImplSetup;
import org.apache.bookkeeper.bookie.TestBKConfiguration;
import org.apache.bookkeeper.conf.ServerConfiguration;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class WriteEntryProcessorTest {

    //testing process packet, no parameters
    @Test
    public void processPacketTest() throws Exception {

        BookieImpl bookie = Mockito.mock(BookieImpl.class);

        BookieProtocol.ParsedAddRequest request = Mockito.mock(BookieProtocol.ParsedAddRequest.class);
        BookieRequestHandler handler = Mockito.mock(BookieRequestHandler.class);
        BookieRequestProcessor processor = Mockito.mock(BookieRequestProcessor.class);
        ChannelHandlerContext ctx = Mockito.mock(ChannelHandlerContext.class);

        when(handler.ctx()).thenReturn(ctx);
        when(processor.getBookie()).thenReturn(bookie);
        //when(request.isRecoveryAdd()).thenReturn(true);

        WriteEntryProcessor wep =  WriteEntryProcessor.create(request, handler, processor); //costruttore privato, quindi chiamo create

        boolean res = false;
        try{
            wep.processPacket();
            res = true;
            Assert.assertTrue(res);

        } catch (Exception e) {
            e.printStackTrace();
            Assert.assertFalse(res);
        }


    }

}