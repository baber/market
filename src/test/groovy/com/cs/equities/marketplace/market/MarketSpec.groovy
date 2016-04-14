package com.cs.equities.marketplace.market

import com.cs.equities.marketplace.data.Submission

import java.time.Instant

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.core.IsCollectionContaining.hasItem
import static org.hamcrest.core.IsEqual.equalTo
import static org.hamcrest.core.IsNull.notNullValue

class MarketSpec extends spock.lang.Specification {

    def "should not be able to add a bid to the market if the quantity is not greater than zero"() {
        given:
        Market market = new Market()
        Submission bidRequest = new Submission("item1", "user1", 0, 5)

        when:
        market.placeBid(bidRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should not be able to add an offer to the market if the quantity is not greater than zero"() {
        given:
        Market market = new Market()
        Submission offerRequest = new Submission("item1", "user1", 0, 5)

        when:
        market.placeOffer(offerRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should not be able to add a bid to the market if the price is less than zero"() {
        given:
        Market market = new Market()
        Submission bidRequest = new Submission("item1", "user1", 5, -1)

        when:
        market.placeBid(bidRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should not be able to add an offer to the market if the price is less than zero"() {
        given:
        Market market = new Market()
        Submission offerRequest = new Submission("item1", "user1", 5, -1)

        when:
        market.placeOffer(offerRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should not be able to add a bid to the market if the user id is empty"() {
        given:
        Market market = new Market()
        Submission bidRequest = new Submission("item1", "", 5, 10)

        when:
        market.placeBid(bidRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should not be able to add an offer to the market if the user id is empty"() {
        given:
        Market market = new Market()
        Submission offerRequest = new Submission("item1", "", 5, 10)

        when:
        market.placeOffer(offerRequest)

        then:
        thrown IllegalArgumentException
    }


    def "should not be able to add a bid to the market if the item id is empty"() {
        given:
        Market market = new Market()
        Submission bidRequest = new Submission("", "user1", 5, 10)

        when:
        market.placeBid(bidRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should not be able to add an offer to the market if the item id is empty"() {
        given:
        Market market = new Market()
        Submission offerRequest = new Submission("", "user1", 5, 10)

        when:
        market.placeOffer(offerRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should be able to add a valid bid to the market"() {
        given:
        Market market = new Market()
        Submission bidRequest = new Submission("item1", "user1", 5, 10)

        when:
        market.placeBid(bidRequest)

        then:
        def bids = market.getBidsForUser(bidRequest.userId)
        assertThat(bids.size(), equalTo(1))
        assertThat(bids[0].submission, equalTo(bidRequest))
    }

    def "should be able to add a valid offer to the market"() {
        given:
        Market market = new Market()
        Submission offerRequest = new Submission("item1", "user1", 5, 10)

        when:
        market.placeOffer(offerRequest)

        then:
        def offers = market.getOffersForUser(offerRequest.userId)
        assertThat(offers.size(), equalTo(1))
        assertThat(offers[0].submission, equalTo(offerRequest))
    }


    def "a placed bid should have a placement time associated with it"() {
        given:
        Market market = new Market()
        Submission bidRequest = new Submission("item1", "user1", 5, 10)

        when:
        market.placeBid(bidRequest)

        then:
        def bids = market.getBidsForUser(bidRequest.userId)
        assertThat(bids.size(), equalTo(1))
        def bid = bids[0]
        assertThat(bid.placementTime, notNullValue())
        assert(bid.placementTime.toEpochMilli() <= Instant.now().toEpochMilli())

    }

    def "a placed offer should have a placement time associated with it"() {
        given:
        Market market = new Market()
        Submission offerRequest = new Submission("item1", "user1", 5, 10)

        when:
        market.placeOffer(offerRequest)

        then:
        def offers = market.getOffersForUser(offerRequest.userId)
        assertThat(offers.size(), equalTo(1))
        def offer = offers[0]
        assertThat(offer.placementTime, notNullValue())
        assert(offer.placementTime.toEpochMilli() <= Instant.now().toEpochMilli())
    }



    def "market should return all bids for a user"() {
        given:
        Market market = new Market()

        def userId = "user1"
        Submission bidRequest1 = new Submission("item1", userId, 5, 10)
        Submission bidRequest2 = new Submission("item2", userId, 45, 100)

        when:
        market.placeBid(bidRequest1)
        market.placeBid(bidRequest2)

        then:
        def bids = market.getBidsForUser(userId).collect { it.submission }
        assertThat(bids.size(), equalTo(2))
        assertThat(bids, hasItem(bidRequest1))
        assertThat(bids, hasItem(bidRequest2))
    }

    def "market should return all offers for a user"() {
        given:
        Market market = new Market()

        def userId = "user1"
        Submission offerRequest1 = new Submission("item1", userId, 5, 10)
        Submission offerRequest2 = new Submission("item2", userId, 45, 100)

        when:
        market.placeOffer(offerRequest1)
        market.placeOffer(offerRequest2)

        then:
        def offers = market.getOffersForUser(userId).collect { it.submission }
        assertThat(offers.size(), equalTo(2))
        assertThat(offers, hasItem(offerRequest1))
        assertThat(offers, hasItem(offerRequest2))
    }


    def "market should return the current bid price for an item as the highest price of all bids for that item"() {
        given:
        Market market = new Market()

        def itemId = "item1"
        market.placeBid(new Submission(itemId, "user1", 10, 200))
        market.placeBid(new Submission(itemId, "user1", 10, 100))

        when:
        def currentBidPrice = market.getCurrentBidPrice(itemId)

        then:
        assertThat(currentBidPrice, equalTo(200))
    }

    def "market should return the current bid price for an item as zero if there are no bids for that item"() {
        given:
        Market market = new Market()

        when:
        def currentBidPrice = market.getCurrentBidPrice("nonexistentid")

        then:
        assertThat(currentBidPrice, equalTo(0))
    }

    def "market should return the current offer price for an item as the lowest price of all offers for that item"() {
        given:
        Market market = new Market()

        def itemId = "item1"
        market.placeOffer(new Submission(itemId, "user1", 10, 200))
        market.placeOffer(new Submission(itemId, "user1", 10, 100))

        when:
        def currentOfferPrice = market.getCurrentOfferPrice(itemId)

        then:
        assertThat(currentOfferPrice, equalTo(100))
    }

    def "market should return the current offer price for an item as zero if there are no offers for that item"() {
        given:
        Market market = new Market()

        when:
        def currentOfferPrice = market.getCurrentOfferPrice("nonexistentid")

        then:
        assertThat(currentOfferPrice, equalTo(0))
    }

    def "no purchase orders should be returned for a user who has placed no bids"() {
        given:
        Market market = new Market()

        when:
        def purchaseOrders = market.getPurchaseOrdersForUser("user1")

        then:
        assertThat(purchaseOrders.size(), equalTo(0))
    }

    def "no sale orders should be returned for a user who has placed no offers"() {
        given:
        Market market = new Market()

        when:
        def saleOrders = market.getSaleOrdersForUser("user1")

        then:
        assertThat(saleOrders.size(), equalTo(0))
    }


    def "no purchase orders should be returned for a user who has placed a bid but there is no matching offer"() {
        given:
        Market market = new Market()
        market.placeBid(new Submission("item1", "user1", 10, 200))

        when:
        def purchaseOrders = market.getPurchaseOrdersForUser("user1")

        then:
        assertThat(purchaseOrders.size(), equalTo(0))
    }

    def "no sale orders should be returned for a user who has placed an offer but there is no matching bid"() {
        given:
        Market market = new Market()
        market.placeOffer(new Submission("item1", "user1", 10, 200))

        when:
        def saleOrders = market.getSaleOrdersForUser("user1")

        then:
        assertThat(saleOrders.size(), equalTo(0))
    }

    def "a purchase order should be returned for a user who has placed a bid for which there is an exact matching offer"() {
        given:
        Market market = new Market()

        def bidRequest = new Submission("item1", "user1", 10, 200)
        market.placeBid(bidRequest)

        def offerRequest = new Submission("item1", "user2", 10, 200)
        market.placeOffer(offerRequest)

        when:
        def purchaseOrders = market.getPurchaseOrdersForUser("user1")

        then:
        assertThat(purchaseOrders.size(), equalTo(1))
        def order = purchaseOrders[0]
        checkOrderAgainstMatchedBidAndOffer(order, bidRequest, offerRequest)

    }

    def "a sales order should be returned for a user who has placed an offer for which there is an exact matching bid"() {
        given:
        Market market = new Market()

        def bidRequest = new Submission("item1", "user1", 10, 200)
        market.placeBid(bidRequest)

        def offerRequest = new Submission("item1", "user2", 10, 200)
        market.placeOffer(offerRequest)

        when:
        def salesOrders = market.getSaleOrdersForUser("user2")

        then:
        assertThat(salesOrders.size(), equalTo(1))
        def order = salesOrders[0]
        checkOrderAgainstMatchedBidAndOffer(order, bidRequest, offerRequest)
    }

    def "no purchase order should be returned for a user who has placed a bid for which there is an offer at a higher price"() {
        given:
        Market market = new Market()

        market.placeBid(new Submission("item1", "user1", 10, 200))
        market.placeOffer(new Submission("item1", "user2", 10, 300))

        when:
        def purchaseOrders = market.getPurchaseOrdersForUser("user1")

        then:
        assertThat(purchaseOrders.size(), equalTo(0))
    }

    def "no sales order should be returned for a user who has placed an offer for which there is a bid at a lower price"() {
        given:
        Market market = new Market()

        market.placeBid(new Submission("item1", "user1", 10, 200))
        market.placeOffer(new Submission("item1", "user2", 10, 300))

        when:
        def salesOrders = market.getSaleOrdersForUser("user2")

        then:
        assertThat(salesOrders.size(), equalTo(0))
    }

    def "a purchase order should be returned for a user who has placed a bid for which there is an offer at a lower price and the order price should be the offer price"() {
        given:
        Market market = new Market()

        def bidRequest = new Submission("item1", "user1", 10, 200)
        market.placeBid(bidRequest)

        def offerRequest = new Submission("item1", "user2", 10, 100)
        market.placeOffer(offerRequest)

        when:
        def purchaseOrders = market.getPurchaseOrdersForUser("user1")

        then:
        assertThat(purchaseOrders.size(), equalTo(1))
        def order = purchaseOrders[0]

        checkOrderAgainstMatchedBidAndOffer(order, bidRequest, offerRequest)
    }

    def "a sales order should be returned for a user who has placed an offer for which there is a bid at a higher price and the order price should be the offer price"() {
        given:
        Market market = new Market()

        def bidRequest = new Submission("item1", "user1", 10, 200)
        market.placeBid(bidRequest)

        def offerRequest = new Submission("item1", "user2", 10, 100)
        market.placeOffer(offerRequest)

        when:
        def salesOrders = market.getSaleOrdersForUser("user2")

        then:
        assertThat(salesOrders.size(), equalTo(1))
        def order = salesOrders[0]

        checkOrderAgainstMatchedBidAndOffer(order, bidRequest, offerRequest)
    }

    def "no purchase order should be returned for a user who has placed a bid for which there is an offer with a lower quantity"() {
        given:
        Market market = new Market()

        market.placeBid(new Submission("item1", "user1", 10, 200))
        market.placeOffer(new Submission("item1", "user2", 5, 200))

        when:
        def purchaseOrders = market.getPurchaseOrdersForUser("user1")

        then:
        assertThat(purchaseOrders.size(), equalTo(0))
    }

    def "no sales order should be returned for a user who has placed an offer for which there is a bid with a higher quantity"() {
        given:
        Market market = new Market()

        market.placeBid(new Submission("item1", "user1", 10, 200))
        market.placeOffer(new Submission("item1", "user2", 5, 200))

        when:
        def salesOrders = market.getSaleOrdersForUser("user1")

        then:
        assertThat(salesOrders.size(), equalTo(0))
    }

    def "a purchase order should be returned for a user who has placed a bid for which there is an offer with a higher quantity"() {
        given:
        Market market = new Market()

        def bidRequest = new Submission("item1", "user1", 10, 200)
        market.placeBid(bidRequest)

        def offerRequest = new Submission("item1", "user2", 15, 200)
        market.placeOffer(offerRequest)

        when:
        def purchaseOrders = market.getPurchaseOrdersForUser("user1")

        then:
        assertThat(purchaseOrders.size(), equalTo(1))
        def order = purchaseOrders[0]

        checkOrderAgainstMatchedBidAndOffer(order, bidRequest, offerRequest)
    }

    def "a sales order should be returned for a user who has placed an offer for which there is a bid with a lower quantity"() {
        given:
        Market market = new Market()

        def bidRequest = new Submission("item1", "user1", 10, 200)
        market.placeBid(bidRequest)

        def offerRequest = new Submission("item1", "user2", 15, 200)
        market.placeOffer(offerRequest)

        when:
        def salesOrders = market.getSaleOrdersForUser("user2")

        then:
        assertThat(salesOrders.size(), equalTo(1))
        def order = salesOrders[0]

        checkOrderAgainstMatchedBidAndOffer(order, bidRequest, offerRequest)
    }

    def "a bid should match against the first placed offer if it matches multiple offers"() {
        given:
        Market market = new Market()

        def offerRequest1 = new Submission("item1", "user2", 10, 200)
        market.placeOffer(offerRequest1)

        def offerRequest2 = new Submission("item1", "user3", 10, 100)
        market.placeOffer(offerRequest2)

        def bidRequest = new Submission("item1", "user1", 10, 300)
        market.placeBid(bidRequest)

        when:
        def purchaseOrders = market.getPurchaseOrdersForUser("user1")

        then:
        assertThat(purchaseOrders.size(), equalTo(1))
        def order = purchaseOrders[0]

        checkOrderAgainstMatchedBidAndOffer(order, bidRequest, offerRequest1)
    }

    def "an offer should match against the first placed bid if it matches multiple bids"() {
        given:
        Market market = new Market()

        def bidRequest1 = new Submission("item1", "user2", 10, 200)
        market.placeBid(bidRequest1)

        def bidRequest2 = new Submission("item1", "user3", 10, 100)
        market.placeBid(bidRequest2)

        def offerRequest = new Submission("item1", "user1", 10, 90)
        market.placeOffer(offerRequest)

        when:
        def salesOrders = market.getSaleOrdersForUser("user1")

        then:
        assertThat(salesOrders.size(), equalTo(1))
        def order = salesOrders[0]

        checkOrderAgainstMatchedBidAndOffer(order, bidRequest1, offerRequest)
    }

    def "when an order is created the bid should be removed from the market"() {
        given:
        Market market = new Market()

        def bidRequest = new Submission("item1", "user1", 10, 200)
        market.placeBid(bidRequest)

        def offerRequest = new Submission("item1", "user2", 10, 200)
        market.placeOffer(offerRequest)

        when:
        def purchaseOrders = market.getPurchaseOrdersForUser("user1")

        then:
        assertThat(purchaseOrders.size(), equalTo(1))
        def order = purchaseOrders[0]
        checkOrderAgainstMatchedBidAndOffer(order, bidRequest, offerRequest)
        assertThat(market.getBidsForUser("user1").size(), equalTo(0))
    }

    def "when an order is created the matching offer should have its quantity reduced by the quantity specified in the bid"() {
        given:
        Market market = new Market()

        def bidRequest = new Submission("item1", "user1", 10, 200)
        market.placeBid(bidRequest)

        def offerRequest = new Submission("item1", "user2", 15, 200)
        market.placeOffer(offerRequest)

        when:
        def purchaseOrders = market.getPurchaseOrdersForUser("user1")

        then:
        assertThat(purchaseOrders.size(), equalTo(1))
        def order = purchaseOrders[0]
        checkOrderAgainstMatchedBidAndOffer(order, bidRequest, offerRequest)
        def offers = market.getOffersForUser("user2")
        assertThat(offers.size(), equalTo(1))
        assertThat(offers[0].submission.quantity, equalTo(5))
    }

    def "when an order is created the matching offer should have be removed from the market if the bid quantity is equal to the offer quantity"() {
        given:
        Market market = new Market()

        def bidRequest = new Submission("item1", "user1", 15, 200)
        market.placeBid(bidRequest)

        def offerRequest = new Submission("item1", "user2", 15, 200)
        market.placeOffer(offerRequest)

        when:
        def purchaseOrders = market.getPurchaseOrdersForUser("user1")

        then:
        assertThat(purchaseOrders.size(), equalTo(1))
        def order = purchaseOrders[0]
        checkOrderAgainstMatchedBidAndOffer(order, bidRequest, offerRequest)
        def offers = market.getOffersForUser("user2")
        assertThat(offers.size(), equalTo(0))
    }

    private static void checkOrderAgainstMatchedBidAndOffer(order, bid, offer) {
        assertThat(order.itemId, equalTo(bid.itemId))
        assertThat(order.buyerId, equalTo(bid.userId))
        assertThat(order.sellerId, equalTo(offer.userId))
        assertThat(order.quantity, equalTo(bid.quantity))
        assertThat(order.pricePerUnit, equalTo(offer.pricePerUnit))
    }
}
