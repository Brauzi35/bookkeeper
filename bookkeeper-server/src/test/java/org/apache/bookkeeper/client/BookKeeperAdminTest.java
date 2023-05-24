package org.apache.bookkeeper.client;

import org.apache.bookkeeper.net.BookieId;
import org.apache.bookkeeper.util.ZkUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)

class BookKeeperAdminTest {


    @Mock
    private BookKeeperAdmin bookKeeperAdminMock;
    @Mock
    private BookKeeper bookKeeper;
    @Mock
    private DefaultEnsemblePlacementPolicy defaultEnsemblePlacementPolicy;



    @Before
    public void setUp() throws BKException.BKNotEnoughBookiesException {
        MockitoAnnotations.initMocks(this);

        when(bookKeeper.getPlacementPolicy().replaceToAdherePlacementPolicy(anyInt(), anyInt(), anyInt(), any(HashSet.class), any(List.class)))
                .thenReturn(defaultEnsemblePlacementPolicy.newEnsemble(1,1,1, new HashMap<>(), new HashSet<>()));
    }










    @Test
    void replaceNotAdheringPlacementPolicyBookieTest() throws BKException, IOException, InterruptedException, NoSuchFieldException, IllegalAccessException {

        Field bk = BookKeeperAdmin.class.getDeclaredField("bkc");
        bk.setAccessible(true);
        bk.set(bookKeeperAdminMock, bookKeeper);


        List<BookieId> prova = new ArrayList<>();
        BookieId bookieId = BookieId.parse("prova");
        prova.add(bookieId);

        bookKeeperAdminMock.replaceNotAdheringPlacementPolicyBookie(prova,3, 2);

    }
}