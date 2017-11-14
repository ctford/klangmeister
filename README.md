Klangmeister
============

A musical scratchpad. [See it in action](http://ctford.github.io/klangmeister/).

[![Build Status](https://travis-ci.org/ctford/klangmeister.png)](https://travis-ci.org/ctford/klangmeister)

Clojurescript
-------------

Klangmeister uses bootstrapped Clojurescript compiled in the browser, so any valid Clojurescript is usable in defining music. One exception to this is importing namespaces. Because everything is evaluated in the browser, only namespaces that Klangmeister exposes can be used - you can't use your own.

Bear in mind that the synthesis functions aren't actually side effecting - they just return a synthesiser definition, so if you create two synthesisers in the body of a function, only the one you return will do anything. Same goes for the music - it just returns a note data structure.

Overtone
--------

Klangmeister is not built on [Overtone](https://overtone.github.io/), though it is heavily influenced by it.

Overtone is a Clojure API for the Supercollider synthesis server. Klangmeister uses the Web Audio API provided by browsers. This has different trade-offs. A good thing is that you can use ordinary Clojurescript at runtime, whereas Overtone can't as it needs to define its synthdefs in advance. A bad thing is that the browser isn't as tuned for high performance synthesis as Supercollider is, so Klangmeister may not be able to match the industrial strength synthesis that Overtone can.

Leipzig
-------

[Leipzig](https://github.com/ctford/leipzig) is a music theory library that I wrote to make it easier to compose melodies and use keys/chords etc. Klangmeister uses Leipzig for all of its music composition.

Anything that Leipzig offers can be used in Klangmeister, with the exception of Leipzig's `live` namespace, which provides features like `jam` that are built on top of Overtone.

[Documentation for Leipzig](http://ctford.github.io/leipzig/) is also available. The only difference with how Leipzig works in
Klangmeister is that notes have an `:instrument` key, rather than relying on the `play-note` multimethod as with Overtone Leipzig
examples.

CLJS Bach
---------

[CLJS Bach](https://github.com/ctford/cljs-bach) provides the synthesis capability for Klangmeister. It was originally just
a namespace within Klangmeister, but has since been extracted.

Building
--------

### Figwheel
Run `lein figwheel` for a hot-reloading development mode.

Then browse to [Figwheel's local server](http://localhost:3449/klangmeister/index.html).

Currently there is an error which appears only with Figwheel - `Namespace "cljs_bach.synthesis" already declared`. Make any change in the code window to trigger a new compilation and it will vanish.

### Production build

This is the method used to build files for pushing to Github pages:

    ./build
    cd resources/public/
    python -m SimpleHTTPServer 8000

Then browse to [SimpleHTTPServer's local server](http://localhost:8000/klangmeister/index.html).

### Tests
To run the unit tests, run `./test.sh`.

Goals
-----
My aims for Klangmeister are:
* Zero-install live-coding
* Accessible synth design
* Music education
* Performance-readiness

There are many other excellent live-coding platforms, like [Sonic Pi](http://sonic-pi.net/), [Overtone](https://overtone.github.io/) and [Extempore](http://extempore.moso.com.au/). The main thing that differentiates Klangmeister is that as it's in-browser, it's really easy to get going quickly.

References
----------
Klangmeister is based on a talk called [Functional Composition](https://www.youtube.com/watch?v=Mfsnlbd-4xQ).

It is built with [Clojurescript](https://github.com/clojure/clojurescript), the [Web Audio API](https://www.w3.org/TR/webaudio/),
the [React](https://facebook.github.io/react/) web framework [Reagent](https://github.com/reagent-project/reagent)
and the music theory library [Leipzig](https://github.com/ctford/leipzig).
