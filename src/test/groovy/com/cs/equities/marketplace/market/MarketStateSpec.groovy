package com.cs.equities.marketplace.market

import com.cs.equities.marketplace.data.PlacementRequest

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.core.IsEqual.equalTo
import static org.hamcrest.core.IsCollectionContaining.hasItem

class MarketStateSpec extends spock.lang.Specification {

    def "should be able to add a bid to the market state"() {
        given:
        MarketState marketState = new MarketState()
        PlacementRequest bidRequest = new PlacementRequest("item1", "user1", 10, 5)

        when:
        marketState.addBid(bidRequest)

        then:
        def bids = marketState.getBidsForItem(bidRequest.itemId)
        assertThat(bids.size(), equalTo(1))
        assertThat(bids[0].originalRequest, equalTo(bidRequest))
    }

    def "should be able to add an offer to the market state"() {
        given:
        MarketState marketState = new MarketState()
        PlacementRequest offerRequest = new PlacementRequest("item1", "user1", 10, 5)

        when:
        marketState.addOffer(offerRequest)

        then:
        def offers = marketState.getOffersForItem(offerRequest.itemId)
        assertThat(offers.size(), equalTo(1))
        assertThat(offers[0].originalRequest, equalTo(offerRequest))
    }

    def "adding a bid to the market state should register that bid against the user specified"() {
        given:
        MarketState marketState = new MarketState()
        PlacementRequest bidRequest = new PlacementRequest("item1", "user1", 10, 5)

        when:
        marketState.addBid(bidRequest)

        then:
        def bids = marketState.getBidsForUser(bidRequest.userId)
        assertThat(bids.size(), equalTo(1))
        assertThat(bids[0].originalRequest, equalTo(bidRequest))
    }

    def "adding an offer to the market state should register that offer against the user specified"() {
        given:
        MarketState marketState = new MarketState()
        PlacementRequest offerRequest = new PlacementRequest("item1", "user1", 10, 5)

        when:
        marketState.addOffer(offerRequest)

        then:
        def offers = marketState.getOffersForUser(offerRequest.userId)
        assertThat(offers.size(), equalTo(1))
        assertThat(offers[0].originalRequest, equalTo(offerRequest))
    }


    def "adding a bid to the market state should register that bid against the item specified"() {
        given:
        MarketState marketState = new MarketState()
        PlacementRequest bidRequest = new PlacementRequest("item1", "user1", 10, 5)

        when:
        marketState.addBid(bidRequest)

        then:
        def bids = marketState.getBidsForItem(bidRequest.itemId)
        assertThat(bids.size(), equalTo(1))
        assertThat(bids[0].originalRequest, equalTo(bidRequest))
    }

    def "adding an offer to the market state should register that offer against the item specified"() {
        given:
        MarketState marketState = new MarketState()
        PlacementRequest offerRequest = new PlacementRequest("item1", "user1", 10, 5)

        when:
        marketState.addOffer(offerRequest)

        then:
        def offers = marketState.getOffersForItem(offerRequest.itemId)
        assertThat(offers.size(), equalTo(1))
        assertThat(offers[0].originalRequest, equalTo(offerRequest))
    }

    def "should be able to add multiple bids to the market state for the same item id"() {
        given:
        MarketState marketState = new MarketState()

        def itemId = "item1"
        PlacementRequest bidRequest1 = new PlacementRequest(itemId, "user1", 10, 5)
        PlacementRequest bidRequest2 = new PlacementRequest(itemId, "user2", 10, 5)

        when:
        marketState.addBid(bidRequest1)
        marketState.addBid(bidRequest2)

        then:
        def bids = marketState.getBidsForItem(itemId).collect { it.originalRequest }
        assertThat(bids.size(), equalTo(2))
        assertThat(bids, hasItem(bidRequest1))
        assertThat(bids, hasItem(bidRequest2))
    }

    def "should be able to add multiple bids to the market state for the same user id"() {
        given:
        MarketState marketState = new MarketState()

        def userId = "user1"
        PlacementRequest bidRequest1 = new PlacementRequest("item1", userId, 10, 5)
        PlacementRequest bidRequest2 = new PlacementRequest("item2", userId, 10, 5)

        when:
        marketState.addBid(bidRequest1)
        marketState.addBid(bidRequest2)

        then:
        def bids = marketState.getBidsForUser(userId).collect { it.originalRequest }
        assertThat(bids.size(), equalTo(2))
        assertThat(bids, hasItem(bidRequest1))
        assertThat(bids, hasItem(bidRequest2))
    }

    def "should be able to add multiple offers to the market state for the same item id"() {
        given:
        MarketState marketState = new MarketState()

        def itemId = "item1"
        PlacementRequest offerRequest1 = new PlacementRequest(itemId, "user1", 10, 5)
        PlacementRequest offerRequest2 = new PlacementRequest(itemId, "user2", 10, 5)

        when:
        marketState.addOffer(offerRequest1)
        marketState.addOffer(offerRequest2)

        then:
        def offers = marketState.getOffersForItem(itemId).collect { it.originalRequest }
        assertThat(offers.size(), equalTo(2))
        assertThat(offers, hasItem(offerRequest1))
        assertThat(offers, hasItem(offerRequest2))
    }

    def "should be able to add multiple offers to the market state for the same user id"() {
        given:
        MarketState marketState = new MarketState()

        def userId = "user1"
        PlacementRequest offerRequest1 = new PlacementRequest("item1", userId, 10, 5)
        PlacementRequest offerRequest2 = new PlacementRequest("item2", userId, 10, 5)

        when:
        marketState.addOffer(offerRequest1)
        marketState.addOffer(offerRequest2)

        then:
        def offers = marketState.getOffersForUser(userId).collect { it.originalRequest }
        assertThat(offers.size(), equalTo(2))
        assertThat(offers, hasItem(offerRequest1))
        assertThat(offers, hasItem(offerRequest2))
    }

}
