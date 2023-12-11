package org.apache.bookkeeper.bookie;

import org.apache.bookkeeper.conf.ServerConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(Parameterized.class)
public class BookieImplFormatTest {

    private ServerConfiguration conf;
    private boolean isInteractive;
    private boolean force;

    private boolean expRes;


    private static ServerConfiguration confBuilder(boolean isValid){
        ServerConfiguration ret = TestBKConfiguration.newServerConfiguration();
        if(!isValid){
            ServerConfiguration spy = spy(ret);
            Mockito.when(spy.getJournalDirs()).thenReturn(null); //new File[]{}
            ret = spy;
        }
        return ret;
    }
    public BookieImplFormatTest(ServerConfiguration conf, boolean isInteractive, boolean force, boolean expRes) {
        this.conf = conf;
        this.isInteractive = isInteractive;
        this.force = force;
        this.expRes = expRes;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                //conf, isInteractive, force, expRes
                //{confBuilder(true), true, true, true},
                //{confBuilder(true), true, false, true},
                {confBuilder(true), false, false, true}, //true
                {confBuilder(true), false, true, true},

                //{confBuilder(false), true, true, false},
                //{confBuilder(false), true, false, false},
                {confBuilder(false), false, false, false},
                {confBuilder(false), false, true, false}


        });
    }

    @Test()
    public void testFormat(){

        //System.setIn(new ByteArrayInputStream("y/n".getBytes(),0,2));

        boolean res; //init res
        try {
            res = BookieImpl.format(this.conf, this.isInteractive, this.force);
        } catch (NullPointerException e){
            res = false; //failure!
        }

        //Assert.assertTrue(res);
        Assert.assertEquals(this.expRes, res);

    }

}