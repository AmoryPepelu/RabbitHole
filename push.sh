#!/bin/bash
date_str=`date +"%Y-%m-%d %H:%M:%S"`
git add .
git commit -m "$date_str"
git push

chmod a+x push.sh