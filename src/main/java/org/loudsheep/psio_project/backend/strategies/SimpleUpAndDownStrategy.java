package org.loudsheep.psio_project.backend.strategies;

import org.loudsheep.psio_project.backend.models.StockData;

public class SimpleUpAndDownStrategy extends Strategy {
    public SimpleUpAndDownStrategy(double budget) {
        super(budget);
        this.STRATEGY_DESCRIPTION = "Simple strategy that sells when downward trend, and buys when upward";
    }

    @Override
    public void execute(StockData data) {

    }
}
