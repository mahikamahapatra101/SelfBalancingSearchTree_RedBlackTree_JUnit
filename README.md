# Self-Balancing Search Tree — Red-Black Tree

A game record leaderboard built on a generic Red-Black Tree in Java,
with O(log n) insertion and retrieval.

## What it does

- Reads game records from a CSV file and stores them in a Red-Black Tree
- Filter records by level range, min/max level, playtime, or result count
- Submit new records at runtime — the tree rebalances on every insertion
- All operations run in O(log n) time

## How it works

The tree uses Java generics, so it works with any `Comparable` type.
Insertions trigger rotations and recoloring as needed to keep the tree
balanced. Records are keyed by level, so range queries map directly to
tree traversals.

## Tech stack

- Java
- JUnit 5

## Tests

Covers CSV loading, insertions, left/right rotations, and all filter
combinations — including edge cases that stress the balancing logic.
