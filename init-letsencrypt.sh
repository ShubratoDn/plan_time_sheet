#!/bin/bash



domains=(managept.net www.managetp.net)

rsa_key_size=4096

data_path="./data/certbot"

email="naresh@managetp.net" # Adding a valid address is strongly recommended

staging=0 # Set to 1 if you're testing your setup to avoid hitting request limits



if [ -d "$data_path" ]; then

read -p "Existing data found for $domains. Continue and replace existing certificate? (y/N) " decision

if [ "$decision" != "Y" ] && [ "$decision" != "y" ]; then

exit

fi

fi



mkdir -p "$data_path/conf"

mkdir -p "$data_path/www"


