package org.apache.bookkeeper.client;

import org.apache.bookkeeper.net.BookieId;
import org.apache.bookkeeper.util.ZkUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ReflectionUtils;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

class BookKeeperAdminTest {

    /*
    @Mock
    BookieIdWrapper bookieId = Mockito.mock(BookieIdWrapper.class);

    @BeforeClass
    public void setUp(){
        when(bookieId.getStringBookieId()).thenReturn("ciaoooooooooooo");
    }




    @Test
    void formatEnsembleTest(){
        //when(bookieId.getStringBookieId()).thenReturn("ciaoooooooooooo");
        String str = bookieId.getStringBookieId();
        System.out.println(str);

    }

     */

    enum PlacementPolicyAdherence {
        FAIL(1), MEETS_SOFT(3), MEETS_STRICT(5);
        private int numVal;

        private PlacementPolicyAdherence(int numVal) {
            this.numVal = numVal;
        }

        public int getNumVal() {
            return numVal;
        }
    }


    @Mock
    BookKeeper mockBkc = Mockito.mock(BookKeeper.class);

    @Mock
    EnsemblePlacementPolicy mockPlacementPolicy = Mockito.mock(EnsemblePlacementPolicy.class);




    @Before
    public void setUp() throws BKException.BKNotEnoughBookiesException {
        MockitoAnnotations.initMocks(this);

        //BookKeeperAdminTest myClass = new BookKeeperAdminTest(mockBkc, mockPlacementPolicy);

        // Configurazione del comportamento dei mock degli oggetti dipendenti
        List<BookieId> ensembleBookiesList = new ArrayList<>();
        ensembleBookiesList.add(BookieId.parse("1"));
        ensembleBookiesList.add(BookieId.parse("2"));
        ensembleBookiesList.add(BookieId.parse("3"));

        List<BookieId> newEnsembles = new ArrayList<>();
        newEnsembles.add(BookieId.parse("4"));
        newEnsembles.add(BookieId.parse("5"));
        newEnsembles.add(BookieId.parse("6"));



        EnsemblePlacementPolicy.PlacementResult<List<BookieId>> placementResult = Mockito.mock(
                EnsemblePlacementPolicy.PlacementResult.class);



        Mockito.when(placementResult.getAdheringToPolicy()).thenReturn(EnsemblePlacementPolicy.PlacementPolicyAdherence.MEETS_SOFT);
        Mockito.when(placementResult.getResult()).thenReturn(newEnsembles);

        Mockito.when(mockBkc.getPlacementPolicy()).thenReturn(mockPlacementPolicy);
        Mockito.when(mockPlacementPolicy.replaceToAdherePlacementPolicy(
                        ensembleBookiesList.size(), anyInt(), anyInt(), new HashSet<>(), ensembleBookiesList))
                .thenReturn(placementResult);

    }










    @Test
    void replaceNotAdheringPlacementPolicyBookieTest() throws BKException, IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {

        // Configurazione del comportamento dei mock degli oggetti dipendenti
        List<BookieId> ensembleBookiesList = new ArrayList<>();
        ensembleBookiesList.add(BookieId.parse("1"));
        ensembleBookiesList.add(BookieId.parse("2"));
        ensembleBookiesList.add(BookieId.parse("3"));

        List<BookieId> newEnsembles = new ArrayList<>();
        newEnsembles.add(BookieId.parse("4"));
        newEnsembles.add(BookieId.parse("5"));
        newEnsembles.add(BookieId.parse("6"));
        PlacementResultWrapper placementResultWrapper = new PlacementResultWrapper();


        EnsemblePlacementPolicy.PlacementResult<List<BookieId>> placementResult = Mockito.mock(
        placementResultWrapper.getPlacementResult().getClass());







        Mockito.when(placementResult.getAdheringToPolicy()).thenReturn(EnsemblePlacementPolicy.PlacementPolicyAdherence.MEETS_SOFT);
        Mockito.when(placementResult.getResult()).thenReturn(newEnsembles);

        Mockito.when(mockBkc.getPlacementPolicy()).thenReturn(mockPlacementPolicy);
        Mockito.when(mockPlacementPolicy.replaceToAdherePlacementPolicy(
                        ensembleBookiesList.size(), anyInt(), anyInt(), new HashSet<>(), ensembleBookiesList))
                .thenReturn(placementResult);

        BookKeeperAdmin bookKeeperAdmin = new BookKeeperAdmin(mockBkc);



        Map<Integer, BookieId> result = bookKeeperAdmin.replaceNotAdheringPlacementPolicyBookie(ensembleBookiesList,
                2, 1);


        Map<Integer, BookieId> expectedResult = new HashMap<>();
        expectedResult.put(0, BookieId.parse("4"));
        expectedResult.put(1, BookieId.parse("5"));
        expectedResult.put(2, BookieId.parse("6"));

        Assert.assertEquals(expectedResult, result);

    }



}