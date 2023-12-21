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
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.Collection;

import static org.apache.bookkeeper.bookie.BufferedChannelUtil.createTempFile;
import static org.junit.Assert.*;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.*;

@RunWith(Parameterized.class)
public class BookieImplFormatTest {

    private ServerConfiguration conf;
    private boolean isInteractive;
    private boolean force;

    private boolean expRes;

    enum Objects{
        VALID,
        INVALID,
        NONULL,
        CLEANDIR_JDIR,
        CLEANDIR_DIR,
        METADIR,


    }


    private static ServerConfiguration confBuilder(Objects ob){
        ServerConfiguration ret = TestBKConfiguration.newServerConfiguration();

        switch (ob){
            //todo: remove dup code
            case INVALID:
                ServerConfiguration spy = spy(ret);
                Mockito.when(spy.getJournalDirs()).thenReturn(null); //new File[]{}
                ret = spy;
                return ret;
            case NONULL:
                ServerConfiguration spy2 = spy(ret);
                File[] f_list = new File[1];
                File f = Mockito.mock(File.class);
                Mockito.when(f.exists()).thenReturn(true);
                Mockito.when(f.isDirectory()).thenReturn(true);
                Mockito.when(f.mkdirs()).thenReturn(true);
                String[] s_list = new String[2];
                s_list[0] = "test1";
                s_list[1] = "test2";
                Mockito.when(f.list()).thenReturn(s_list);
                f_list[0] = f;

                Mockito.when(spy2.getJournalDirs()).thenReturn(f_list);
                ret = spy2;
                return ret;

            case CLEANDIR_JDIR:

                ServerConfiguration spy3 = spy(ret);
                File[] f_list3 = new File[1];
                File f3 = Mockito.mock(File.class);
                Mockito.when(f3.exists()).thenReturn(false); //false
                Mockito.when(f3.isDirectory()).thenReturn(true);
                Mockito.when(f3.mkdirs()).thenReturn(false); //false
                String[] s_list3 = new String[2];
                s_list3[0] = "test1";
                s_list3[1] = "test2";
                Mockito.when(f3.list()).thenReturn(s_list3);
                f_list3[0] = f3;

                Mockito.when(spy3.getJournalDirs()).thenReturn(f_list3);
                ret = spy3;
                return ret;
            case CLEANDIR_DIR:

                ServerConfiguration spy4 = spy(ret);
                File[] f_list4 = new File[1];
                File f4 = Mockito.mock(File.class);
                Mockito.when(f4.exists()).thenReturn(false); //false
                Mockito.when(f4.isDirectory()).thenReturn(true);
                Mockito.when(f4.mkdirs()).thenReturn(false); //false
                String[] s_list4 = new String[2];
                s_list4[0] = "test1";
                s_list4[1] = "test2";
                Mockito.when(f4.list()).thenReturn(s_list4);
                f_list4[0] = f4;

                Mockito.when(spy4.getIndexDirs()).thenReturn(f_list4);
                ret = spy4;
                return ret;
            case METADIR:

                ServerConfiguration spy5 = spy(ret);


                Mockito.when(spy5.getGcEntryLogMetadataCachePath()).thenReturn("test1");
                ret = spy5;
                return ret;

            default:
                return ret;




        }

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
                {confBuilder(Objects.VALID), false, false, true}, //true
                {confBuilder(Objects.VALID), false, true, true},

                //{confBuilder(false), true, true, false},
                //{confBuilder(false), true, false, false},
                {confBuilder(Objects.INVALID), false, false, false},
                {confBuilder(Objects.INVALID), false, true, false},


                //v2 test
                {confBuilder(Objects.NONULL), false, false, false},
                {confBuilder(Objects.NONULL), false, true, true},
                {confBuilder(Objects.CLEANDIR_JDIR), false, true, false},
                {confBuilder(Objects.CLEANDIR_DIR), false, true, false},
                {confBuilder(Objects.METADIR), false, true, true},
        });
    }

    @Test()
    public void testFormat(){

        System.setIn(new ByteArrayInputStream("y/n".getBytes(),0,2));

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