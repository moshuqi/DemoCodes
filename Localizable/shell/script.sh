#!/bin/bash
echo "Run localizable script ..."
# python localizable.py 

basepath=$(cd `dirname $0`; pwd)
echo $basepath 
python --version