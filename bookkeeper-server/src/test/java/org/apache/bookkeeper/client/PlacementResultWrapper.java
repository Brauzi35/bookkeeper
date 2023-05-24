package org.apache.bookkeeper.client;

import java.util.Collections;

public class PlacementResultWrapper {

        private EnsemblePlacementPolicy.PlacementResult placementResult;
        private DefaultEnsemblePlacementPolicy defaultEnsemblePlacementPolicy = new DefaultEnsemblePlacementPolicy();

        public PlacementResultWrapper() throws BKException.BKNotEnoughBookiesException {
            placementResult = defaultEnsemblePlacementPolicy.newEnsemble(4,4,4, Collections.emptyMap(), Collections.emptySet());
        }

    public EnsemblePlacementPolicy.PlacementResult getPlacementResult() {
        return placementResult;
    }

    public DefaultEnsemblePlacementPolicy getDefaultEnsemblePlacementPolicy() {
        return defaultEnsemblePlacementPolicy;
    }
}
