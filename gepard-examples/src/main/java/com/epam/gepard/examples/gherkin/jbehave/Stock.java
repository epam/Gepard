package com.epam.gepard.examples.gherkin.jbehave;

/*==========================================================================
Copyright 2004-2015 EPAM Systems

This file is part of Gepard.

Gepard is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gepard is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gepard.  If not, see <http://www.gnu.org/licenses/>.
===========================================================================*/

/**
 * Simple object for the test code.
 * @author Adam_Csaba_Kiraly
 */
public class Stock {

    private final String symbol;
    private final double threshold;
    private double price;

    /**
     * Create a new {@link Stock} instance.
     * @param symbol the symbol of the stock
     * @param threshold the price threshold of the stock
     */
    public Stock(final String symbol, final double threshold) {
        this.symbol = symbol;
        this.threshold = threshold;
    }

    public double getThreshold() {
        return threshold;
    }

    public String getSymbol() {
        return symbol;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }

    /**
     * Returns the alert status.
     * @return ON if price is above threshold else OFF
     */
    public String alertStatus() {
        return price > threshold ? "ON" : "OFF";
    }

}
