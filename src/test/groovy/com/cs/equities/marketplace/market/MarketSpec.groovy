package com.cs.equities.marketplace.market

import com.cs.equities.marketplace.data.PlacementRequest

import java.time.Instant

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.core.IsCollectionContaining.hasItem
import static org.hamcrest.core.IsCollectionContaining.hasItem
import static org.hamcrest.core.IsEqual.equalTo
import static org.hamcrest.core.IsNull.notNullValue

class MarketSpec extends spock.lang.Specification {

    def "should not be able to add a bid to the market if the quantity is not greater than zero"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest bidRequest = new PlacementRequest("item1", "user1", 0, 5)

        when:
        market.placeBid(bidRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should not be able to add an offer to the market if the quantity is not greater than zero"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest offerRequest = new PlacementRequest("item1", "user1", 0, 5)

        when:
        market.placeOffer(offerRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should not be able to add a bid to the market if the price is less than zero"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest bidRequest = new PlacementRequest("item1", "user1", 5, -1)

        when:
        market.placeBid(bidRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should not be able to add an offer to the market if the price is less than zero"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest offerRequest = new PlacementRequest("item1", "user1", 5, -1)

        when:
        market.placeOffer(offerRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should not be able to add a bid to the market if the user id is empty"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest bidRequest = new PlacementRequest("item1", "", 5, 10)

        when:
        market.placeBid(bidRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should not be able to add an offer to the market if the user id is empty"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest offerRequest = new PlacementRequest("item1", "", 5, 10)

        when:
        market.placeOffer(offerRequest)

        then:
        thrown IllegalArgumentException
    }


    def "should not be able to add a bid to the market if the item id is empty"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest bidRequest = new PlacementRequest("", "user1", 5, 10)

        when:
        market.placeBid(bidRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should not be able to add an offer to the market if the item id is empty"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest offerRequest = new PlacementRequest("", "user1", 5, 10)

        when:
        market.placeOffer(offerRequest)

        then:
        thrown IllegalArgumentException
    }

    def "should be able to add a valid bid to the market"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest bidRequest = new PlacementRequest("item1", "user1", 5, 10)

        when:
        market.placeBid(bidRequest)

        then:
        def bids = market.getBidsForUser(bidRequest.userId)
        assertThat(bids.size(), equalTo(1))
        assertThat(bids[0].placementRequest, equalTo(bidRequest))
    }

    def "should be able to add a valid offer to the market"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest offerRequest = new PlacementRequest("item1", "user1", 5, 10)

        when:
        market.placeOffer(offerRequest)

        then:
        def offers = market.getOffersForUser(offerRequest.userId)
        assertThat(offers.size(), equalTo(1))
        assertThat(offers[0].placementRequest, equalTo(offerRequest))
    }


    def "a placed bid should have a placement time associated with it"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest bidRequest = new PlacementRequest("item1", "user1", 5, 10)

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
        Market market = new Market(new MarketState())
        PlacementRequest offerRequest = new PlacementRequest("item1", "user1", 5, 10)

        when:
        market.placeOffer(offerRequest)

        then:
        def offers = market.getOffersForUser(offerRequest.userId)
        assertThat(offers.size(), equalTo(1))
        def offer = offers[0]
        assertThat(offer.placementTime, notNullValue())
        assert(offer.placementTime.toEpochMilli() <= Instant.now().toEpochMilli())
    }

    def "multiple bids should have a placement time that reflects the serial order in which they were added"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest bidRequest1 = new PlacementRequest("item1", "user1", 5, 10)
        PlacementRequest bidRequest2 = new PlacementRequest("item1", "user1", 10, 10)
        PlacementRequest bidRequest3 = new PlacementRequest("item1", "user1", 15, 10)

        when:
        market.placeBid(bidRequest1)
        Thread.sleep(500)
        market.placeBid(bidRequest2)
        Thread.sleep(500)
        market.placeBid(bidRequest3)

        then:
        def bids = market.getBidsForUser(bidRequest1.userId)
        assertThat(bids.size(), equalTo(3))
        def bid1 = bids.find { bidRequest1.equals(it.placementRequest) }
        def bid2 = bids.find { bidRequest2.equals(it.placementRequest) }
        def bid3 = bids.find { bidRequest3.equals(it.placementRequest) }

        assert(bid1.placementTime.isBefore(bid2.placementTime))
        assert(bid2.placementTime.isBefore(bid3.placementTime))
    }


