# Smart Parking Lot

## Overview
This project models a simple parking-lot domain with vehicles, parking spots, customers, tickets, and pricing rules. The main goal is to compute a valid parking charge for a session while keeping the model easy to reason about, test, and extend.

The implementation is intentionally domain-driven: a `ParkingLot` manages inventory and active sessions, while pricing rules are evaluated by `PricingPolicy` and resolved by `PriceCalculator`.

## Assumptions

### 1. A ticket represents the source of truth for a parking session
Each ticket contains the customer, vehicle, parking spot, entry time, and exit time. The lot uses the ticket as the authoritative record for billing and occupancy state.

### 2. Parking duration is measured in real time, not in declared hours
A session is valid only when the exit time is not before the entry time. If the duration is invalid, pricing logic returns an invalid value rather than a misleading zero value.

### 3. Invalid or impossible pricing scenarios return `-1.0`
This project follows the requirement that invalid pricing scenarios should return `-1` instead of `0`. Examples include:
- missing ticket
- null or invalid time data
- exit before entry
- pricing policy not applicable for the given window
- no valid policy for a stay

### 4. Lot inventory is type-based and spot-aware
The project distinguishes between compact and large spaces. Parking is only valid when a vehicle matches the required spot type.

### 5. Loyalty discounts are optional and applied only when the customer carries a loyalty tier
If no loyalty tier exists for a customer, the base fee is used without a discount.

### 6. The pricing engine chooses the minimum valid price among applicable policies
The project’s design does not assume one policy always wins. Instead, all valid policy outcomes are evaluated and the lowest price is selected.

## Design decisions

### Domain model structure
The project is split into a small set of focused entity classes:
- `Vehicle`
- `ParkingSpot`
- `Ticket`
- `Customer`
- `PricingPolicy`
- `ParkingLot`
- `PriceCalculator`

This keeps responsibilities separate:
- `ParkingLot` handles occupancy and ticket lifecycle
- `PricingPolicy` encodes price rules
- `PriceCalculator` decides the best valid price
- entities store the domain data

### Policy-based pricing
The pricing strategy is modeled as a policy object rather than hard-coded conditions inside one large method. This makes it easier to:
- add new rules
- test each rule independently
- compare policies without mutating the lot state

### Policy comparison via minimum valid price
The final price is not chosen by a fixed precedence order. Instead, each valid price candidate is considered and the smallest one is selected. This makes the pricing engine flexible and avoids hidden rule priority assumptions.

### Explicit time inputs for park/exit operations
The design exposes overloads for both `park` and `exit` with explicit time parameters. Default convenience methods use the current time, but callers can supply precise timestamps for testing and real business scenarios.

This was an intentional decision to avoid hidden clock-driven behavior and make session pricing reproducible.


### JaCoCo threshold
The project enforces a JaCoCo line coverage gate to ensure the project remains testable and maintainable. The required threshold was set to 90% to reflect the project’s actual coverage while still preserving a meaningful quality bar.

## Trade-offs and decisions

### 1. Simple in-memory model instead of persistent storage
The project intentionally uses in-memory collections for lot inventory and active tickets. This keeps the code small and easy to execute in a classroom or interview-style environment.

Trade-off:
- easier to understand and test
- not suitable for production-level persistence, concurrency, or multi-instance deployment

### 2. Singleton `PriceCalculator`
A singleton was chosen to centralize the pricing logic and avoid repeated object creation.

Trade-off:
- simpler access pattern in the current design
- less flexible if multiple independent pricing engines or configurations are needed later

Alternative considered:
- pass a calculator instance into the lot or ticket
- create a new `PriceCalculator` for each computation

The singleton was chosen because the project is small and needs a lightweight, centralized rule engine.

### 3. Fixed peak windows and flat fees
The pricing model uses fixed peak-hour windows and static fee assumptions (for example, early-bird and night-owl thresholds).

Trade-off:
- predictable and easy to validate
- not configurable from external data or user settings

Alternative considered:
- external configuration files
- database-driven pricing rules
- dynamic policy registration

The fixed model was chosen because the project scope is bounded and the requirements are explicit.

### 4. Rule evaluation by minimum valid price
The project evaluates all applicable policies and selects the minimum valid amount instead of enforcing a strict priority list.

Trade-off:
- fair and easy to explain
- may have subtle rule interactions if policies overlap in future updates

Alternative considered:
- fixed precedence such as early-bird > night-owl > standard
- one policy per vehicle class

The chosen design preserves the requirement: “select the one yielding the minimum derived amount.”

### 5. Return `-1.0` for invalid pricing instead of throwing exceptions
This is a deliberate business-rule decision to keep the API practical for callers that need a numeric result.

Trade-off:
- easy to handle in calling code
- can hide some invalid conditions if callers do not check the result carefully

Alternative considered:
- throwing exceptions for every invalid case
- returning `0.0`

The project explicitly chose `-1.0` to satisfy the requirement and signal an impossible or invalid result without interpreting it as a real charge.

## Alternatives considered

### Alternative: per-policy precedence instead of minimum-price selection
A strict rule order could have been implemented, such as “Early Bird wins before Night Owl before Standard.”

Reason not chosen:
- it adds implicit business priority rules not specified by the requirements
- it is less transparent than comparing all valid results

### Alternative: no explicit time parameters in lot methods
The methods could have always used `LocalDateTime.now()` internally.

Reason not chosen:
- it weakens test control
- it makes historical or scenario-driven pricing impossible to reproduce consistently
- it breaks deterministic validation in automated tests

### Alternative: throw exceptions for invalid pricing conditions
This would be a common Java practice but conflicts with the requirement to return `-1` in invalid scenarios.

Reason not chosen:
- the domain requirement is explicit
- returning a numeric sentinel is simpler for price consumers

### Alternative: database- or configuration-driven pricing rules
This would be a more scalable production design.

Reason not chosen:
- the project scope is a compact domain model, not a production pricing platform
- the requirements are fixed and local

## Practical notes

### Local Maven setup
The project was built with a local Maven installation because the default environment did not provide a working Homebrew-based setup. The repo depends on Maven and JUnit 5, and the build is validated through the normal Maven lifecycle.

### Test philosophy
The project emphasizes real behavior over mock-heavy tests. Tests exercise real objects and real price calculations instead of asserting on synthetic doubles or shallow mocks.

## Summary
This project is intentionally small, explicit, and rule-driven. It balances simplicity with testability by using clear domain entities, explicit timestamps, a single pricing selection rule (“minimum valid result”), and a transparent invalid-result policy. The design favors readability and scenario validation over enterprise complexity, which fits the problem’s scope and the requirement set.
