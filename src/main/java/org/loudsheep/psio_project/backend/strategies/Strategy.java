package org.loudsheep.psio_project.backend.strategies;

import org.loudsheep.psio_project.backend.models.StockData;

public abstract class Strategy {
    protected String STRATEGY_DESCRIPTION = "Strategy description";
    protected double budget;

    public  Strategy(double budget) {
        this.budget = budget;
    }

    public String getDescription() {
        return this.STRATEGY_DESCRIPTION;
    }

    public abstract void execute(StockData data);
}
