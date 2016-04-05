Klangmeister
============

A musical scratchpad.

[![Build Status](https://travis-ci.org/ctford/klangmeister.png)](https://travis-ci.org/ctford/klangmeister)

The TravisCI build has recently intermittently failed for what appears to be resource exhaustion e.g. memory. The solution appears to be to require sudo, perhaps because it causes the build to run on infrastructure with different resource limits. 

Playing
-------

See it in action [here](http://ctford.github.io/klangmeister/). I've been developing in Chrome. Other browsers may or may not work.

Edit the code and see and hear your changes.

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

Run `lein figwheel` for a hot-reloading development mode, or `lein cljsbuild once prod` to aggregate the javascript ready for deployment to a static fileserver.

Then browse to `resources/klangmeister/index.html`.

To run the unit tests, run `lein doo phantom test once`.

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
