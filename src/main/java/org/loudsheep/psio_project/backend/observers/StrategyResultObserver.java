package org.loudsheep.psio_project.backend.observers;

import org.loudsheep.psio_project.backend.strategies.StrategyResult;

public interface StrategyResultObserver {
    void onStrategyResult(StrategyResult result);
}
