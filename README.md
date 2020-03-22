# autorex

With `autorex`, you can convert an automaton back into its regular expression
string representation. The translation is based on the state-elimination
algorithm which is neatly described in *Sipser, Michael.  Introduction to the
Theory of Computation.  Vol. 2.  Boston: Thomson Course Technology, 2006,
Chapter 1 (Starting from page 70)*.  The implementation is based on the
[dk.brics](http://www.brics.dk/automaton/) automaton package.

With `dk.brics`, you can apply automata operations such as concatenation,
union, intersection, *etc.* on *input automata* to derive an *output
automaton*. Once a regular expression string has been converted into the
`dk.brics` automaton representation, `dk.brics` does not offer the functionality
to convert it back to a 
regular expression String. This package provides tooling that converts
a `dk.brics` result automaton, i.e., the automaton resulting from a sequence of
automaton operations, back to a regular expression string.

# Status

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)][licence]
[![Language](http://img.shields.io/badge/language-java-brightgreen.svg)][language]
[![Maven](https://maven-badges.herokuapp.com/maven-central/com.github.julianthome/autorex/badge.svg)][maven]
[![Linux Build Status](https://img.shields.io/travis/julianthome/autorex/master.svg?label=Linux%20build)][travis]
[![Test Coverage](https://codecov.io/gh/julianthome/autorex/branch/master/graph/badge.svg)][coverage]
[![Code Climate](https://codeclimate.com/github/julianthome/autorex/badges/issue_count.svg)][codeclimate]

[licence]: https://opensource.org/licenses/mit
[language]: https://www.java.com
[maven]: https://maven-badges.herokuapp.com/maven-central/com.github.julianthome/autorex
[travis]: https://travis-ci.org/julianthome/autorex
[codeclimate]: https://codeclimate.com/github/julianthome/autorex
[coverage]: https://codecov.io/gh/julianthome/autorex

# Maven Integration

`autorex` is available on maven central. You can integrate it by using
the following dependency in the `pom.xml` file. Note, that the maven releases
do not necessarily contain the newest changes that are available in the
repository. The maven releases are kept in sync with the tagged
[releases](https://github.com/julianthome/autorex/releases). The API
documentation for every release is available.
[here](http://www.javadoc.io/doc/com.github.julianthome/autorex). However,
the content of this documentation, in particular the code examples and usage
scenarios, is always aligned with the master branch of this repository. Hence,
it might be that the latest `autorex` features are not yet available through
the maven package.

```xml
<dependency>
    <groupId>com.github.julianthome</groupId>
    <artifactId>autorex</artifactId>
    <version>1.0</version>
</dependency>
```

# Usage

## State Elimination

`autorex` has a very simple API for state elimination
provided by the class `autorex`. The following example gives an intuition how
`autorex` can be used. After creating some `dk.brics` automata, and after 
performing a couple of operations on them, the result is stored in the `d` 
automaton. Afterwards, we can simply invoke the `getRegexFromAutomaton` method
on `d` in order to get the corresponding regular expression string.

```java
Automaton a = new RegExp("(abc)+[0-9]{1,3}[dg]*").toAutomaton();
Automaton b = new RegExp("12345678").toAutomaton();
Automaton c = new RegExp(".{0,5}").toAutomaton();
Automaton d = a.union(b).intersection(c); // output automaton

String s0 = Autorex.getRegexFromAutomaton(d); // obtain regular expression String
System.out.println("Regex String: " + s0);
```

`autorex` will give you the following regular expression string which is
equivalent to the result of the computation (the automaton `d`) in the example
above.  

```bash
(((abc[0-9])([0-9])))|((abc[0-9])((g|d)|.{0}))
```

## Automaton Transformations

`autorex` can also be used in order to transform a given automaton. At
the moment, we support the following transformation from a *source automaton* to
a *target automaton*:

- camel-case automatons that are case insensitive
- substring automatons that accept all the substring of the *source automaton*
- suffix automatons that accepts all the suffixes of a given *source automaton*

The following code snippet shows how the transformation API can be used:

```java
String s = "hello my name is Alice";
Automaton a = new RegExp(s).toAutomaton();
Automaton ccas = Autorex.getCamelCaseAutomaton(a);
Automaton substr = Autorex.getSubstringAutomaton(a);
Automaton sfx = Autorex.getSuffixAutomaton(a);
```

For more examples, please have a look at the provided test cases or at the javadoc
documentation of the class `autorex`.

# Licence

The MIT License (MIT)

Copyright (c) 2016 Julian Thome <julian.thome.de@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
of the Software, and to permit persons to whom the Software is furnished to do
so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
