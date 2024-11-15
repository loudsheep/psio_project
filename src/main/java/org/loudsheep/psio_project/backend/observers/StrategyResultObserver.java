package org.loudsheep.psio_project.backend.observers;

import org.loudsheep.psio_project.backend.models.StrategyResult;
import org.loudsheep.psio_project.backend.models.Transaction;

public interface StrategyResultObserver {
    void onStrategyResultUpdate(StrategyResult result);
    void onTransactionAdd(Transaction transaction);
}
