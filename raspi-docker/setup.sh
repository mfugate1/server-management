#!/bin/bash

# This enables memory stats in docker that can be monitored in home assistant
echo "$(cat /boot/cmdline.txt | tr '\n' ' ')cgroup_enable=cpuset cgroup_memory=1 cgroup_enable=memory" > /boot/cmdline.txt