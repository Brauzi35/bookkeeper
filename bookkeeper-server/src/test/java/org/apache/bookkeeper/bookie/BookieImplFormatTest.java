package org.apache.bookkeeper.bookie;

import org.apache.bookkeeper.conf.ServerConfiguration;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.mockito.Mockito;

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


    private static ServerConfiguration confBuilder(boolean isValid){
        ServerConfiguration ret = TestBKConfiguration.newServerConfiguration();
        if(!isValid){
            ServerConfiguration spy = spy(ret);
            Mockito.when(spy.getJournalDirs()).thenReturn(new File[]{});
            ret = spy;
        }
        return ret;
    }
    public BookieImplFormatTest(ServerConfiguration conf, boolean isInteractive, boolean force) {
        this.conf = conf;
        this.isInteractive = isInteractive;
        this.force = force;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getParameters() {
        return Arrays.asList(new Object[][]{
                {confBuilder(true), false, true},
                {confBuilder(true), true, true},
                {confBuilder(true), false, false},
                {confBuilder(false), false, true}
        });
    }

    @Test()
    public void testFormat(){

        for(File f : this.conf.getJournalDirs()){
            System.out.println("Test n "+f);
        }
        boolean res = BookieImpl.format(this.conf, this.isInteractive, this.force);
        Assert.assertTrue(res);

    }

}