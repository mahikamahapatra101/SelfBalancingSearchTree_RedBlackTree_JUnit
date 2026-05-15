# Self-Balancing Search Tree — Red-Black Tree

A generic Red-Black Tree in Java powering a game record leaderboard —
filtering, ranking, and retrieval all in O(log n).

## What it does

- **Leaderboard from CSV data** — loads game records from a CSV file and
  stores them in a Red-Black Tree for efficient ranked access
- **Rich filtering** — filter by level range, min/max level, playtime,
  or number of records returned
- **Live record submission** — insert new game records at runtime; the
  tree rebalances automatically to maintain sorted order
- **O(log n) performance** — all insertion, search, and retrieval
  operations are guaranteed logarithmic via strict RBT invariants

## How it works

Uses Java generics so the tree can store any `Comparable` type. During
insertion, the tree automatically applies rotations and recoloring to
keep depth balanced. Game records are keyed by level, making range
queries a natural tree traversal.

## Tech stack

- Java
- JUnit 5

## Testing

JUnit 5 tests cover CSV ingestion, single/bulk insertions, left/right
rotations, and all filter combinations — verifying both correctness and
tree structure invariants after each operation.
