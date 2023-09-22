# Principles of Programming: Assignment 01

## Objective

Implement the functions in `src/main/scala/Main.scala`, which are currently left blank as `???`

**WARNING:Please read the restrictions below carefully.**

If you do not follow these, **your submission will not be graded.**

- Do not use the keyword `var`. Use `val` and `def` instead.
- Do not use any library functions or data structures like `List`, `Array`, `Range` (`1 to n`, `1 until n` ...), `fold`, `map`, `reduce` or etc.
- You can only use tuples, `scala.annotation.tailrec`, and `scala.util.control.TailCalls._`, `Math._` from the library.
- Do not use any looping syntax of Scala (`for`, `while`, `do-while`, `yield`, ...)

Again, your score will be zero if you do not follow these rules.

Note that these rules will be gradually relaxed through the next
assignments.

For three problems, 50% of the test cases will require tail call optimizations (i.e., large inputs)
and the other 50% will not (i.e., small inputs).

So, you will get at least 50% of the score if you submit a correct program without tail call optimization. (30sec timeout)

## How to test your code

```bash
sbt test # compile and run TestSuite.scala
```

If `sbt test` does not work, then your code is wrong.

Fix your code until it works, or send a question to the TA.

## How to Submit

- For all assignments, we will test your code only with the given `Main.scala` files in `src/main/scala` directory. **All the additional files you've created will be lost!!**
- We will check git logs of the main branch, and grade the latest commit before the due date.

```bash
git add src
git commit -m 'Write some meaningful message'
git push
```

## Due date

- 2023/10/3 23:59:59 KST

## Errata

- Branch `errata` is protected. If any correction would be appeared on the code, I will update `errata` branch.
- If you find any ambiguity from the description, write an issue to the GitHub issue tracker.
  - https://github.com/snu-sf-class/pp202302/issues
