S3 Bucket Gedöns

Setup
mkdir ~/.aws
cd ~/.aws
touch credentials
touch config

in credentials
[default]
aws_access_key_id = ACCESS_KEY
aws_secret_access_key = SECRET_ACCESS_KEY

in config
[default]
region = eu-west-1