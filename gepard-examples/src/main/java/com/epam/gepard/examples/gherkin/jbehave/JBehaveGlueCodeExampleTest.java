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

import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Named;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;

import com.epam.gepard.gherkin.jbehave.JBehaveTestCase;

import static org.junit.Assert.assertEquals;

/**
 * Glue code for the example story files.
 * @author Adam_Csaba_Kiraly
 */
public abstract class JBehaveGlueCodeExampleTest extends JBehaveTestCase {

    private Stock stock;

    @Given("a stock of $symbol and a $threshold")
    public void aStock(@Named("symbol") final String symbol, @Named("threshold") final double threshold) {
        stock = new Stock(symbol, threshold);
    }

    @When("the stock is traded at $price")
    public void theStockTradeAt(@Named("price") final double price) {
        stock.setPrice(price);
    }

    @Then("the alert status should be $status")
    public void theAlertStatusShouldBe(@Named("status") final String status) {
        assertEquals(status, stock.alertStatus());
    }
}
