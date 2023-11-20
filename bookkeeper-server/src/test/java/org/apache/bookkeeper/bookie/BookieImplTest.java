package org.apache.bookkeeper.bookie;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;
import static org.powermock.api.mockito.PowerMockito.mock;

public class BookieImplTest {

    public static class checkDirectoryValid {
        File mockFile;
        //File mockParent;

        @Before
        public void setup() {

            mockFile = mock(File.class);

            //mockParent = mock(File.class);

            Mockito.doReturn(true).when(mockFile).exists();

            //Mockito.doReturn(mockParent).when(mockFile).getParentFile();
            //Mockito.doReturn(false).when(mockFile).mkdirs();

        }

        @Test
        public void checkDirectoryStructure() {
            boolean actual = true;
            try {
                BookieImpl.checkDirectoryStructure(mockFile);
            } catch (IOException e) {
                actual = false;
            }
            Assert.assertTrue(actual);
        }

    }


    public static class checkDirectoryNull {
        File mockFile;
        //File mockParent;

        @Before
        public void setup() {

            mockFile = null;

        }

        @Test
        public void checkDirectoryStructure() {
            boolean actual = true;
            try {
                BookieImpl.checkDirectoryStructure(mockFile);
            } catch (NullPointerException | IOException e) {
                actual = false;
            }
            Assert.assertFalse(actual);
        }

    }

    public static class checkDirectoryInvalid {
        File mockFile;
        File mockParent;

        @Before
        public void setup() {

            mockFile = mock(File.class);

            mockParent = mock(File.class);

            Mockito.doReturn(false).when(mockFile).exists();
            Mockito.doReturn(mockParent).when(mockFile).getParentFile();
            Mockito.doReturn(false).when(mockFile).mkdirs();

        }

        @Test
        public void checkDirectoryStructure() {
            boolean actual = true;
            try {
                BookieImpl.checkDirectoryStructure(mockFile);
            } catch (IOException e) {
                actual = false;
            }
            Assert.assertFalse(actual);
        }

    }



    @Test
    public void getBookieId() {
    }

    @Test
    public void getBookieAddress() {
    }

    @Test
    public void getLedgerDirsManager() {
    }
}