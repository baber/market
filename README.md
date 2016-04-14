**Building and Running**

Just run ./gradlew clean build to build the code and run the tests.

**Concurrency**

Concurrency has been handled by synchronizing changes to the market state (adding bids or offers).  Reads simply get a snapshot of the state.  This is rather unsatisfactory
and like most lock based implementations totally susceptible to problems.  An optimistic concurrency control solution (like an STM) is a much better fit.

**Logging**

No logging has been added.

**Internationlization**

There is no support for internationalization other than holding monetary values as integers representing the smallest units (e.g. pence or cents) and using time instants.

**Testing**

The spec for the Market class is rather large and could do with refactoring.  The bid/offer sides are identical for many acceptance criteria so perhaps do not need to be tested
separately.  I tend to consolidate tests later rather than sooner.

