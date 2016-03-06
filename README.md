Klangmeister
============

A musical scratchpad.

[![Build Status](https://travis-ci.org/ctford/klangmeister.png)](https://travis-ci.org/ctford/klangmeister)

The TravisCI build has recently intermittently failed for what appears to be resource exhaustion e.g. memory. The solution appears to be to require sudo, perhaps because it causes the build to run on infrastructure with different resource limits. 

Playing
-------

See it in action [here](http://ctford.github.io/klangmeister/). I've been developing in Chrome. Other browsers may or may not work.

Edit the code and see and hear your changes.

Building
--------

Run `lein figwheel` for a hot-reloading development mode, or `lein cljsbuild once prod` to aggregate the javascript ready for deployment to a static fileserver.

Then browse to `resources/index.html`.

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
Klangmeister is based on a talk called [Functional Composition](https://www.youtube.com/watch?v=Mfsnlbd-4xQ) and a music theory library called [Leipzig](https://github.com/ctford/leipzig).

It is built on [Clojurescript](https://github.com/clojure/clojurescript) and the [Web Audio API](https://www.w3.org/TR/webaudio/).
