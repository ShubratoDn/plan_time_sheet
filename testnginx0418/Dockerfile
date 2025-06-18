FROM nginx:latest

RUN apt-get update && apt-get install -y python3-certbot-nginx

COPY nginx.conf /etc/nginx/nginx.conf 
