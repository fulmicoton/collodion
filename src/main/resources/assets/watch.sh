#!/bin/bash
cd js/ && watchify  -r -t coffee-reactify main.coffee  -o bundle.js
