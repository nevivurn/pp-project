# Principles of Programming Assignment 03

## Objective

### Cellular Automaton

Implement the functions in `src/main/scala/cellular`, which are currently left blank as `???`

## Restrictions

**WARNING:Please read the restrictions below carefully.**

If you do not follow these, **your submission will not be graded.**

- **DO NOT FIX THE FILES WITHOUT EXPLICIT INSTRUCTIONS**
  - You can **ONLY** modify the functions with `???` in the files below:
    - `Elementary.scala`
    - `GameOfLife.scala`
    - `WireWorld.scala`
  - Your code will not be compiled if you modify any of the codes other than ???.
- Do not use the keyword `var` (except we've already given). Use `val` and `def` instead.
- Do not use mutable data structures.
  - You can see useful data structures from this link: [Scala 3 Collections](https://docs.scala-lang.org/scala3/book/collections-classes.html)
  - From the above link, you are forbidden to use the classes in `scala.collection.mutable`.
- Do not use `while`.

Again, your score will be zero if you do not follow these rules.

Note that these rules will be gradually relaxed through the assignments.

We do not require tail-recursion explicitly for this assignment.

Timeout: 30 sec.

## How to test your code

```bash
sbt
# From sbt console, type the below keyword. This will compile your code every time you fix the code.
~fastLinkJS
# open index.html. Click some of the cells to fix the states, and press `Step` button to progress the automaton.
```

We will offer test cases (`TestSuite.scala`) at 11/26.

## How to Submit

- For all assignments, we will test your code only with the given files in `src/main/scala` directory.
- **All the additional files you've created will be lost!!**
- We will check git logs of the main branch, and grade the latest commit before the due date.

```bash
git add src
git commit -m 'Write some meaningful message'
git push
```

## Due date

- 2023/12/7 23:59:59 KST

## Errata

- Branch `errata` is protected. If any correction would be appeared on the code, I will update `errata` branch.
- If you find any ambiguity from the description, write an issue to the GitHub issue tracker.
  - https://github.com/snu-sf-class/pp202302/issues
