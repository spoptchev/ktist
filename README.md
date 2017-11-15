# Scientist [![Build Status](https://travis-ci.org/spoptchev/scientist.svg?branch=master)](https://travis-ci.org/spoptchev/scientist)

A kotlin library for refactoring code. Port of GitHub's scientist.

```
val result = scientist<Int, Unit> {
    context {}
} conduct {
    experiment { "experiment-name" }
    control("control") { 1 }
    candidate("candidate") { 2 }
}
```