    def "multiple offers should have a placement time that reflects the serial order in which they were added"() {
        given:
        Market market = new Market(new MarketState())
        PlacementRequest offerRequest1 = new PlacementRequest("item1", "user1", 5, 10)
        PlacementRequest offerRequest2 = new PlacementRequest("item1", "user1", 10, 10)
        PlacementRequest offerRequest3 = new PlacementRequest("item1", "user1", 15, 10)

        when:
        market.placeOffer(offerRequest1)
        Thread.sleep(500)
        market.placeOffer(offerRequest2)
        Thread.sleep(500)
        market.placeOffer(offerRequest3)

        then:
        def offers = market.getOffersForUser(offerRequest1.userId)
        assertThat(offers.size(), equalTo(3))
        def offer1 = offers.find { offerRequest1.equals(it.placementRequest) }
        def offer2 = offers.find { offerRequest2.equals(it.placementRequest) }
        def offer3 = offers.find { offerRequest3.equals(it.placementRequest) }

        assert(offer1.placementTime.isBefore(offer2.placementTime))
        assert(offer2.placementTime.isBefore(offer3.placementTime))
    }


    def "market should return all bids for a user"() {
        given:
        Market market = new Market(new MarketState())

        def userId = "user1"
        PlacementRequest bidRequest1 = new PlacementRequest("item1", userId, 5, 10)
        PlacementRequest bidRequest2 = new PlacementRequest("item2", userId, 45, 100)

        when:
        market.placeBid(bidRequest1)
        market.placeBid(bidRequest2)

        then:
        def bids = market.getBidsForUser(userId).collect { it.placementRequest }
        assertThat(bids.size(), equalTo(2))
        assertThat(bids, hasItem(bidRequest1))
        assertThat(bids, hasItem(bidRequest2))
    }

    def "market should return all offers for a user"() {
        given:
        Market market = new Market(new MarketState())

        def userId = "user1"
        PlacementRequest offerRequest1 = new PlacementRequest("item1", userId, 5, 10)
        PlacementRequest offerRequest2 = new PlacementRequest("item2", userId, 45, 100)

        when:
        market.placeOffer(offerRequest1)
        market.placeOffer(offerRequest2)

        then:
        def offers = market.getOffersForUser(userId).collect { it.placementRequest }
        assertThat(offers.size(), equalTo(2))
        assertThat(offers, hasItem(offerRequest1))
        assertThat(offers, hasItem(offerRequest2))
    }


    def "market should return the current bid price for an item as the highest price of all bids for that item"() {
        given:
        Market market = new Market(new MarketState())

        def itemId = "item1"
        market.placeBid(new PlacementRequest(itemId, "user1", 10, 200))
        market.placeBid(new PlacementRequest(itemId, "user1", 10, 100))

        when:
        def currentBidPrice = market.getCurrentBidPrice(itemId)

        then:
        assertThat(currentBidPrice, equalTo(200))
    }

    def "market should return the current bid price for an item as zero if there are no bids for that item"() {
        given:
        Market market = new Market(new MarketState())

        when:
        def currentBidPrice = market.getCurrentBidPrice("nonexistentid")

        then:
        assertThat(currentBidPrice, equalTo(0))
    }

    def "market should return the current offer price for an item as the lowest price of all offers for that item"() {
        given:
        Market market = new Market(new MarketState())

        def itemId = "item1"
        market.placeOffer(new PlacementRequest(itemId, "user1", 10, 200))
        market.placeOffer(new PlacementRequest(itemId, "user1", 10, 100))

        when:
        def currentOfferPrice = market.getCurrentOfferPrice(itemId)

        then:
        assertThat(currentOfferPrice, equalTo(100))
    }

    def "market should return the current offer price for an item as zero if there are no offers for that item"() {
        given:
        Market market = new Market(new MarketState())

        when:
        def currentOfferPrice = market.getCurrentOfferPrice("nonexistentid")

        then:
        assertThat(currentOfferPrice, equalTo(0))
    }


}
