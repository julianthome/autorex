# autorex

With `autorex`, one can convert an automaton back into its
regular expression representation. The translation is
based on the well-known state-elimination algorithm. One
can find a very neat description of this algorithm
in the textbook *Sipser, Michael. 
Introduction to the Theory of Computation. 
Vol. 2. Boston: Thomson Course Technology, 2006, Chapter
1 (Starting from page 70)*. The implementation is based on the
[dk.brics](http://www.brics.dk/automaton/) automaton package.
 
With `dk.brics`, one can apply automaton operations such as
concatenation, union, intersection, *etc.* on *input automata* to
derive an *output automaton*. However, the automaton representation used by `dk.brics`
might not be compatible with the one of another API. If you would like to 
use the result of a `dk.brics` computation together with
different API that is using another automaton representation than
`dk.brics`, you can use `autorex` for the
purpose of translating the *output automaton* into a simple regular expression string,
the most generic representation of a regular expression,
that can then be used this across different APIs.

# Status
[![Build Status](https://travis-ci.org/julianthome/autorex.svg?branch=master)](https://travis-ci.org/julianthome/autorex.svg?branch=master)  [![codecov](https://codecov.io/gh/julianthome/autorex/branch/master/graph/badge.svg)](https://codecov.io/gh/julianthome/autorex)  

# Usage

## State Elimination

`autorex` has a very simple API for state elimination 
provided by the class `Autorex`:

```java
Automaton a = new RegExp("[a-z]{1,3}test[0-9]+").toAutomaton();
String regex = Autorex.getRegexFromAutomaton(a);
System.out.println(regex.toString());
assert(new RegExp(regex).toAutomaton().equals(a));
```
This example would yield the following output which is equivalent to 
`[a-z]{1,3}test[0-9]+`:

```bash
(((([a-z]t))((([f-s]|[u-z]|[a-d])(((t(es))(t[0-9]))([0-9])*.{0})|(((t(((t(es)|(es))|(es))(t[0-9]))|((((et)(es)|(es))|(es))(t[0-9])))|((((et)(es)|(es))|(es))(t[0-9])))([0-9])*.{0}))|(((t(((t(es)|(es))|(es))(t[0-9]))|((((et)(es)|(es))|(es))(t[0-9])))|((((et)(es)|(es))|(es))(t[0-9])))([0-9])*.{0}))|((((([a-z]([u-z]|[a-s]))([u-z]|[a-s])))(((t(es))(t[0-9]))([0-9])*.{0})|((((([a-z]([u-z]|[a-s]))t))(((t(es)|(es))|(es))(t[0-9])))([0-9])*.{0}))|((((([a-z]([u-z]|[a-s]))t))(((t(es)|(es))|(es))(t[0-9])))([0-9])*.{0})))|((((([a-z]([u-z]|[a-s]))([u-z]|[a-s])))(((t(es))(t[0-9]))([0-9])*.{0})|((((([a-z]([u-z]|[a-s]))t))(((t(es)|(es))|(es))(t[0-9])))([0-9])*.{0}))|((((([a-z]([u-z]|[a-s]))t))(((t(es)|(es))|(es))(t[0-9])))([0-9])*.{0})))
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
Automaton substr = Autorex.getSubstringAutomaton(a);
Automaton sfx = Autorex.getSuffixAutomaton(a);
Automaton ccas = Autorex.getCamelCaseAutomaton(a);

```

For more examples, please have a look at the provided test cases or at the javadoc
documentation of the class `Autorex`.

# Licence
Licensed under the EUPL, Version 1.1 or – as soon they will be approved by the European Commission - subsequent versions of the EUPL (the "Licence"); You may not use this work except in compliance with the Licence. You may obtain a copy of the Licence at: https://joinup.ec.europa.eu/sites/default/files/eupl1.1.-licence-en_0.pdf

Unless required by applicable law or agreed to in writing, software distributed under the Licence is distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the Licence for the specific language governing permissions and limitations under the Licence.
