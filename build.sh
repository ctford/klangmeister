#!/bin/sh
lein clean
lein cljsbuild once prod
